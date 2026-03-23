package dataaccess;

import exception.DataAccessException;
import model.AuthData;
import java.util.UUID;
import java.util.HashMap;

public class AuthDataAccess {
    public HashMap<String, AuthData> authDataHash = new HashMap<>();

    public static String generateToken(){
        return UUID.randomUUID().toString();
    }

    public void createAuthData(String username, String authToken){
        AuthData authData = new AuthData(username, authToken);
        authDataHash.put(authToken, authData);
    }

    public AuthData getAuthData(String authToken){
        return authDataHash.get(authToken);
    }

    public void deleteAuthData() {
        authDataHash = new HashMap<>();
    }

    public void deleteAuthToken(String authToken) throws DataAccessException {
        if (!authDataHash.containsKey(authToken)) {
            throw new DataAccessException("User is not logged in", 401);
        } else {
            authDataHash.remove(authToken);
        }
    }

    public String getUser(String authToken){
        return authDataHash.get(authToken).username();
    }
}
