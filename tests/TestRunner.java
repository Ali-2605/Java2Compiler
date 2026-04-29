package tests;

import ast.Program;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

    private static final class TestResult {
        private final String name;
        private final boolean passed;
        private final String details;

        private TestResult(String name, boolean passed, String details) {
            this.name = name;
            this.passed = passed;
            this.details = details;
        }
    }

    public static void main(String[] args) {
        List<Path> files = discoverCaseFiles();
        if (files.isEmpty()) {
            System.out.println("No test case files found in " + CASES_DIR);
            return;
        }

        List<TestResult> results = new ArrayList<>();
        for (Path file : files) {
            results.add(runCase(file));
        }

        int passed = 0;
        int failed = 0;
        for (TestResult result : results) {
            System.out.println("\nCASE: " + result.name);
            if (result.passed) {
                passed++;
                System.out.println("[PASS] " + result.name + result.details);
            } else {
                failed++;
                System.out.println("[FAIL] " + result.name + result.details);
            }
        }

        System.out.println("\nTotal: " + results.size() + ", Passed: " + passed + ", Failed: " + failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static List<Path> discoverCaseFiles() {
        List<Path> files = new ArrayList<>();
        try {
            if (!Files.exists(CASES_DIR) || !Files.isDirectory(CASES_DIR)) {
                return files;
            }
            Files.list(CASES_DIR)
                    .filter(path -> path.getFileName().toString().endsWith(".txt"))
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .forEach(files::add);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to list test cases: " + ex.getMessage(), ex);
        }
        return files;
    }

    private static TestResult runCase(Path file) {
        String fileName = file.getFileName().toString();
        String caseName = fileName.substring(0, fileName.length() - 4);
        boolean expectError = fileName.startsWith("invalid_");

        try {
            String source = Files.readString(file, StandardCharsets.UTF_8);

            Scanner scanner = new Scanner(source);
            List<Token> tokens = scanner.scanTokens();

            Parser parser = new Parser(tokens);
            Program program = parser.parseProgram();
            List<ParseException> parseErrors = parser.getErrors();

            if (expectError) {
                if (!parseErrors.isEmpty()) {
                    return new TestResult(caseName, true, " -> error: PARSER");
                }
                return new TestResult(caseName, false, " -> expected error, but parsed successfully");
            }

            if (!parseErrors.isEmpty()) {
                return new TestResult(caseName, false,
                        " -> unexpected parser error: " + parseErrors.get(0).getMessage());
            }

            if (program.getStatements().isEmpty()) {
                return new TestResult(caseName, false, " -> expected non-empty program");
            }
            return new TestResult(caseName, true, "");
        } catch (LexicalException ex) {
            if (expectError) {
                return new TestResult(caseName, true, " -> error: LEXER");
            }
            return new TestResult(caseName, false, " -> unexpected lexer error: " + ex.getMessage());
        } catch (Exception ex) {
            return new TestResult(caseName, false, " -> system failure: " + ex.getMessage());
        }
    }
}