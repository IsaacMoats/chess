package server;

import exception.DataAccessException;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.ListGameResponse;
import model.UserData;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverURL;
    public String authToken;

    public ServerFacade(String url) {
        serverURL = url;
    }

    public AuthData addUser (UserData userData) throws DataAccessException {
        HttpRequest request = buildRequest("POST", "/user",userData);
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }

    public AuthData loginUser(UserData userData) throws DataAccessException {
        HttpRequest request = buildRequest("POST", "/session", userData);
        var response = sendRequest(request);
        AuthData authData = handleResponse(response, AuthData.class);
        assert authData != null;
        authToken = authData.authToken();
        return authData;
    }

    public void clear() throws DataAccessException {
        HttpRequest request = buildRequest("DELETE", "/db", null);
        sendRequest(request);
    }

    public GameData createGame(GameData gameData) throws DataAccessException {
        HttpRequest request = buildRequest("POST", "/game", gameData);
        var response = sendRequest(request);
        return handleResponse(response, GameData.class);
    }

    public void logoutUser() throws DataAccessException{
        HttpRequest request = buildRequest("DELETE", "/session", null);
        authToken = null;
        sendRequest(request);
    }

    public void joinGame(GameData gameData) throws DataAccessException {
        HttpRequest request = buildRequest("PUT", "/game", gameData);
        sendRequest(request);
    }

    public Collection<ListGameResponse> listGames() throws DataAccessException {
        HttpRequest request = buildRequest("GET", "/game", null);
        var response = sendRequest(request);
        return handleResponse(response, Collection.class);
    }

    private HttpRequest buildRequest(String method, String path, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverURL + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        if (authToken != null) {
            request.setHeader("authorization", authToken);
        }
        return request.build();
    }

    private HttpRequest.BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws DataAccessException{
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new DataAccessException(e.getMessage(), 400);
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws DataAccessException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                model.HttpResponse HttpResponse = new Gson().fromJson(body, model.HttpResponse.class);
                throw new DataAccessException (HttpResponse.message(), 400);
            }

            throw new DataAccessException("bad3", 400);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
