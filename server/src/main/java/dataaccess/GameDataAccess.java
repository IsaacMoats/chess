package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.HashMap;

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
    // Read (get)
    public int getGameID(GameData gameData){return gameData.gameID();}
    public String getWhiteUsername(GameData gameData){return gameData.whiteUsername();}
    public String getBlackUsername(GameData gameData){return gameData.blackUsername();}
    public String getGameName(GameData gameData){return gameData.gameName();}
    public ChessGame getGame(GameData gameData){return gameData.game();}
    // Update (set)
    // It is possible that it would be better to override the old GameData record with the new one rather than leaving it with old data
    public void setGameID(GameData gameData, int gameID){
        GameData newGameData = new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), gameData.game());
    }
    public void setWhiteUsername(GameData gameData, String whiteUsername){
        GameData newGameData = new GameData(gameData.gameID(), whiteUsername, gameData.blackUsername(), gameData.gameName(), gameData.game());
    }
    public void setBlackUsername(GameData gameData, String blackUsername){
        GameData newGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), blackUsername, gameData.gameName(), gameData.game());
    }
    public void setGameName(GameData gameData, String gameName){
        GameData newGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameName, gameData.game());
    }
    public void  setGame(GameData gameData, ChessGame game){
        GameData newGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
    }
    // Delete
    // Idea: set fields to void
    public void deleteGameData(){
        gameDataHash = new HashMap<>();
    }
}
