package websocket.messages;

import java.util.Objects;

public class ErrorMessage extends ServerMessage{
    String message;
    public ErrorMessage(ServerMessageType type, String message) {
        super(type);
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }

}
