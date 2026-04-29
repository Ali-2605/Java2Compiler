package lexer;

import lexer.Token;
import lexer.TokenType;
import lexer.LexicalException;
import java.util.List;

public class TokenPrinter {

    public static void printTokens(String source) {
        Scanner scanner = new Scanner(source);

        System.out.println("\n--- TOKEN STREAM VISUALIZATION ---");
        // Defining a consistent format string for the table
        // TYPE (15) | LEXEME (12) | LITERAL (10) | POSITION (10)
        String rowFormat = "| %-15s | %-12s | %-10s | %-10s |%n";
        String separator = "+-----------------+--------------+------------+------------+";

        System.out.println(separator);
        System.out.printf(rowFormat, "TYPE", "LEXEME", "LITERAL", "POSITION");
        System.out.println(separator);

        try {
            List<Token> tokens = scanner.scanTokens();
            for (Token token : tokens) {
                // Formatting the literal column: show "null" as empty or "N/A"
                String literalStr = (token.literal != null) ? token.literal.toString() : "";

                // Formatting the position: "Line:Col"
                String posStr = token.position.line() + ":" + token.position.column();

                System.out.printf(rowFormat, 
                    token.type, 
                    "'" + token.lexeme + "'", 
                    literalStr, 
                    posStr
                );
            }
        } catch (LexicalException e) {
            System.out.println(separator);
            System.err.println("\n[LEXICAL ERROR]: " + e.getMessage());
            return; // Exit early on error to avoid printing a broken table
        }

        System.out.println(separator);
        System.out.println("Total tokens processed: " + (source == null ? 0 : "Done"));
    }
}
