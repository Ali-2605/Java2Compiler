import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import ast.Program;
import lexer.LexicalException;
import lexer.Scanner;
import lexer.Token;
import lexer.TokenPrinter;
import parser.ParseException;
import parser.Parser;
import runtime.EvalException;
import runtime.Interpreter;

public class Main {
    public static void main(String[] args) {
        boolean printTokens = false;
        String path = null;

        for (String arg : args) {
            if (arg.equals("--tokens") || arg.equals("-t")) {
                printTokens = true;
            } else if (path == null) {
                path = arg;
            }
        }

        if (path != null) {
            runFile(path, printTokens);
        } else {
            runPrompt(printTokens);
        }
    }

    private static void runFile(String path, boolean printTokens) {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(path));
            run(new String(bytes), printTokens);
        } catch (IOException e) {
            System.err.println("Error: Could not read file " + path);
        }
    }

    private static void runPrompt(boolean printTokens) {
        // Quick demo string for local testing
        String demo = "x :- 10 ~\n" +
                     "leviosa -> x + 5 -<\n" +
                     "isValid :- :) ~";
        System.out.println("Running Demo Mode:\n" + demo + "\n");
        run(demo, printTokens);
    }

    private static void run(String source, boolean printTokens) {
        try {
            Scanner scanner = new Scanner(source);
            List<Token> tokens = scanner.scanTokens();

            if (printTokens) {
                TokenPrinter.printTokens(tokens);
            }

            Parser parser = new Parser(tokens);
            Program program = parser.parseProgram();

            if (!parser.getErrors().isEmpty()) {
                for (ParseException error : parser.getErrors()) {
                    System.err.println(error.getMessage());
                }
                return;
            }

            Interpreter interpreter = new Interpreter();
            interpreter.execute(program);
        } catch (LexicalException | EvalException e) {
            System.err.println(e.getMessage());
        } catch (Exception e) {
            System.err.println("System Failure: " + e.getMessage());
        }
    }
}