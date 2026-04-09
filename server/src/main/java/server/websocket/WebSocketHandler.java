package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.SQLAuthDataAccess;
import dataaccess.SQLGameDataAccess;
import exception.DataAccessException;
import io.javalin.websocket.*;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import service.UserService;
import websocket.commands.MakeMoveCommand;
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
                case MAKE_MOVE -> makeMove(ctx, new Gson().fromJson(ctx.message(), MakeMoveCommand.class));
                case LEAVE -> leave(ctx, userGameCommand);
                case RESIGN -> resign(ctx, userGameCommand);
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

    }

    public void resign(WsContext ctx, UserGameCommand userGameCommand) throws SQLException, DataAccessException {
        try {
            if (authDataID(userGameCommand.getAuthToken(), userGameCommand.getGameID(), ctx)) {
                ChessGame game = gameDataAccess.getGame(userGameCommand.getGameID());
                String white = gameDataAccess.getGameData(userGameCommand.getGameID()).whiteUsername();
                String black = gameDataAccess.getGameData(userGameCommand.getGameID()).blackUsername();
                String username = authDataAccess.getUser(userGameCommand.getAuthToken());
                if (game.getOver()) {
                    ctx.send(new Gson().toJson(new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                            "Error: game is already over")));
                    return;
                }
                if (Objects.equals(username, white)) {
                    game.setOver(true);
                    gameDataAccess.updateGame(userGameCommand.getGameID(), white, black, game);
                    ctx.send(new Gson().toJson(new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                            "You resigned")));
                    String message = username + " has resigned (white side).";
                    NotificationMessage notificationMessage = new NotificationMessage(
                            ServerMessage.ServerMessageType.NOTIFICATION, message);
                    connections.broadcast(ctx.session, notificationMessage, userGameCommand.getGameID(), message);
                } else if (Objects.equals(username, black)) {
                    game.setOver(true);
                    gameDataAccess.updateGame(userGameCommand.getGameID(), white, black, game);
                    ctx.send(new Gson().toJson(new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                            "You resigned")));
                    String message = username + " has resigned (black side).";
                    NotificationMessage notificationMessage = new NotificationMessage(
                            ServerMessage.ServerMessageType.NOTIFICATION, message);
                    connections.broadcast(ctx.session, notificationMessage, userGameCommand.getGameID(), message);
                } else {
                    ctx.send(new Gson().toJson(new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                            "Error: cannot resign if watching")));
                }
            }
        } catch (DataAccessException | RuntimeException | SQLException | IOException e) {
            ctx.send(new Gson().toJson(new ErrorMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage())));
        }
    }


    public void makeMove(WsContext ctx, MakeMoveCommand makeMoveCommand) throws IOException, SQLException, DataAccessException {
        try {
            if (!authDataID(makeMoveCommand.getAuthToken(), makeMoveCommand.getGameID(), ctx)) {
               return;
            }
            ChessGame game = gameDataAccess.getGame(makeMoveCommand.getGameID());
            if (game.getOver()) {
                ctx.send(new Gson().toJson(
                    new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
            "Error: Game is over! Cannot make move")));
            }
            String white = gameDataAccess.getGameData(makeMoveCommand.getGameID()).whiteUsername();
            String black = gameDataAccess.getGameData(makeMoveCommand.getGameID()).blackUsername();
            String username = authDataAccess.getUser(makeMoveCommand.getAuthToken());
            if (Objects.equals(username, white) && game.getTeamTurn() != ChessGame.TeamColor.WHITE) {
                ctx.send(new Gson().toJson(
                    new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
            "Error: Not your turn! Cannot make move")));
            } else if (Objects.equals(username, black) && game.getTeamTurn() != ChessGame.TeamColor.BLACK) {
                ctx.send(new Gson().toJson(
                    new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
            "Error: Not your turn! Cannot make move")));
            } else if (!Objects.equals(username, white) && !Objects.equals(username, black)) {
                ctx.send(new Gson().toJson(
                    new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
            "Error: Not a player! Cannot make move")));
            }

            ChessMove move = makeMoveCommand.getMove();
            game.makeMove(move);
            gameDataAccess.updateGame(makeMoveCommand.getGameID(), white, black, game);


        } catch (Exception e) {
            ctx.send(new Gson().toJson(
                    new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: " + e.getMessage())));
        }
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
