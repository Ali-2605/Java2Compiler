package tests;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lexer.LexicalException;
import lexer.Scanner;
import lexer.Token;
import parser.ParseException;
import parser.Parser;

public class TestRunner {
    private static final Path CASES_DIR = Path.of("tests", "cases");
    private static final Path EXPECTED_DIR = Path.of("tests", "expected");

    public static void main(String[] args) throws IOException {
        List<Path> cases = new ArrayList<>();
        try (var stream = Files.list(CASES_DIR)) {
            stream.filter(path -> path.toString().endsWith(".txt"))
                    .sorted(Comparator.comparing(Path::toString))
                    .forEach(cases::add);
        }

        int passed = 0;
        int failed = 0;

        for (Path testCase : cases) {
            String name = stripExtension(testCase.getFileName().toString());
            String source = Files.readString(testCase);
            String actual = buildOutput(name, source);
            Path expectedPath = EXPECTED_DIR.resolve(name + ".out");
            String expected = Files.readString(expectedPath);

            System.out.println(actual);

            if (equalsNormalized(expected, actual)) {
                System.out.println("[PASS] " + name);
                passed++;
            } else {
                System.out.println("[FAIL] " + name);
                printMismatch(expected, actual);
                failed++;
            }

            System.out.println();
        }

        System.out.println("Total: " + (passed + failed) + ", Passed: " + passed + ", Failed: " + failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static String buildOutput(String name, String source) {
        StringBuilder output = new StringBuilder();
        output.append("CASE: ").append(name).append("\n");
        try {
            Scanner scanner = new Scanner(source);
            List<Token> tokens = scanner.scanTokens();
            output.append("TOKENS:\n");
            for (Token token : tokens) {
                output.append(formatToken(token)).append("\n");
            }

            Parser parser = new Parser(tokens);
            parser.parseProgram();
            if (parser.getErrors().isEmpty()) {
                output.append("PARSER: OK\n");
            } else {
                output.append("PARSER_ERRORS:\n");
                for (ParseException error : parser.getErrors()) {
                    output.append(stripPosition(error.getMessage())).append("\n");
                }
            }
        } catch (LexicalException ex) {
            output.append("LEXER_ERROR: ").append(stripPosition(ex.getMessage())).append("\n");
        }
        return output.toString();
    }

    private static String formatToken(Token token) {
        String literal = token.getLiteral() == null ? "null" : token.getLiteral().toString();
        return token.getType() + " | " + token.getLexeme() + " | " + literal;
    }

    private static String stripPosition(String message) {
        int index = message.lastIndexOf(" at ");
        if (index == -1) {
            return message;
        }
        return message.substring(0, index);
    }

    private static String stripExtension(String name) {
        int index = name.lastIndexOf('.');
        return index == -1 ? name : name.substring(0, index);
    }

    private static boolean equalsNormalized(String expected, String actual) {
        return normalize(expected).equals(normalize(actual));
    }

    private static String normalize(String text) {
        String normalized = text.replace("\r\n", "\n").replace("\r", "\n");
        if (!normalized.endsWith("\n")) {
            normalized += "\n";
        }
        return normalized;
    }

    private static void printMismatch(String expected, String actual) {
        String[] expLines = normalize(expected).split("\n", -1);
        String[] actLines = normalize(actual).split("\n", -1);
        int max = Math.max(expLines.length, actLines.length);
        for (int i = 0; i < max; i++) {
            String exp = i < expLines.length ? expLines[i] : "<no line>";
            String act = i < actLines.length ? actLines[i] : "<no line>";
            if (!exp.equals(act)) {
                System.out.println("First mismatch at line " + (i + 1));
                System.out.println("Expected: " + exp);
                System.out.println("Actual:   " + act);
                return;
            }
        }
    }
}
