package dataaccess;

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
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) AS cnt FROM userData"
            );
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    assertEquals(0, rs.getInt("cnt"));
                }
            }
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
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT username, password, email FROM userData WHERE username=?"
            );
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    assertEquals("loginNeg", rs.getString("username"));
                    assertEquals("loginNeg@example.com", rs.getString("email"));
                    assertTrue(BCrypt.checkpw(password, rs.getString("password")));
                } else {
                    fail("Expected stored user not found");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Bad Connection", 500);
        }
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
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) AS cnt FROM userData"
            );
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    assertEquals(0, rs.getInt("cnt"));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Bad Connection", 500);
        }
    }
}