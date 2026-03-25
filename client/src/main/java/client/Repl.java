package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import exception.DataAccessException;
import model.GameData;
import model.JoinGameRequest;
import model.ListGameResponse;
import model.UserData;
import server.ServerFacade;
import ui.EscapeSequences;

import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;
import static ui.EscapeSequences.*;
import static java.awt.Color.GREEN;

public class Repl {
    private final ServerFacade server;
    private String state = "signed out";

    public Repl(String serverUrl) throws DataAccessException {
        server = new ServerFacade(serverUrl);
    }

    public void run() {
        System.out.println("Welcome to CS 240 Chess. Sign in to start.");
        System.out.println(help());

        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (!result.equals("Q")) {
            System.out.println("\n >>> ");
            String line = scanner.nextLine();
            try {
                result = eval(line);
                System.out.println(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                String msg = e.toString();
                System.out.println(msg);
            }
        }
        System.out.println();
    }

    public String registerUser(String... params) throws DataAccessException {
        if (params.length >= 3) {
            state = "signed in";
            UserData userData = new UserData(params[0], params[1], params[2]);
            server.addUser(userData);
            server.loginUser(userData);
            return "Welcome " + params[0] +". You have registered successfully and logged in.";
        }
        throw new DataAccessException("Expected: <Username> <Password> <email>", 400);
    }

    public String signIn(String... params) throws DataAccessException {
        if (params.length >= 2) {
            UserData userData = new UserData(params[0], params[1], "email");
            server.loginUser(userData);
            state = "signed in";
            return "Welcome back " + params[0] +". You have successfully logged in.";
        }
        throw new DataAccessException("Expected: <Username> <Password", 400);
    }

    public String createGame(String... params) throws DataAccessException {
        if (params.length >= 1) {
            GameData gameData = new GameData(null, null, null, params[0], null);
            try {
                server.createGame(gameData);
                return params[0] + " game created successfully.";
            } catch (DataAccessException ex) {
                return "Not logged in! Must log in to create game";
            }
        }
        throw new DataAccessException("Expected: <Game name>", 400);
    }

    public String logout() throws DataAccessException {

        state = "signed out";
        server.logoutUser();
        return "Successfully logged out.";
    }

    public String listGames() throws DataAccessException {
        var games = server.listGames();
        String printable = "";
        for (ListGameResponse gameResponse : games.games()) {
            printable = printable.concat("Name: " + gameResponse.gameName());
            if (gameResponse.whiteUsername() == null) {
                printable = printable.concat("\tWhite Player: No player entered yet!");
            } else {
                printable = printable.concat("\tWhite Player: " + gameResponse.whiteUsername());
            }
            if (gameResponse.blackUsername() == null) {
                printable = printable.concat("\tBlack Player: No player entered yet!");
            } else {
                printable = printable.concat("\tBlack Player: " + gameResponse.blackUsername());
            }
            printable = printable.concat("\tGameID: " + gameResponse.gameID() + "\n");
        }
        if (Objects.equals(printable, "")) {
            return "No games yet!";
        }
            return printable;
    }

    private String printBoard(ChessBoard board, int i, int j) {
        String printable = "";
        ChessPosition position = new ChessPosition(i, j);
        ChessPiece piece = board.getPiece(position);
        if (((i%2) != 0 && (j%2)!=0) || ((i%2) == 0 && (j%2) == 0)) {
            printable = printable.concat(SET_BG_COLOR_BLACK);
        } else  {
            printable = printable.concat(SET_BG_COLOR_WHITE);
        }
        if (piece == null) {
            printable = printable.concat(EMPTY);
        } else if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                printable = printable.concat(WHITE_KING);
            } else if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
                printable = printable.concat(WHITE_ROOK);
            } else if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
                printable = printable.concat(WHITE_BISHOP);
            } else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
                printable = printable.concat(WHITE_QUEEN);
            } else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
                printable = printable.concat(WHITE_KNIGHT);
            } else if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
                printable = printable.concat(WHITE_PAWN);
            }
        } else {
            if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                printable = printable.concat(BLACK_KING);
            } else if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
                printable = printable.concat(BLACK_ROOK);
            } else if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
                printable = printable.concat(BLACK_BISHOP);
            } else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
                printable = printable.concat(BLACK_QUEEN);
            } else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
                printable = printable.concat(BLACK_KNIGHT);
            } else if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
                printable = printable.concat(BLACK_PAWN);
            }
        }
        return printable;
    }

    public String joinGame(String... params) throws DataAccessException {
        int gameID = Integer.parseInt(params[0]);
        JoinGameRequest joinGameRequest = new JoinGameRequest(params[1], gameID);
        ChessGame game = server.joinGame(joinGameRequest);
        String printable = "";
        ChessBoard board = game.getBoard();
        //For black
        if (Objects.equals(params[1], "BLACK")) {
            printable = printable.concat(RESET_BG_COLOR + "\s\sH\s\sG\s\sF\s\sE\s\sD\s\sC\s\sB\s\sA\n");
            for (int i = 1; i < 9; i++){
                printable = printable.concat(RESET_BG_COLOR + i);
                for(int j = 8; j > 0; j--){
                    printable = printable.concat(printBoard(board, i, j));
                }
                printable = printable.concat(RESET_BG_COLOR + i + "\n");
            }
            printable = printable.concat(RESET_BG_COLOR + "\s\sH\s\sG\s\sF\s\sE\s\sD\s\sC\s\sB\s\sA\n");
        } else if (Objects.equals(params[1], "WHITE")){
            printable = printable.concat(RESET_BG_COLOR + "\s\sA\s\sB\s\sC\s\sD\s\sE\s\sF\s\sG\s\sH\n");
            for (int i = 8; i > 0; i--){
                printable = printable.concat(RESET_BG_COLOR + i);
                for (int j = 1; j < 9; j++) {
                    printable = printable.concat(printBoard(board, i, j));
                }
                printable = printable.concat(RESET_BG_COLOR + i + "\n");
            }
            printable = printable.concat(RESET_BG_COLOR + "\s\sA\s\sB\s\sC\s\sD\s\sE\s\sF\s\sG\s\sH\n");
        }

        return printable;

    }

    public String clearGame() throws DataAccessException {
        server.clear();
        state = "signed out";
        return "Game cleared! All ";
    }

    public String watch(params) thro

    public String eval(String input) {
        try {
            String[] tokens = input.toUpperCase().split(" ");
            String cmd = tokens[0];
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "S" -> signIn(params);
                case "R" -> registerUser(params);
                case "Q" -> "Quit";
                case "C" -> createGame(params);
                case "L" -> logout();
                case "LI" -> listGames();
                case "J" -> joinGame(params);
                case "D" -> clearGame();
                case "H" -> help();
                case "W" -> watch(params);
                default -> throw new IllegalStateException("Unexpected value: " + cmd);
            };
        } catch (Throwable ex) {
            return ex.getMessage();
        }
    }

    public String help(){
        if (Objects.equals(state, "signed out")) {
            return """
                    - (S)ign In <Username> <Password>
                    - (R)egister <Username> <Password> <email>
                    - (Q)uit
                    - (H)elp
                    """;
        } else {
            return """
                    - (C)reate game <Game name>
                    - (L)ogout
                    - (Li)st games
                    - (J)oin game <Game ID> <Color>
                    - (W)atch game <Game ID>
                    - (D)elete all games
                    - (Q)uit
                    - (H)elp
                    """;
        }
    }
}
