package websocket.messages;

import chess.ChessGame;

import java.util.Objects;

public class LoadGameMessage {
    private final ServerMessage.ServerMessageType serverMessageType;
    private ChessGame game;

    public LoadGameMessage(ServerMessage.ServerMessageType type, ChessGame game) {
        this.serverMessageType = type;
        this.game = game;
    }

    public ServerMessage.ServerMessageType getServerMessageType() {
        return serverMessageType;
    }

    public ChessGame getGame() {
        return game;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LoadGameMessage that = (LoadGameMessage) o;
        return serverMessageType == that.serverMessageType && Objects.equals(game, that.game);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serverMessageType, game);
    }
}
