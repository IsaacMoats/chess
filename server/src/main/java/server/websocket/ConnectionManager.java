package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import server.Server;
import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    //Hash of every game listed by gameID and the list of sessions connected to that game (watching or playing).
    public final ConcurrentHashMap<Integer, Set<Session>> connections = new ConcurrentHashMap<>();

    //Add a new session to the hash.
    public void add(int gameID, Session session){
        Set<Session> sessions = ConcurrentHashMap.newKeySet();
        if (connections.get(gameID) != null) {
            sessions = connections.get(gameID);
            sessions.add(session);
        } else {
            sessions.add(session);
        }
        
        connections.put(gameID, sessions);
    }
    public void delete(int gameID, Session session) {
        Set<Session> sessions = connections.get(gameID);
        sessions.remove(session);
        connections.put(gameID, sessions);
    }
    private final Gson gson = new Gson();

    public void broadcast(Session exclude, ServerMessage message, int gameId, ChessGame game) throws IOException {
        String json;
        if (message.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
            ServerMessage loadGameMessage = new ServerMessage(
                    ServerMessage.ServerMessageType.NOTIFICATION);
            loadGameMessage.setMessage("Player has joined the game");
            json = gson.toJson(loadGameMessage);
        } else {
            json = gson.toJson(message);
        }

        for (Session s : connections.getOrDefault(gameId, Set.of())) {
            if (s.isOpen() && !s.equals(exclude)) {
                s.getRemote().sendString(json);
            }
        }
    }
    public void sendSelf(Session session, ServerMessage message, ChessGame game) throws IOException {
        String json;
        if (message.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
            websocket.messages.LoadGameMessage loadGameMessage = new LoadGameMessage(game);
            json = gson.toJson(loadGameMessage);
        } else {
            json = gson.toJson(message);
        }
        session.getRemote().sendString(json);
    }

}
