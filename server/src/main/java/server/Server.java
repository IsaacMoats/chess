package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import model.AuthData;
import model.GameData;
import model.UserData;
import service.JoinGameRequest;
import service.UserService;

import java.util.Map;
import java.util.Objects;

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
                .put("/game", this::joinGame)
                .exception(DataAccessException.class, this::exceptionHandler);

        // Register your endpoints and exception handlers here.

    }

    public int run(int desiredPort) {
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException ex) {
            System.out.println("failed to create database");
        }
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

    private void listGames(Context ctx) throws DataAccessException {
        String authToken = ctx.header("authorization");
        if (userService.authenticate(authToken)) {
            if (userService.listGames() == null){
                ctx.result("");
            } else {
                ctx.result("{\"games\" : " + new Gson().toJson(userService.listGames()) + "}");
            }
        }
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

            Integer gameID = userService.createGame(gameName);
            ctx.result("{\"gameID\":\"" + gameID + "\"}");
        }
    }

    private void joinGame(Context ctx) throws DataAccessException{
        String authToken = ctx.header("authorization");
        if (userService.authenticate(authToken)){
            JoinGameRequest game = new Gson().fromJson(ctx.body(), JoinGameRequest.class);
            if (!Objects.equals(game.playerColor(), "WHITE")) {
                if (!Objects.equals(game.playerColor(), "BLACK")) {
                    throw new DataAccessException("Choose white or black!", 400);
                }
            }
            String user = userService.getUser(authToken);
            userService.joinGame(user, game.playerColor(), game.gameID());
        }
    }
}
