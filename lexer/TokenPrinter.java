package lexer;

import java.util.List;

public class TokenPrinter {

    public static void printTokens(String source) {
        Scanner scanner = new Scanner(source);
        printTokens(scanner.scanTokens());
    }

    public static void printTokens(List<Token> tokens) {
        System.out.println("\n--- TOKEN STREAM VISUALIZATION ---");
        String rowFormat = "| %-15s | %-12s | %-10s | %-10s |%n";
        String separator = "+-----------------+--------------+------------+------------+";

        System.out.println(separator);
        System.out.printf(rowFormat, "TYPE", "LEXEME", "LITERAL", "POSITION");
        System.out.println(separator);

        for (Token token : tokens) {
            String literalStr = token.getLiteral() == null ? "" : token.getLiteral().toString();
            String posStr = token.getPosition() == null ? "" : token.getPosition().toString();
            System.out.printf(
                    rowFormat,
                    token.getType(),
                    "'" + token.getLexeme() + "'",
                    literalStr,
                    posStr
            );
        }

        System.out.println(separator);
    }
}
