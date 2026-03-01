package server;

import com.google.gson.Gson;
import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import dataaccess.GameDataAccess;
import dataaccess.UserDataAccess;
import io.javalin.*;
import io.javalin.http.Context;
import model.AuthData;
import model.UserData;
import service.UserService;

import java.util.Map;

public class Server {

    private final Javalin javalin;
    private final UserService userService = new UserService();
    private final UserDataAccess userDataAccess = new UserDataAccess();
    private final AuthDataAccess authDataAccess = new AuthDataAccess();
    private final GameDataAccess gameDataAccess = new GameDataAccess();
    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", this::addUser)
                .delete("/db", this::clear)
                .get("/game", this::listGames)
                .post("/session", this::loginUser)
                .delete("/session", this::logoutUser)
                .post("/game", this::createGame)
                .exception(DataAccessException.class, this::exceptionHandler);

        // Register your endpoints and exception handlers here.

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void exceptionHandler(DataAccessException dataAccessException, Context ctx){
        var body = new Gson().toJson(Map.of("message", String.format("Error: %s", dataAccessException.getMessage()), "success", false));
        ctx.status(dataAccessException.getCode());
        ctx.json(body);
    }

    private void clear(Context ctx){
        userDataAccess.deleteUserData();
        gameDataAccess.deleteGameData();
        authDataAccess.deleteAuthData();
    }

    private void addUser(Context ctx) throws DataAccessException{
        UserData userData = new Gson().fromJson(ctx.body(), UserData.class);
        ctx.result(new Gson().toJson(userService.addUser(userData)));
    }

    private void listGames(Context ctx){
        String authToken = new Gson().fromJson(ctx.body(), String.class);
        ctx.result(authToken);
    }

    private void loginUser(Context ctx) throws DataAccessException{
        UserData userData = new Gson().fromJson(ctx.body(), UserData.class);
        System.out.println("Input Data: " + userData);
        ctx.result(new Gson().toJson(userService.loginUser(userData)));
    }

    private void logoutUser(Context ctx) throws DataAccessException{
        String authToken = ctx.header("authorization");
        Map<String, String> headerMap = ctx.headerMap();
        System.out.println(authToken);
        System.out.println(headerMap);
        userService.logoutUser(authToken);
    }

    private void createGame(Context ctx){
        String gameName = new Gson().fromJson(ctx.body(), String.class);
        ctx.result(new Gson().toJson(userService.createGame(gameName)));
    }
}
