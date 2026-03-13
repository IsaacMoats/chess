package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class AuthDataAccessTests {
    private SQLAuthDataAccess authDataAccess = new SQLAuthDataAccess();

    @BeforeEach
    void setup() throws DataAccessException {
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException ex) {
            System.out.println("failed to create database");
        }
        authDataAccess = new SQLAuthDataAccess();
        authDataAccess.deleteAuthData();
    }
    @Test
    void generateTokenPositive() {
        String token = SQLAuthDataAccess.generateToken();
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void generateTokenNegative() {
        String token1 = SQLAuthDataAccess.generateToken();
        String token2 = SQLAuthDataAccess.generateToken();
        assertNotEquals(token1, token2);
    }

    @Test
    void createAuthDataPositive() throws DataAccessException {
        String username = "userCADPos";
        String token = SQLAuthDataAccess.generateToken();

        authDataAccess.createAuthData(username, token);
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT username, authToken FROM authData WHERE authToken=?"
            );
            ps.setString(1, token);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    assertEquals("userCADPos", rs.getString("username"));
                    assertEquals(token, rs.getString("authToken"));
                } else {
                    fail("No row returned for inserted auth record");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Bad Connection", 500);
        }
    }

    @Test
    void createAuthDataNegative() throws DataAccessException {
        String user1 = "userCADNegA";
        String token1 = SQLAuthDataAccess.generateToken();
        authDataAccess.createAuthData(user1, token1);
        String user2 = "userCADNegB";
        String token2 = SQLAuthDataAccess.generateToken();
        authDataAccess.createAuthData(user2, token2);
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT username, authToken FROM authData WHERE authToken=?"
            );
            ps.setString(1, token2);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    assertNotEquals(user1, rs.getString("username"));
                    assertNotEquals(token1, rs.getString("authToken"));
                } else {
                    fail("No row returned for second auth record");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Bad Connection", 500);
        }
    }

    @Test
    void getAuthDataPositive() throws DataAccessException {
        String username = "userGADPos";
        String token = SQLAuthDataAccess.generateToken();
        authDataAccess.createAuthData(username, token);

        AuthData found = authDataAccess.getAuthData(token);
        assertNotNull(found);
        assertEquals(username, found.username());
        assertEquals(token, found.authToken());
    }

    @Test
    void getAuthDataNegative() throws DataAccessException {
        String missingToken = SQLAuthDataAccess.generateToken(); // never inserted
        AuthData found = authDataAccess.getAuthData(missingToken);
        assertNull(found);
    }

    @Test
    void deleteAuthTokenPositive() throws DataAccessException {
        String username = "userDATPos";
        String token = SQLAuthDataAccess.generateToken();
        authDataAccess.createAuthData(username, token);
        authDataAccess.deleteAuthToken(token);
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT username, authToken FROM authData WHERE authToken=?"
            );
            ps.setString(1, token);
            try (ResultSet rs = ps.executeQuery()) {
                assertFalse(rs.next());
            }
        } catch (SQLException e) {
            throw new DataAccessException("Bad Connection", 500);
        }
    }

    @Test
    void deleteAuthTokenNegative() throws DataAccessException {
        String missingToken = SQLAuthDataAccess.generateToken(); // not in DB

        DataAccessException ex = assertThrows(
                DataAccessException.class,
                () -> authDataAccess.deleteAuthToken(missingToken)
        );
        assertEquals(401, ex.getStatusCode());
        assertEquals("User is not logged in", ex.getMessage());
    }

    @Test
    void getUserPositive() throws DataAccessException {
        String username = "userGUPos";
        String token = SQLAuthDataAccess.generateToken();
        authDataAccess.createAuthData(username, token);

        String resolved = authDataAccess.getUser(token);
        assertEquals(username, resolved);
    }

    @Test
    void getUserNegative() throws DataAccessException {
        String missingToken = SQLAuthDataAccess.generateToken();

        String resolved = authDataAccess.getUser(missingToken);
        assertEquals("hi", resolved);
    }
    @Test
    void deleteAuthDataPositive() throws DataAccessException {
        authDataAccess.createAuthData("u1", SQLAuthDataAccess.generateToken());
        authDataAccess.createAuthData("u2", SQLAuthDataAccess.generateToken());
        authDataAccess.deleteAuthData();

        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) AS cnt FROM authData"
            );
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    assertEquals(0, rs.getInt("cnt"));
                } else {
                    fail("COUNT(*) did not return a row");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Bad Connection", 500);
        }
    }
}