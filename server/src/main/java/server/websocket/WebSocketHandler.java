package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.DataAccessException;
import io.javalin.websocket.*;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import service.UserService;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.sql.SQLException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private final UserService userService = new UserService();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) throws Exception {
        Session session = ctx.session;
        try {
            UserGameCommand userGameCommand = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            Integer gameID = userGameCommand.getGameID();
            // figure out how to get the username from the given authToken
            String authToken = userGameCommand.getAuthToken();
            String username = userService.getUser(authToken);
            connections.add(gameID, session);
            switch (userGameCommand.getCommandType()) {
                case CONNECT -> connect(session, username, userGameCommand, gameID);
                case LEAVE -> leave(userGameCommand.getGameID(), userGameCommand.getAuthToken(), ctx.session);
            }
        } catch (Exception ex) {
            System.out.println("Bad");
        }

    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) throws Exception {

    }

    public void connect(Session session, String username, UserGameCommand userGameCommand, int gameID) throws IOException, SQLException, DataAccessException {
        // Figure out how to authenticate and get username
        String message = String.format("%s has joined the game", username);
        ChessGame game = userService.getGame(gameID);
        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        //Figure out how to broadcast and make notifications
        connections.sendSelf(session, notification, game);
        connections.broadcast(session, notification, gameID, game);
    }

    public void leave(int gameID, String authToken, Session session){
        connections.delete(gameID, session);
        String userName = " ";
        String message = String.format("%s has left the game", userName);
    }

//    public void test(String username, int gameID) throws IOException {
//        String message = "Joined the game" + username;
//        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
//        connections.broadcast(null, notification, gameID);
//    }
}
