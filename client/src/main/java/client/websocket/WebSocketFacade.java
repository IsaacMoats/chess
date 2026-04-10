package client.websocket;
import com.google.gson.Gson;
import exception.DataAccessException;
import jakarta.websocket.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws DataAccessException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                    switch (notification.getServerMessageType()) {
                        case LOAD_GAME -> notificationHandler.loadGame(
                                new Gson().fromJson(message, LoadGameMessage.class));
                        case NOTIFICATION -> notificationHandler.notification(
                                new Gson().fromJson(message, NotificationMessage.class));
                        case ERROR -> notificationHandler.error(
                                new Gson().fromJson(message, ErrorMessage.class));
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new DataAccessException(ex.getMessage(), 400);
        }
    }
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void enterGame(String authToken, int gameID) throws DataAccessException {
        try {
            UserGameCommand userGameCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
        } catch (IOException ex) {
            throw new DataAccessException(ex.getMessage(), 400);
        }
    }


    public void sendUserCommand(UserGameCommand userGameCommand) throws IOException {
        session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
    }
}
