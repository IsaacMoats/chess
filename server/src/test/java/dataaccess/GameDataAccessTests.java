package dataaccess;

import chess.ChessGame;
import exception.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class GameDataAccessTests {
    private SQLGameDataAccess gameDataAccess = new SQLGameDataAccess();

    @BeforeEach
    void setup() throws DataAccessException {
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException ex) {
            System.out.println("failed to create database");
        }
        gameDataAccess = new SQLGameDataAccess();
        gameDataAccess.deleteGameData();
    }

    @Test
    void createGameDataPositive() throws DataAccessException {
        String whiteUsername = "whiteUserTest";
        String blackUsername = "blackUserTest";
        String gameName = "gameOne";
        ChessGame game = new ChessGame();
        int gameID = gameDataAccess.createGameData(whiteUsername, blackUsername, gameName, game);
        assertEquals(1, gameID);
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement(
                    "SELECT whiteUsername, blackUsername, gameName, game FROM gameData WHERE gameID=?"
            );
            preparedStatement.setInt(1, gameID);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    assertEquals("whiteUserTest", resultSet.getString("whiteUsername"));
                    assertEquals("blackUserTest", resultSet.getString("blackUsername"));
                    assertEquals("gameOne", resultSet.getString("gameName"));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Bad Connection", 500);
        }
    }

    @Test
    void createGameDataNegative() throws DataAccessException {
        String whiteUsername = "whiteUserTest";
        String blackUsername = "blackUserTest";
        String gameName = "gameOne";
        ChessGame game = new ChessGame();
        gameDataAccess.createGameData(whiteUsername, blackUsername, gameName, game);
        whiteUsername = "whiteUserTest2";
        blackUsername = "blackUserTest2";
        gameName = "gameTwo";
        game = new ChessGame();
        int gameID = gameDataAccess.createGameData(whiteUsername, blackUsername, gameName, game);
        assertEquals(2, gameID);
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement(
                    "SELECT whiteUsername, blackUsername, gameName, game FROM gameData WHERE gameID=?"
            );
            preparedStatement.setInt(1, gameID);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    assertNotEquals("whiteUserTest", resultSet.getString("whiteUsername"));
                    assertNotEquals("blackUserTest", resultSet.getString("blackUsername"));
                    assertNotEquals("gameOne", resultSet.getString("gameName"));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Bad Connection", 500);
        }
    }

    @Test
    void joinGamePositive() throws DataAccessException {
        // Create a game with open BLACK seat
        String whiteUsername = "whiteUserTest";
        String blackUsername = null;
        String gameName = "joinGame";
        ChessGame game = new ChessGame();

        int gameID = gameDataAccess.createGameData(whiteUsername, blackUsername, gameName, game);

        // Join as BLACK
        gameDataAccess.joinGame("blackJoiner", "BLACK", gameID);

        // Verify via DB
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement(
                    "SELECT whiteUsername, blackUsername, gameName FROM gameData WHERE gameID=?"
            );
            preparedStatement.setInt(1, gameID);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    assertEquals("whiteUserTest", resultSet.getString("whiteUsername"));
                    assertEquals("blackJoiner", resultSet.getString("blackUsername"));
                    assertEquals("joinGame", resultSet.getString("gameName"));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Bad Connection", 500);
        }
    }

    @Test
    void joinGameNegativeColorTaken() throws DataAccessException {
        String white = "alice";
        String black = "bob";
        String gameName = "match3";
        ChessGame game = new ChessGame();

        int gameID = gameDataAccess.createGameData(white, black, gameName, game);

        DataAccessException ex = org.junit.jupiter.api.Assertions.assertThrows(
                DataAccessException.class,
                () -> gameDataAccess.joinGame("zoe", "WHITE", gameID)
        );
        assertEquals(403, ex.getStatusCode());
        assertEquals("Color already taken!", ex.getMessage());
    }

    @Test
    void listGamesPositive() throws DataAccessException {
        // Insert two games
        int id1 = gameDataAccess.createGameData("w1", "b1", "g1", new ChessGame());
        int id2 = gameDataAccess.createGameData("w2", "b2", "g2", new ChessGame());

        // Verify via DB by id1
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement ps1 = conn.prepareStatement(
                    "SELECT whiteUsername, blackUsername, gameName FROM gameData WHERE gameID=?"
            );
            ps1.setInt(1, id1);
            try (ResultSet rs = ps1.executeQuery()) {
                if (rs.next()) {
                    assertEquals("w1", rs.getString("whiteUsername"));
                    assertEquals("b1", rs.getString("blackUsername"));
                    assertEquals("g1", rs.getString("gameName"));
                }
            }

            PreparedStatement ps2 = conn.prepareStatement(
                    "SELECT whiteUsername, blackUsername, gameName FROM gameData WHERE gameID=?"
            );
            ps2.setInt(1, id2);
            try (ResultSet rs = ps2.executeQuery()) {
                if (rs.next()) {
                    assertEquals("w2", rs.getString("whiteUsername"));
                    assertEquals("b2", rs.getString("blackUsername"));
                    assertEquals("g2", rs.getString("gameName"));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Bad Connection", 500);
        }
    }

    @Test
    void listGamesNegative() throws DataAccessException {
        // Insert two games
        int id1 = gameDataAccess.createGameData("wA", "bA", "gA", new ChessGame());
        int id2 = gameDataAccess.createGameData("wB", "bB", "gB", new ChessGame());

        // For id2, ensure it does NOT equal id1’s values
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT whiteUsername, blackUsername, gameName FROM gameData WHERE gameID=?"
            );
            ps.setInt(1, id2);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    assertNotEquals("wA", rs.getString("whiteUsername"));
                    assertNotEquals("bA", rs.getString("blackUsername"));
                    assertNotEquals("gA", rs.getString("gameName"));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Bad Connection", 500);
        }
    }

    @Test
    void deleteGameDataPositive() throws DataAccessException {
        gameDataAccess.createGameData("w", "b", "g", new ChessGame());

        var pre = gameDataAccess.listGames();
        assertEquals(1, pre.size());

        gameDataAccess.deleteGameData();

        var post = gameDataAccess.listGames();
        assertEquals(0, post.size());

        // Also ensure next insert starts at 1 again if your DB resets AUTO_INCREMENT on TRUNCATE
        int newId = gameDataAccess.createGameData(null, null, "fresh", new ChessGame());
        assertEquals(1, newId);
    }
}
