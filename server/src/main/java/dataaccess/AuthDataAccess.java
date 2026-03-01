package dataaccess;

import model.AuthData;
import java.util.UUID;
import java.util.HashMap;

public class AuthDataAccess {
    private HashMap<String, AuthData> authDataHash = new HashMap<>();

    public static String generateToken(){
        return UUID.randomUUID().toString();
    }

    // Following CRUD operations
    // Create
    public void createAuthData(String username, String authToken){
        AuthData authData = new AuthData(username, authToken);
        authDataHash.put(authToken, authData);
    }
    // Read (get)
    public AuthData getAuthData(String authToken){
        return authDataHash.get(authToken);
    }
    // Delete
    // Idea: set fields to void
    public void deleteAuthData() {
        // Should somehow delete or reset the authData
        authDataHash = new HashMap<>();
    }
    public void deleteAuthToken(String authToken) throws DataAccessException{
        if (!authDataHash.containsKey(authToken)) {
            throw new DataAccessException("User is not logged in", 401);
        } else {
            authDataHash.remove(authToken);
        }
    }
    public boolean authTokenExists(String authToken){
        return authDataHash.containsValue(authToken);
    }
}
