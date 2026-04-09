package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.SQLAuthDataAccess;
import dataaccess.SQLGameDataAccess;
import exception.DataAccessException;
import io.javalin.websocket.*;
import model.AuthData;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import service.UserService;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.sql.SQLException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private final UserService userService = new UserService();
    private final SQLAuthDataAccess authDataAccess = new SQLAuthDataAccess();
    private final SQLGameDataAccess gameDataAccess = new SQLGameDataAccess();

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
            connections.add(userGameCommand.getGameID(), session);
            switch (userGameCommand.getCommandType()) {
                case CONNECT -> connect(ctx, userGameCommand);
//                case MAKE_MOVE -> makeMove(session, username, gameID);
//                case LEAVE -> leave(gameID, authToken, session);
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

    }

    public void makeMove(Session session, String username, int gameID) throws IOException, SQLException, DataAccessException {
        ChessGame game = userService.getGame(gameID);
        LoadGameMessage gameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
        NotificationMessage moveMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, "MOVE MADE");
        connections.sendSelf(session, gameMessage, game);
        connections.broadcast(session, gameMessage, gameID, game, username);
        connections.broadcast(session, moveMessage, gameID, game, username);
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) throws Exception {

    }

    public void connect(WsContext ctx, UserGameCommand command) throws IOException, SQLException, DataAccessException {
        try {
            AuthData authData = authDataAccess.getAuthData(command.getAuthToken());
            if (authData == null) {
                ctx.send(new Gson().toJson(new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: no access")));
                return;
            }
            String username = authDataAccess.getUser(command.getAuthToken());
            int gameID = command.getGameID();
            ChessGame game = gameDataAccess.getGame(gameID);
            if (game == null) {
                ctx.send(new Gson().toJson(new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                        "Error: game does not exist")));
                return;
            }
            LoadGameMessage selfNotification = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
            String intro = username + " has joined the game. ";
            connections.sendSelf(ctx.session, selfNotification, game);
            NotificationMessage broadcastNotification =
                    new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, intro);
            connections.broadcast(ctx.session, broadcastNotification, gameID, game, username);
        } catch (DataAccessException e) {
            ctx.send(new Gson().toJson(
                    new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: " + e.getMessage())));
        }

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
