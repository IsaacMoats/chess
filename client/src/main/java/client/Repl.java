package client;

import exception.DataAccessException;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import static jdk.javadoc.internal.html.HtmlAttr.InputType.RESET;

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
            System.out.println("\n" + RESET + ">>>" + GREEN);
            String line = scanner.nextLine();
            try {
                result = eval(line);
                System.out.println(BLUE + result);
            } catch (Throwable e) {
                String msg = e.toString();
                System.out.println(msg);
            }
        }
        System.out.println();
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toUpperCase().split(" ");
            String cmd = tokens[0];
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
//                case "S" -> signIn(params);
//                case "R" -> registerUser(params);
                case "Q" -> "Quit";
//                case "C" -> createGame(params);
//                case "L" -> listGames();
//                case "J" -> joinGame(params);
//                case "C" -> clearGame();
            };
        } catch (DataAccessException ex) {
            return ex.getMessage();
        }
    }

    public String help(){
        if (Objects.equals(state, "signed out")) {
            return """
                    - (S)ign In <Username> <Password>
                    - (R)egister <Username> <Password> <email>
                    - (Q)uit
                    """;
        } else {
            return """
                    - (C)reate game <Game name>
                    - (L)ist games
                    - (J)oin game <Game ID> <Color>
                    - (C)lear all games
                    - (Q)uit
                    """;
        }
    }
}
