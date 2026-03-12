package dataaccess;

import model.UserData;
import org.eclipse.jetty.server.Authentication;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.Objects;

public class SQLUserDataAccess {
    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS userData (
                username varchar(256) NOT NULL,
                password varchar(256) NOT NULL,
                email varchar(256) NOT NULL,
                PRIMARY KEY (username)
            )
"""
    };
    private void configureDatabase() throws SQLException{
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                } catch (SQLException ex) {
                    throw new SQLException("bad connection", ex);
                }
            }
        } catch (SQLException | DataAccessException ex) {
            throw new SQLException("Bad connection", ex);
        }
    }

    public SQLUserDataAccess() {
        try {
            configureDatabase();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() throws DataAccessException {
            return DatabaseManager.getConnection();
    }


    public void newUserData(String username, String password, String email) throws DataAccessException {
        if (username == null || password == null || email == null) {
            throw new DataAccessException("Bad Request", 400);
        }
        if (!usernameExists(this.getConnection(), username)) {
            // insert logic to make new row with the proper data
            try (var preparedStatement = this.getConnection().prepareStatement(
                    "INSERT INTO userData (username, password, email) VALUES (?, ?, ?)")) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                preparedStatement.setString(3, email);
                preparedStatement.executeUpdate();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else if (usernameExists(this.getConnection(), username)) {
            throw new DataAccessException("Username already taken!", 403);
        } else {
            throw new DataAccessException("Bad Request", 400);
        }
    }

    public void validateLogin(UserData userData) throws DataAccessException {
        // userData input is the username and password
        // Get the userData based off of the username
        try (var preparedStatement = getConnection().prepareStatement(
                "SELECT username, password FROM userData WHERE username=?")) {
            preparedStatement.setString(1, userData.username());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet == null) {
                throw new DataAccessException("User not registered!", 401);
            }
            var username = resultSet.getString("username");
            var password = resultSet.getString("password");
            UserData checkUserData = new UserData(username, password, null);
            if (userData.username() == null){
                throw new DataAccessException("No username given!", 400);
            } else if (userData.password() == null) {
                throw new DataAccessException("No password given!", 400);
            } else if (checkUserData == null){
                throw new DataAccessException("User not registered!", 401);
            } else if (!Objects.equals(checkUserData.password(), userData.password())){
                throw new DataAccessException("Wrong password!", 401);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Bad access!", e, e.getErrorCode());
        }

    }

    public boolean usernameExists(Connection conn, String username) {
        try (var preparedStatement = conn.prepareStatement("SELECT username FROM userData WHERE username=?")) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet != null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteUserData() {
        try (var preparedStatement = getConnection().prepareStatement("TRUNCATE userData")) {
            preparedStatement.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
