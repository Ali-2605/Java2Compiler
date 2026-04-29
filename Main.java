
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import ast.ASTPrinter;
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
        boolean printAST = false; // Added a flag for AST visualization
        String path = null;

        for (String arg : args) {
            switch (arg) {
                case "-t" -> printTokens = true;
                case "-a" -> printAST = true; // New flag for the AST Printer
                default -> {
                    if (path == null) path = arg;
                }
            }
        }

        if (path != null) {
            runFile(path, printTokens, printAST);
        } else {
            System.err.println("Usage: java Main [-t] [-a] <file.txt>");
        }
    }

    private static void runFile(String path, boolean printTokens, boolean printAST) {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(path));
            run(new String(bytes), printTokens, printAST);
        } catch (IOException e) {
            System.err.println("Error: Could not read file " + path);
        }
    }

    private static void run(String source, boolean printTokens, boolean printAST) {
        try {
            // 1. Lexical Analysis
            Scanner scanner = new Scanner(source);
            List<Token> tokens = scanner.scanTokens();

            if (printTokens) {
                // Assuming TokenPrinter.printTokens accepts List<Token>
                TokenPrinter.printTokens(tokens);
            }

            // 2. Syntax Analysis
            Parser parser = new Parser(tokens);
            Program program = parser.parseProgram();

            if (!parser.getErrors().isEmpty()) {
                for (ParseException error : parser.getErrors()) {
                    System.err.println("[Syntax Error] " + error.getMessage());
                }
                return;
            }

            // 3. Optional AST Visualization (Your Task D)
            if (printAST) {
                ASTPrinter printer = new ASTPrinter();
                printer.print(program);
            }

            // 4. Execution
            Interpreter interpreter = new Interpreter();
            interpreter.execute(program);

        } catch (LexicalException | EvalException e) {
            System.err.println(e.getMessage());
        } catch (Exception e) {
            System.err.print("System Failure: ");
            e.printStackTrace(); // Better for debugging than just getMessage()
        }
    }
}