package service;

import chess.ChessGame;
import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import dataaccess.GameDataAccess;
import dataaccess.UserDataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;

import javax.xml.crypto.Data;
import java.util.Collection;
import java.util.HashMap;

// register
public class UserService {
    public final UserDataAccess userDataAccess = new UserDataAccess();
    public final AuthDataAccess authDataAccess = new AuthDataAccess();
    public final GameDataAccess gameDataAccess = new GameDataAccess();

    public AuthData addUser(UserData userData) throws DataAccessException {
        userDataAccess.newUserData(userData.username(), userData.password(), userData.email());
        String authToken = AuthDataAccess.generateToken();
        authDataAccess.createAuthData(userData.username(), authToken);
        return authDataAccess.getAuthData(authToken);
    }

    public AuthData loginUser(UserData userData) throws DataAccessException {
        userDataAccess.validateLogin(userData);
        String authToken = AuthDataAccess.generateToken();
        authDataAccess.createAuthData(userData.username(), authToken);
        return authDataAccess.getAuthData(authToken);
    }

    public void logoutUser(String authToken) throws DataAccessException {
        authDataAccess.deleteAuthToken(authToken);
    }

    public Integer createGame(GameData gameName) {
        ChessGame game = new ChessGame();
        return gameDataAccess.createGameData(null, null, gameName.gameName(), game);
    }

    public void clearGame() {
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

    public String getUser(String authToken) {
        return authDataAccess.getUser(authToken);
    }

    public void joinGame(String user, String color, Integer gameID) throws DataAccessException{
        gameDataAccess.joinGame(user, color, gameID);
    }

    public Collection<ListGameResponse> listGames() {
        return gameDataAccess.listGames();
    }
}