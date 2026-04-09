package client.websocket;

import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

public interface NotificationHandler {
    void loadGame(LoadGameMessage message);
    void notification(NotificationMessage message);
    void error(ErrorMessage message);
}
