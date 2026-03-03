package dataaccess;

import chess.ChessGame;
import model.GameData;
import service.ListGameResponse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

public class GameDataAccess {
    private HashMap<Integer, GameData> gameDataHash = new HashMap<>();
    private int gameID = 1;
    // Following CRUD operations
    // Create
    public Integer createGameData(String whiteUsername, String blackUsername, String gameName, ChessGame game){
        GameData gameData = new GameData(this.gameID, whiteUsername, blackUsername, gameName, game);
        gameDataHash.put(this.gameID, gameData);
        Integer oldGameID = gameID;
        gameID++;
        return oldGameID;
    }

    public void joinGame(String user, String color, Integer gameID) throws DataAccessException {
        if (gameID == null) {
            throw new DataAccessException("No gameID given!", 400);
        }
        if (Objects.equals(color, "BLACK")) {
            if (gameDataHash.get(gameID).blackUsername() != null){
                throw new DataAccessException("Color already taken!", 403);
            }
            GameData updatedGame = new GameData(gameID, gameDataHash.get(gameID).whiteUsername(), user, gameDataHash.get(gameID).gameName(), gameDataHash.get(gameID).game());
            gameDataHash.replace(gameID, updatedGame);
        } else {
            if (gameDataHash.get(gameID).whiteUsername() != null){
                throw new DataAccessException("Color already taken!", 403);
            }
            GameData updatedGame = new GameData(gameID, user, gameDataHash.get(gameID).blackUsername(), gameDataHash.get(gameID).gameName(), gameDataHash.get(gameID).game());
            gameDataHash.replace(gameID, updatedGame);
        }

    }
    // Read (get)
    // Delete
    // Idea: set fields to void
    public void deleteGameData(){
        gameDataHash = new HashMap<>();
    }

    public Collection<ListGameResponse> listGames() {
        ArrayList<ListGameResponse> games = new ArrayList<>();
        for (GameData gameData : gameDataHash.values()){
            ListGameResponse game = new ListGameResponse(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName());
            games.add(game);
        }
        return games;
    }
}
