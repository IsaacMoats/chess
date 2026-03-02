package server;

import com.google.gson.Gson;
import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import dataaccess.GameDataAccess;
import dataaccess.UserDataAccess;
import io.javalin.*;
import io.javalin.http.Context;
import model.AuthData;
import model.GameData;
import model.UserData;
import service.UserService;

import java.util.Map;

public class Server {

    private final Javalin javalin;
    private final UserService userService = new UserService();

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
        userService.clearGame();
    }

    private void addUser(Context ctx) throws DataAccessException{
        UserData userData = new Gson().fromJson(ctx.body(), UserData.class);
        ctx.result(new Gson().toJson(userService.addUser(userData)));
        Map<String, String> headerMap = ctx.headerMap();
        System.out.println(headerMap);
    }

    private void listGames(Context ctx){
        String authToken = new Gson().fromJson(ctx.body(), String.class);
        ctx.result(authToken);
    }

    private void loginUser(Context ctx) throws DataAccessException{
        UserData userData = new Gson().fromJson(ctx.body(), UserData.class);
        AuthData authedLogin = userService.loginUser(userData);
        ctx.result(new Gson().toJson(authedLogin));
    }

    private void logoutUser(Context ctx) throws DataAccessException{
        String authToken = ctx.header("authorization");
        userService.logoutUser(authToken);
    }

    private void createGame(Context ctx) throws DataAccessException{
        String authToken = ctx.header("authorization");
        if (userService.authenticate(authToken)) {
            GameData gameName = new Gson().fromJson(ctx.body(), GameData.class);
            if (gameName.gameName() == null) {
                throw new DataAccessException("Needs a game name!", 400);
            }
            Integer gameID = userService.createGame(gameName);
            System.out.println("Name: " + gameName + "\n");
            System.out.println(gameID);
            ctx.result("{\"gameID\":\"" + gameID + "\"}");
        }
    }
}
