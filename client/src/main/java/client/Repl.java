package client;

import exception.DataAccessException;
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
            return "Welcome " + params[0] +". You have registered successfully and logged in.";
        }
        throw new DataAccessException("Expected: <Username> <Password> <email>", 400);
    }

    public String signIn(String... params) throws DataAccessException {
        if (params.length >= 2) {
            state = "signed in";
            UserData userData = new UserData(params[0], params[1], "email");
            server.loginUser(userData);
            return "Welcome back " + params[0] +". You have successfully logged in.";
        }
        throw new DataAccessException("Expected: <Username> <Password", 400);
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toUpperCase().split(" ");
            String cmd = tokens[0];
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "S" -> signIn(params);
                case "R" -> registerUser(params);
                case "Q" -> "Quit";
                case "C" -> "createGame(params);";
                case "L" -> "listGames();";
                case "J" -> "joinGame(params);";
                case "D" -> "clearGame();";
                case "H" -> help();
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
                    - (L)ist games
                    - (J)oin game <Game ID> <Color>
                    - (D)elete all games
                    - (Q)uit
                    - (H)elp
                    """;
        }
    }
}
