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
import java.util.Objects;

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
                case MAKE_MOVE -> makeMove(session, userGameCommand.getAuthToken(), userGameCommand.getGameID());
                case LEAVE -> leave(ctx, userGameCommand);
                case RESIGN -> resign(ctx, userGameCommand);
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
        connections.broadcast(session, gameMessage, gameID, null);
        connections.broadcast(session, moveMessage, gameID, null);
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) throws Exception {

    }

    public void connect(WsContext ctx, UserGameCommand command) throws IOException, SQLException, DataAccessException {
        try {
            if (authDataID(command.getAuthToken(), command.getGameID(), ctx)) {
                String username = authDataAccess.getUser(command.getAuthToken());
                ChessGame game = gameDataAccess.getGame(command.getGameID());
                int gameID = command.getGameID();
                authDataID(command.getAuthToken(), command.getGameID(), ctx);
                LoadGameMessage selfNotification = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
                String intro = username + " has joined the game. ";
                connections.sendSelf(ctx.session, selfNotification, game);
                NotificationMessage broadcastNotification =
                        new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, intro);
                connections.broadcast(ctx.session, broadcastNotification, gameID, intro);
            }
        } catch (DataAccessException e) {
            ctx.send(new Gson().toJson(
                    new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: " + e.getMessage())));
        }

    }

    public void leave(WsContext ctx, UserGameCommand userGameCommand){
        boolean playing = false;
        String message;
        try {
            if (authDataID(userGameCommand.getAuthToken(), userGameCommand.getGameID(), ctx)) {
                AuthData authData = authDataAccess.getAuthData(userGameCommand.getAuthToken());
                String white = gameDataAccess.getGameData(userGameCommand.getGameID()).whiteUsername();
                String black = gameDataAccess.getGameData(userGameCommand.getGameID()).blackUsername();
                if (Objects.equals(white, authData.username())) {
                    white = null;
                    playing = true;
                    message = authData.username() + " quit playing the game. White is now open to play.";
                } else if (Objects.equals(black, authData.username())) {
                    black = null;
                    playing = true;
                    message = authData.username() + " quit playing the game. Black is now open to play.";
                } else {
                    message = authData.username() + " quit watching the game.";
                }
                gameDataAccess.updateGame(userGameCommand.getGameID(), white, black,
                        gameDataAccess.getGame(userGameCommand.getGameID()));
                connections.delete(userGameCommand.getGameID(), ctx.session);

                NotificationMessage notificationMessage =
                        new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
                connections.broadcast(ctx.session, notificationMessage, userGameCommand.getGameID(), message);
            }
        } catch (DataAccessException | RuntimeException | SQLException | IOException e) {
            ctx.send(new Gson().toJson(new ErrorMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage())));
        }
    }

    private boolean authDataID(String authToken, int gameID, WsContext ctx) throws DataAccessException, SQLException {
        AuthData authData = authDataAccess.getAuthData(authToken);
        if (authData == null) {
            ctx.send(new Gson().toJson(new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: no access")));
            return false;
        }
        ChessGame game = gameDataAccess.getGame(gameID);
        if (game == null) {
            ctx.send(new Gson().toJson(new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                    "Error: game does not exist")));
            return false;
        }
        return true;
    }

//    public void test(String username, int gameID) throws IOException {
//        String message = "Joined the game" + username;
//        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
//        connections.broadcast(null, notification, gameID);
//    }
}
