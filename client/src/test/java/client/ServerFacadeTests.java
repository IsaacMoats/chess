package client;

import exception.DataAccessException;
import model.AuthData;
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

    }

//    @Test
//    public void clearDatabase() throws DataAccessException {
//        UserData userData = new UserData("player1", "password", "email");
//        facade.addUser(userData);
//        facade.clear();
//        assertThrows(DataAccessException.class, () ->)
//    }

}
