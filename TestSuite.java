import lexer.LexicalException;
import lexer.Token;
import lexer.TokenPrinter;
public class TestSuite {
    public static void runTests() {
        String[] tests = {
            "x :- 42 ~",                   // Basic assignment
            "leviosa -> :) :{ :( -< ~",    // Logic and Print
            "counter :- counter + 1 ~",    // Arithmetic
            "a < b != c >= d",             // Comparisons
            "invalidChar @",               // Should throw LexicalException
            "unclosed :- :",               // Should throw LexicalException (Incomplete token)
        };

        for (int i = 0; i < tests.length; i++) {
            System.out.println("\n--- TEST CASE " + (i + 1) + " ---");
            System.out.println("Input: " + tests[i]);
            try {
                TokenPrinter.printTokens(tests[i]);
            } catch (LexicalException e) {
                System.out.println("Caught Expected Error: " + e.getMessage());
            }
        }
    }
}