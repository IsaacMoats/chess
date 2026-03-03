package service;

import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    private UserService userService = new UserService();

    @BeforeEach
    void setup() throws DataAccessException {
        userService = new UserService();
        userService.clearGame();
    }

    @Test
    void addUserPositive() throws DataAccessException {
        UserData userData = new UserData("username1", "password1", "email1");
        AuthData authData = userService.addUser(userData);
        assertNotNull(authData);
        assertEquals(authData.username(), userData.username());
        assertNotNull(authData.authToken());
    }

    @Test
    void addUserNegative() throws DataAccessException {
        UserData userData = new UserData("username", "password", "email");
        userService.addUser(userData);
        assertThrows(
                DataAccessException.class,
                () -> userService.addUser(new UserData("username", "password", "email")),
                "Throws DataAccessException due to duplicate username"
        );
    }

    @Test
    void loginUserPositive() throws DataAccessException {
        UserData userData = new UserData("username", "password", "email");
        userService.addUser(userData);
        AuthData authData = userService.loginUser(userData);
        assertNotNull(authData);
        assertEquals(authData.username(), userData.username());
        assertNotNull(authData.authToken());
    }

    @Test
    void loginUserNegative(){
        UserData userData = new UserData("username", "password", "email");
        assertThrows(
                DataAccessException.class,
                () -> userService.loginUser(userData),
                "Throws DataAccessException due to user not existing"
        );
    }


    @Test
    void logoutUserPositive() throws DataAccessException {
        UserData userData = new UserData("username", "password", "email");
        AuthData loginAuth = userService.addUser(userData);
        AuthData authData = userService.loginUser(userData);

        assertEquals(loginAuth.username(), authData.username());
    }

    @Test
    void logoutUserNegative() throws DataAccessException {
        UserData userData = new UserData("username", "password", "email");
        userService.addUser(userData);
        assertThrows(
                DataAccessException.class,
                () -> userService.logoutUser("bad token"),
                "Throws DataAccessException due to bad authToken"
        );
    }

    @Test
    void createGamePositive() {
        GameData gameName = new GameData(null,
                null,
                null,
                "name",
                null);
        Integer gameID = userService.createGame(gameName);
        assertNotNull(gameID);
        assertEquals(1, gameID);
    }

    @Test
    void createGameNegative() {
        GameData gameName = new GameData(null, null, null, null, null);
        assertThrows(
                DataAccessException.class,
                () -> userService.createGame(gameName),
                "Throws DataAccessException due to bad game name"
        );
    }

    @Test
    void clearGame() {
    }


    @Test
    void joinGame() {
    }

    @Test
    void listGames() {
    }
}
