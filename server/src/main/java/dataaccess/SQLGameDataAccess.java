package dataaccess;
import passoff.exception.ResponseParseException;

import java.sql.*;

public class SQLGameDataAccess {
    private final String[] createStatements = {
            """
    CREATE TABLE IF NOT EXISTS gameData(
            `id` INT NOT NULL AUTO_INCREMENT,
            `whiteUsername` STRING DEFAULT NULL,
            `blackUsername` STRING DEFAULT NULL,
            `gameName` STRING NOT NULL,
            `game` ChessGame,
            PRIMARY KEY (`id`),
    );
    """
    };
    private void configureDatabase() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {

        } catch (SQLException ex) {
            throw new DataAccessException("Didn't work", ex, ex.getErrorCode());
        }
    }
}
