package dataaccess;

import model.UserData;
import org.eclipse.jetty.server.Authentication;

import java.sql.*;

public class SQLUserDataAccess{
    private void configureDatabase(){
        try (Connection conn = DatabaseManager.getConnection()) {
            String createTable = """
                    CREATE TABLE IF NOT EXISTS userData (
                    username VARCHAR(255),
                    password VARCHAR(255),
                    email VARCHAR(255)
                    )
                    """;
            try (PreparedStatement createTableStatement = conn.prepareStatement(createTable)) {
                createTableStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            System.out.println("Bad");
        }
    }

    public SQLUserDataAccess(){
        configureDatabase();
    }

    public void newUserData(String username, String password, String email) throws DataAccessException {
        if (username == null || password == null || email == null) {
            throw new DataAccessException("Bad Request", 400);
        }
        try (Connection conn = DatabaseManager.getConnection()) {
            if (!usernameExists(username)) {
                var preparedStatement = conn.prepareStatement(
                        "INSERT INTO userData (username, password, email) VALUES (?, ?, ?)");
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                preparedStatement.setString(3, email);
                preparedStatement.executeUpdate();
            } else if (usernameExists(username)) {
                throw new DataAccessException("Username already taken!", 403);
            } else {
                throw new DataAccessException("Bad Request", 400);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage(), e.getErrorCode());
        }

    }

    public void validateLogin(UserData userData) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            if (userData.username() == null) {
                throw new DataAccessException("No username given!", 400);
            } else if (userData.password() == null) {
                throw new DataAccessException("No password given!", 400);
            } else if (!usernameExists(userData.username())) {
                throw new DataAccessException("User not registered!", 401);
            }
            var preparedStatement = conn.prepareStatement(
                    "SELECT username, password FROM userData WHERE username=?");
            preparedStatement.setString(1, userData.username());
            UserData userDataDatabase = null;
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    userDataDatabase = new UserData(
                            resultSet.getString("username"),
                            resultSet.getString("password"),
                            resultSet.getString("email"));

                }
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage(), e.getErrorCode());
            }
            assert userDataDatabase != null;
            if (!userData.password().equals(userDataDatabase.password())){
                throw new DataAccessException("Wrong Password!", 401);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage(), e.getErrorCode());
        }
    }

    public void deleteUserData() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement("TRUNCATE TABLE userData");
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage(), e.getErrorCode());
        }
    }

    public boolean usernameExists(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var preparedStatement = conn.prepareStatement(
                    "SELECT username, password, email FROM userData WHERE username=?");
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage(), e.getErrorCode());
        }
    }
}
