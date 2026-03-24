package client;

import chess.*;
import exception.DataAccessException;

public class ClientMain {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        String serverUrl = "http://localhost:8080";
        try {
            new Repl(serverUrl).run();
        } catch (Throwable ex) {
            System.out.println("Unable to start server");
        }

    }
}
