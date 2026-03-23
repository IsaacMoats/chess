package dataaccess;

import exception.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class UserDataAccessTests {

    private SQLUserDataAccess userDataAccess = new SQLUserDataAccess();

    @BeforeEach
    void setup() throws DataAccessException {
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException ex) {
            System.out.println("failed to create database");
        }
        userDataAccess = new SQLUserDataAccess();
        userDataAccess.deleteUserData();
    }
    @Test
    void storeUserPasswordPositive() throws DataAccessException {
        String username = "userPos";
        String password = "PlainSecret123!";
        String email = "userPos@example.com";

        userDataAccess.storeUserPassword(username, password, email);
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT username, password, email FROM userData WHERE username=?"
            );
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    assertEquals("userPos", rs.getString("username"));
                    assertEquals("userPos@example.com", rs.getString("email"));
                    String hashed = rs.getString("password");
                    assertNotEquals(password, hashed);
                    assertTrue(BCrypt.checkpw(password, hashed));
                } else {
                    fail("No row returned for inserted user");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Bad Connection", 500);
        }
    }

    @Test
    void storeUserPasswordNegative() throws DataAccessException {
        String username = null;
        String password = "SomePass!";
        String email = "neg@example.com";

        DataAccessException ex = assertThrows(
                DataAccessException.class,
                () -> userDataAccess.storeUserPassword(username, password, email)
        );
        assertEquals(400, ex.getStatusCode());
        assertEquals("Bad Request", ex.getMessage());
        try (Connection conn = DatabaseManager.getConnection()) {
            int total = countUsers();
            assertEquals(0, total);
        } catch (SQLException e) {
            throw new DataAccessException("Bad Connection", 500);
        }
    }

    @Test
    void validateLoginPositive() throws DataAccessException {
        String username = "loginPos";
        String password = "CorrectHorseBatteryStaple";
        String email = "loginPos@example.com";
        userDataAccess.storeUserPassword(username, password, email);
        userDataAccess.validateLogin(new UserData(username, password, email));
    }

    @Test
    void validateLoginNegative() throws DataAccessException {
        String username = "loginNeg";
        String password = "RightPass123";
        String email = "loginNeg@example.com";
        userDataAccess.storeUserPassword(username, password, email);
        DataAccessException ex = assertThrows(
                DataAccessException.class,
                () -> userDataAccess.validateLogin(new UserData(username, "WrongPass!", email))
        );
        assertEquals(401, ex.getStatusCode());
        assertEquals("Wrong Password!", ex.getMessage());
        DbUserRow row = selectUser(username);
        assertUserRowEquals(row, "loginNeg", "loginNeg@example.com");
        assertNotNull(row);
        assertTrue(org.mindrot.jbcrypt.BCrypt.checkpw(password, row.hashedPassword));
    }
    @Test
    void usernameExistsPositive() throws DataAccessException {
        String username = "existsPos";
        String password = "SomePass123";
        String email = "existsPos@example.com";
        userDataAccess.storeUserPassword(username, password, email);

        boolean exists = userDataAccess.usernameExists(username);
        assertTrue(exists);
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT username FROM userData WHERE username=?"
            );
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertEquals(username, rs.getString("username"));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Bad Connection", 500);
        }
    }

    @Test
    void usernameExistsNegative() throws DataAccessException {
        String username = "noSuchUser";

        boolean exists = userDataAccess.usernameExists(username);
        assertFalse(exists);
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT username FROM userData WHERE username=?"
            );
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                assertFalse(rs.next());
            }
        } catch (SQLException e) {
            throw new DataAccessException("Bad Connection", 500);
        }
    }
    @Test
    void deleteUserDataPositive() throws DataAccessException {
        userDataAccess.storeUserPassword("u1", "p1", "u1@example.com");
        userDataAccess.storeUserPassword("u2", "p2", "u2@example.com");
        userDataAccess.deleteUserData();
        try (Connection conn = DatabaseManager.getConnection()) {
            int total = countUsers();
            assertEquals(0, total);
        } catch (SQLException e) {
            throw new DataAccessException("Bad Connection", 500);
        }
    }

    private static final class DbUserRow {
        final String username;
        final String hashedPassword;
        final String email;

        DbUserRow(String username, String hashedPassword, String email) {
            this.username = username;
            this.hashedPassword = hashedPassword;
            this.email = email;
        }
    }

    private DbUserRow selectUser(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT username, password, email FROM userData WHERE username=?"
            );
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new DbUserRow(
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email")
                    );
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Bad Connection", 500);
        }
    }

    private int countUsers() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) AS cnt FROM userData");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cnt");
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Bad Connection", 500);
        }
    }


    private void assertUserRowEquals(DbUserRow row, String expectedUsername, String expectedEmail) {
        assertNotNull(row, "Expected a user row but found none");
        assertEquals(expectedUsername, row.username);
        assertEquals(expectedEmail, row.email);
    }
}