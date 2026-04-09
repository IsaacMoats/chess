package websocket.messages;

import java.util.Objects;

public class ErrorMessage extends ServerMessage{
    String errorMessage;
    public ErrorMessage(ServerMessageType type, String message) {
        super(type);
        this.errorMessage = message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return errorMessage;
    }

}
