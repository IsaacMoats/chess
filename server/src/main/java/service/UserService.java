package service;

import chess.ChessGame;
import dataaccess.*;
import exception.DataAccessException;
import model.AuthData;
import model.GameData;
import model.ListGameResponse;
import model.UserData;

import java.sql.SQLException;
import java.util.Collection;

// register
public class UserService {
    public final SQLUserDataAccess userDataAccess = new SQLUserDataAccess();
    public final SQLAuthDataAccess authDataAccess = new SQLAuthDataAccess();
    public final SQLGameDataAccess gameDataAccess = new SQLGameDataAccess();


    public ChessGame getGame(int gameID) throws SQLException, DataAccessException {
        return gameDataAccess.getGame(gameID);
    }

    public AuthData getAuthData(String username) throws DataAccessException {
        return authDataAccess.getAuthDataFromUsername(username);
    }
    public AuthData addUser(UserData userData) throws DataAccessException {
        userDataAccess.storeUserPassword(userData.username(), userData.password(), userData.email());
        String authToken = SQLAuthDataAccess.generateToken();
        authDataAccess.createAuthData(userData.username(), authToken);
        return authDataAccess.getAuthData(authToken);
    }

    public AuthData loginUser(UserData userData) throws DataAccessException {
        userDataAccess.validateLogin(userData);
        String authToken = SQLAuthDataAccess.generateToken();
        authDataAccess.createAuthData(userData.username(), authToken);
        return authDataAccess.getAuthData(authToken);
    }

    public void logoutUser(String authToken) throws DataAccessException {
        authDataAccess.deleteAuthToken(authToken);
    }

    public Integer createGame(GameData gameName) throws DataAccessException{
        if (gameName.gameName() == null) {
            throw new DataAccessException("Needs a game name!", 400);
        }
        ChessGame game = new ChessGame();
        return gameDataAccess.createGameData(null, null, gameName.gameName(), game);
    }

    public void clearGame() throws DataAccessException {
        authDataAccess.deleteAuthData();
        gameDataAccess.deleteGameData();
        userDataAccess.deleteUserData();
    }

    public Boolean authenticate(String authToken) throws DataAccessException{
        if (authDataAccess.getAuthData(authToken) == null){
            throw new DataAccessException("Not authorized", 401);
        } else {
            return true;
        }
    }

    public String getUser(String authToken) throws DataAccessException {
        return authDataAccess.getUser(authToken);
    }

    public GameData joinGame(String user, String color, Integer gameID) throws DataAccessException{
        return gameDataAccess.joinGame(user, color, gameID);
    }

    public Collection<ListGameResponse> listGames() throws DataAccessException {
        return gameDataAccess.listGames();
    }

}