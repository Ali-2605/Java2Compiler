import lexer.Scanner;
import lexer.Token;
import lexer.TokenType;
import lexer.LexicalException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import lexer.TokenPrinter;

public class Main {
    public static void main(String[] args) {
        if (args.length > 0) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    private static void runFile(String path) {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(path));
            run(new String(bytes));
        } catch (IOException e) {
            System.err.println("Error: Could not read file " + path);
        }
    }

    private static void runPrompt() {
        // Quick demo string for local testing
        String demo = "x :- 10 ~\n" +
                     "leviosa -> x + 5 -<\n" +
                     "isValid :- :) ~";
        System.out.println("Running Demo Mode:\n" + demo + "\n");
        run(demo);
    }

    private static void run(String source) {
        try {
            TokenPrinter.printTokens(source);
        } catch (Exception e) {
            System.err.println("System Failure: " + e.getMessage());
        }
    }
}