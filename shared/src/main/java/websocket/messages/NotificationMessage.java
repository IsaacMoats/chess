package websocket.messages;

public class NotificationMessage {
    private final ServerMessage.ServerMessageType serverMessageType = ServerMessage.ServerMessageType.NOTIFICATION;
    private final String message;
    public NotificationMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
