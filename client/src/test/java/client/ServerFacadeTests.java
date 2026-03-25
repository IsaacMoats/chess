package client;

import chess.ChessGame;
import exception.DataAccessException;
import model.AuthData;
import model.GameData;
import model.JoinGameRequest;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
     static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        int port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);
        String url = "http://localhost:" + port;
        facade = new ServerFacade(url);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        assertTrue(true);
    }

    @BeforeEach
    void clear() throws DataAccessException {
        facade.clear();
    }

    @Test public void registerPositive() throws DataAccessException {
        UserData userData = new UserData("player1", "password", "p1@email.com");
        AuthData authData = facade.addUser(userData);
        assertTrue(authData.authToken().length()>10);
        assertEquals("player1", authData.username());
    }

    @Test
    public void registerNegative() throws DataAccessException {
        UserData userData1 = new UserData("player1", "password", "email");
        UserData userData2 = new UserData("player1", "password", "email");
        AuthData authData = facade.addUser(userData1);
        assertEquals("player1", authData.username());
        assertThrows(DataAccessException.class, () -> facade.addUser(userData2));
    }

    @Test
    public void loginUserPositive() throws DataAccessException {
        UserData userData = new UserData("player1", "password", "email");
        facade.addUser(userData);
        AuthData authData = facade.loginUser(userData);
        assertEquals("player1", authData.username());
    }

    @Test
    public void loginUserNegative() throws DataAccessException {
        UserData userData = new UserData("player1", "password", "email");
        facade.addUser(userData);
        UserData userData2 = new UserData("player2", "password2", "email2");
        assertThrows(DataAccessException.class, () -> facade.loginUser(userData2));
    }

    @Test
    public void logoutUserPositive() throws DataAccessException {
        UserData userData = new UserData("player1", "password", "email");
        facade.addUser(userData);
        AuthData authData = facade.loginUser(userData);
        assertDoesNotThrow(() -> facade.logoutUser());
    }

    @Test
    public void logoutUserNegative() throws DataAccessException {
        UserData userData = new UserData("player1", "password", "email");
        facade.addUser(userData);
//        facade.authToken = "bad auth";
        facade.logoutUser();
        assertThrows(DataAccessException.class, () -> facade.logoutUser());
    }

    @Test
    public void createGamePositive() throws DataAccessException {
        UserData userData = new UserData("player1", "password", "email");
        facade.addUser(userData);
        AuthData authData = facade.loginUser(userData);
        String authToken = authData.authToken();
        ChessGame game = new ChessGame();
        GameData gameData = new GameData(1, null, null, "gameOne", game);
        GameData gameDataReturn = facade.createGame(gameData);
        assertEquals(gameData.gameID(), gameDataReturn.gameID());
    }

    @Test
    public void createGameNegative() throws DataAccessException {
        UserData userData = new UserData("player1", "password", "email");
        facade.addUser(userData);
        facade.logoutUser();
        ChessGame game = new ChessGame();
        GameData gameData = new GameData(1, null, null, "gameOne", game);
        assertThrows(DataAccessException.class, () -> facade.createGame(gameData));
    }

    @Test
    public void joinGamePositive() throws DataAccessException {
        UserData userData = new UserData("player1", "password", "email");
        facade.addUser(userData);
        facade.loginUser(userData);
        GameData gameData = new GameData(1, null, null, "gameOne", null);
        facade.createGame(gameData);
        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", 1);
        assertDoesNotThrow(()->facade.joinGame(joinGameRequest));
    }

    @Test void joinGameNegative() throws DataAccessException {
        UserData userData = new UserData("player1", "password", "email");
        facade.addUser(userData);
        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", 1);
        facade.authToken = "bad token";
        assertThrows(DataAccessException.class, ()-> facade.joinGame(joinGameRequest));
    }

    @Test void listGamesPositive() throws DataAccessException {
        ChessGame game1 = new ChessGame();
        GameData gameData1 = new GameData(1, null, null, "game1", game1);
        ChessGame game2 = new ChessGame();
        GameData gameData2 = new GameData(2, null, null, "game2", game2);
        UserData userData = new UserData("player1", "password", "email");
        facade.addUser(userData);
        facade.loginUser(userData);
        facade.createGame(gameData1);
        facade.createGame(gameData2);
        System.out.println(facade.listGames());

    }

}
