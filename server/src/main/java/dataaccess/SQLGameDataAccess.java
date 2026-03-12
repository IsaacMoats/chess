package dataaccess;
import chess.ChessGame;
import com.google.gson.Gson;
import passoff.exception.ResponseParseException;
import service.ListGameResponse;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class SQLGameDataAccess {
    private void configureDatabase(){
        try (Connection conn = DatabaseManager.getConnection()) {
            String createTable = """
                    CREATE TABLE IF NOT EXISTS gameData (
                    gameID INT AUTO_INCREMENT,
                    whiteUsername VARCHAR(255),
                    blackUsername VARCHAR(255),
                    gameName VARCHAR(255),
                    game longtext,
                    PRIMARY KEY (gameID)
                    )
                    """;
            try (PreparedStatement createTableStatement = conn.prepareStatement(createTable)) {
                createTableStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            System.out.println("Bad");
        }
    }

    public SQLGameDataAccess(){
        configureDatabase();
    }
    public Integer createGameData(String whiteUsername, String blackUsername, String gameName, ChessGame game)
            throws DataAccessException {
        int gameID = 0;
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement(
                    "INSERT INTO gameData (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            preparedStatement.setString(1, whiteUsername);
            preparedStatement.setString(2, blackUsername);
            preparedStatement.setString(3, gameName);
            String json = new Gson().toJson(game);
            preparedStatement.setString(4, json);
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                gameID = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage(), 500);
        }
        return gameID;
    }

    public void joinGame(String user, String color, Integer gameID) throws DataAccessException {
        if (gameID == null) {
            throw new DataAccessException("No gameID given!", 400);
        }
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement preparedStatement = null;
            if (Objects.equals(color, "BLACK")) {
                preparedStatement = conn.prepareStatement(
                        "SELECT blackUsername FROM gameData WHERE gameID=?"
                );
                preparedStatement.setInt(1, gameID);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        if (resultSet.getString("blackUsername")  != null) {
                            throw new DataAccessException("Color already taken!", 403);
                        }
                    }
                }
                preparedStatement = conn.prepareStatement(
                        "UPDATE gameData SET blackUsername=? WHERE gameID=?"
                );
            } else {
                preparedStatement = conn.prepareStatement(
                        "SELECT whiteUsername FROM gameData WHERE gameID=?"
                );
                preparedStatement.setInt(1, gameID);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        if (resultSet.getString("whiteUsername")  != null) {
                            throw new DataAccessException("Color already taken!", 403);
                        }
                    }
                }
                preparedStatement = conn.prepareStatement(
                        "UPDATE gameData SET whiteUsername=? WHERE gameID=?"
                );
            }
            preparedStatement.setString(1, user);
            preparedStatement.setInt(2, gameID);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage(), 500);
        }
    }

    public void deleteGameData() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement("TRUNCATE TABLE gameData");
            try {
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage(), 500);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage(), 500);
        }
    }

    public Collection<ListGameResponse> listGames() throws DataAccessException {
        ArrayList<ListGameResponse> games = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement(
                    "SELECT gameID, whiteUsername, blackUsername, gameName FROM gameData"
            );
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next()) {
//                    String json = resultSet.getString("game");
//                    ChessGame game = new Gson().fromJson(json, ChessGame.class);
                    Integer gameID = resultSet.getInt("gameID");
                    String whiteUsername = resultSet.getString("whiteUsername");
                    String blackUsername = resultSet.getString("blackUsername");
                    String gameName = resultSet.getString("gameName");
                    ListGameResponse gameResponse = new ListGameResponse(gameID, whiteUsername, blackUsername, gameName);
                    games.add(gameResponse);
                }
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage(), 500);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage(), 500);
        }
        return games;
    }
}
