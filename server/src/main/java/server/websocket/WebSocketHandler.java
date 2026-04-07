package server.websocket;

import com.google.gson.Gson;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.commands.UserGameCommand;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final ConnectionManager connections = new ConnectionManager();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) throws Exception {
        UserGameCommand userGameCommand = new Gson().fromJson(ctx.message(), UserGameCommand.class);
        switch (userGameCommand.getCommandType()){
            case CONNECT -> connect(userGameCommand.getGameID(), userGameCommand.getAuthToken(), ctx.session);
            case LEAVE -> leave(userGameCommand.getGameID(), userGameCommand.getAuthToken(), ctx.session);
        }
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) throws Exception {

    }

    public void connect(int gameID, String authToken, Session session){
        connections.add(gameID, session);
        // Figure out how to authenticate and get username
        String userName = " ";
        String message = String.format("%s has joined the game", userName);
        //Figure out how to broadcast and make notifications
    }

    public void leave(int gameID, String authToken, Session session){
        connections.delete(gameID, session);
        String userName = " ";
        String message = String.format("%s has left the game", userName);
    }

}
