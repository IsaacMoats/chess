package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, ArrayList<Session>> connections = new ConcurrentHashMap<>();
    public void add(int gameID, Session session){
        ArrayList<Session> sessions = connections.get(gameID);
        sessions.add(session);
        connections.put(gameID, sessions);
    }
    public void delete(int gameID, Session session) {
        ArrayList<Session> sessions = connections.get(gameID);
        sessions.remove(session);
        connections.put(gameID, sessions);
    }
}
