package client;

import chess.*;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import exception.DataAccessException;
import model.*;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;
import static ui.EscapeSequences.*;

public class Repl implements NotificationHandler {
    private AuthData authData = null;
    private ChessGame currentGame;
    private int gameID;
    private final ServerFacade server;
    private String state = "signed out";
    private int gameTotal = 0;
    private WebSocketFacade ws;
    private String color = null;
    private String serverUrl = null;

    public Repl(String serverUrl) throws DataAccessException {
        server = new ServerFacade(serverUrl);
        ws = null;
        this.serverUrl = serverUrl;
    }

    public void run() {
        System.out.println("Welcome to CS 240 Chess. Sign in to start.");
        System.out.println(help());

        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (!result.equals("QUIT")) {
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
            ws = new WebSocketFacade(serverUrl, this);
            this.authData = server.loginUser(userData);
            return "Welcome " + params[0] +". You have registered successfully and logged in.";
        }
        throw new DataAccessException("Expected: <Username> <Password> <email>", 400);
    }

    public String signIn(String... params) throws DataAccessException {
        if (params.length == 2) {
            UserData userData = new UserData(params[0], params[1], "email");
            this.authData = server.loginUser(userData);

            state = "signed in";
            ws = new WebSocketFacade(serverUrl, this);
            return "Welcome back " + params[0] +". You have successfully logged in.";
        } else {
            throw new DataAccessException("Expected: <Username> <Password>", 400);
        }
    }

    public String createGame(String... params) throws DataAccessException {
        if (params.length >= 1) {
            GameData gameData = new GameData(null, null, null, params[0], null);
            try {
                server.createGame(gameData);
                gameTotal += 1;
                return params[0] + " game created successfully.";
            } catch (DataAccessException ex) {
                return "Not logged in! Must log in to create game";
            }
        }
        throw new DataAccessException("Expected: <Game name>", 400);
    }

    public String logout() throws DataAccessException {
        if (!Objects.equals(state, "signed in")) {
            return "Must be logged in to log out!";
        }
        server.logoutUser();
        state = "signed out";
        authData = null;
        return "Successfully logged out.";
    }

    public String listGames() throws DataAccessException {
        if (!Objects.equals(state, "signed in") && !Objects.equals(state, "in game")) {
            return "Must be logged in to list games!";
        }
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

    public String joinGame(String... params) throws DataAccessException, IOException {
        if (params.length != 2) {
            return "Expected <GameID> <WHITE|BLACK>";
        }
        if (!Objects.equals(state, "signed in") && !Objects.equals(state, "in game")) {
            return "Must be logged in to join games!";
        }
        try {
            Integer.parseInt(params[0]);
        } catch (NumberFormatException e){
            return "Please input a valid number (1, 2, 3 ...) for the game to join";
        }
        int gameID = Integer.parseInt(params[0]);
        int counter = 0;
        var games = server.listGames();
        for (ListGameResponse gameResponse : games.games()) {
            counter++;
        }
        gameTotal = counter;
        if (gameID < 0 || gameID > gameTotal) {
            return "Choose a valid game from the list!";
        }
        JoinGameRequest joinGameRequest = new JoinGameRequest(params[1], gameID);
        ChessGame game = server.joinGame(joinGameRequest);
        this.currentGame = game;
        this.gameID = gameID;
        String printable = "";
        ChessBoard board = game.getBoard();
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
            this.color = "black";
        } else if (Objects.equals(params[1], "WHITE")) {
            this.color = "white";
            printable = printable.concat(printWhiteOnly(board));
        }
        ws.enterGame(server.authToken, gameID);
        state = "in game";
        return "Joining";
    }

    private String printWhiteOnly(ChessBoard board) {
        String printable = "";
        printable = printable.concat(RESET_BG_COLOR + "\s\sA\s\sB\s\sC\s\sD\s\sE\s\sF\s\sG\s\sH\n");
        for (int i = 8; i > 0; i--) {
            printable = printable.concat(RESET_BG_COLOR + i);
            for (int j = 1; j < 9; j++) {
                printable = printable.concat(printBoard(board, i, j));
            }
            printable = printable.concat(RESET_BG_COLOR + i + "\n");
        }
        printable = printable.concat(RESET_BG_COLOR + "\s\sA\s\sB\s\sC\s\sD\s\sE\s\sF\s\sG\s\sH\n");
        return printable;
    }


    public String watch(String... params) throws DataAccessException, IOException {
        if (params.length != 1) {
            return "Input the number of a game to watch.";
        }
        if (!Objects.equals(state, "signed in") && !Objects.equals(state, "in game")) {
            return "Must be logged in to watch games!";
        }
        try {
            Integer.parseInt(params[0]);
        } catch (NumberFormatException e){
            return "Please input a valid number (1, 2, 3 ...) for the game to watch";
        }
        int gameID = Integer.parseInt(params[0]);
        int counter = 0;
        var games = server.listGames();
        for (ListGameResponse gameResponse : games.games()) {
            counter++;
        }
        gameTotal = counter;
        if (gameID < 0 || gameID > gameTotal) {
            return "Choose a valid game from the list!";
        }
        ChessGame game = new ChessGame();
        ChessBoard board = game.getBoard();
        ws.enterGame(server.authToken, gameID);
//        ws.sendUserCommand(new UserGameCommand(
//             UserGameCommand.CommandType.CONNECT,
//               authData.authToken(),
//               gameID));
        return "Watching now";
    }

    public String leave() throws IOException {
        if (authData == null) {
            return "Must sign in to leave game.";
        }
        ws.sendUserCommand(new UserGameCommand(
                UserGameCommand.CommandType.LEAVE,
                authData.authToken(),
                this.gameID));
        state = "signed in";
        return "Left game.";
    }

    public String move() throws Exception {
        if (currentGame.getOver()) {
            return "The game is over.";
        }
        System.out.println("Starting position: ");
        String start = new Scanner(System.in).nextLine();
        System.out.println("End position: ");
        String end = new Scanner(System.in).nextLine();

        try {
            ChessPosition startPosition = makePosition(start);
            ChessPosition endPosition = makePosition(end);
            ChessPiece.PieceType promotion = null;
            ChessPiece piece = currentGame.getBoard().getPiece(startPosition);
            if (piece != null && piece.getPieceType() == ChessPiece.PieceType.PAWN) {
                if (endPosition.getRow() == 8 || endPosition.getRow() == 1) {
                    System.out.println("Promote the pawn to a queen, rook, bishop, or knight.");
                    promotion = ChessPiece.PieceType.valueOf(new Scanner(System.in).nextLine().trim().toUpperCase());
                }
            }
            ChessMove move = new ChessMove(startPosition, endPosition, promotion);
            ws.sendUserCommand(new MakeMoveCommand
                    (UserGameCommand.CommandType.MAKE_MOVE, authData.authToken(), gameID, move));

        } catch (Exception e) {
            return e.getMessage();
        }
        return "Move sent";
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toUpperCase().split(" ");
            String cmd = tokens[0];
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "LOGIN" -> signIn(params);
                case "REGISTER" -> registerUser(params);
                case "QUIT" -> "QUIT";
                case "CREATE" -> createGame(params);
                case "LOGOUT" -> logout();
                case "LIST" -> listGames();
                case "JOIN" -> joinGame(params);
//                case "D" -> clearGame();
                case "HELP" -> help();
                case "WATCH" -> watch(params);
                case "MOVE" -> move();
                case "LEAVE" -> leave();
                case "REDRAW" -> redraw();
                default -> throw new IllegalStateException("Not on options list: " + cmd);
            };
        } catch (Throwable ex) {
            return ex.getMessage();
        }
    }

    public String redraw() {
        ChessBoard board = this.currentGame.getBoard();
        String printable = "";
        if (Objects.equals(this.color, "black")) {
            printable = printable.concat(RESET_BG_COLOR + "\s\sH\s\sG\s\sF\s\sE\s\sD\s\sC\s\sB\s\sA\n");
            for (int i = 1; i < 9; i++){
                printable = printable.concat(RESET_BG_COLOR + i);
                for(int j = 8; j > 0; j--){
                    printable = printable.concat(printBoard(board, i, j));
                }
                printable = printable.concat(RESET_BG_COLOR + i + "\n");
            }
            printable = printable.concat(RESET_BG_COLOR + "\s\sH\s\sG\s\sF\s\sE\s\sD\s\sC\s\sB\s\sA\n");
        } else if (Objects.equals(this.color, "white")) {
            printable = printable.concat(printWhiteOnly(board));
        }
        return printable;
    }

    public String help(){
        if (Objects.equals(state, "signed out")) {
            return """
                    - Login <Username> <Password>
                    - Register <Username> <Password> <email>
                    - Quit
                    - Help
                    """;
        } else if (Objects.equals(state, "signed in")){
            return """
                    - Create <Game name>
                    - Logout
                    - (List) games
                    - Join <Game ID> <Color>
                    - Watch <Game ID>
                    - Quit
                    - Help
                    """;
        } else {
            return """
                    - Help
                    - Redraw Board
                    - Leave
                    - Move
                    - Resign
                    - Highlight legal moves
                    """;
        }
    }


    @Override
    public void loadGame(LoadGameMessage message) {
        this.currentGame = message.getGame();
        ChessBoard board = this.currentGame.getBoard();
        String printable = "";
        if (Objects.equals(this.color, "black")) {
            printable = printable.concat(RESET_BG_COLOR + "\s\sH\s\sG\s\sF\s\sE\s\sD\s\sC\s\sB\s\sA\n");
            for (int i = 1; i < 9; i++){
                printable = printable.concat(RESET_BG_COLOR + i);
                for(int j = 8; j > 0; j--){
                    printable = printable.concat(printBoard(board, i, j));
                }
                printable = printable.concat(RESET_BG_COLOR + i + "\n");
            }
            printable = printable.concat(RESET_BG_COLOR + "\s\sH\s\sG\s\sF\s\sE\s\sD\s\sC\s\sB\s\sA\n");
        } else if (Objects.equals(this.color, "white")) {
            printable = printable.concat(printWhiteOnly(board));
        }
        System.out.println(printable);
        System.out.print(message.getGame());
    }

    @Override
    public void notification(NotificationMessage message) {
        System.out.println(message.getMessage());
    }

    @Override
    public void error(ErrorMessage message) {
        System.out.println(message.getErrorMessage());
    }

    private ChessPosition makePosition(String input) {
        input = input.trim().toLowerCase();
        int col = input.charAt(0) - 'a' + 1;
        int row = input.charAt(1) - '0';
        return new ChessPosition(row, col);
    }
}
