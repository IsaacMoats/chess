package dataaccess;
import exception.DataAccessException;
import model.UserData;

import java.util.HashMap;
import java.util.Objects;

public class UserDataAccess {
    public HashMap<String, UserData> users = new HashMap<>();

    public void newUserData(String username, String password, String email) throws DataAccessException {
        if (username == null || password == null || email == null) {
            throw new DataAccessException("Bad Request", 400);
        }
        if (!usernameExists(username)){
            UserData userData = new UserData(username, password, email);
            users.put(userData.username(), userData);
        } else if (usernameExists(username)) {
            throw new DataAccessException("Username already taken!", 403);
        } else {
            throw new DataAccessException("Bad Request", 400);
        }
    }

    public void validateLogin(UserData userData) throws DataAccessException{
        UserData checkUserData = users.get(userData.username());
        if (userData.username() == null){
            throw new DataAccessException("No username given!", 400);
        } else if (userData.password() == null) {
            throw new DataAccessException("No password given!", 400);
        } else if (checkUserData == null){
            throw new DataAccessException("User not registered!", 401);
        } else if (!Objects.equals(checkUserData.password(), userData.password())){
            throw new DataAccessException("Wrong password!", 401);
        }
    }

    public void deleteUserData(){
        users = new HashMap<>();
    }

    public boolean usernameExists(String username){
        return users.containsKey(username);
    }
}
