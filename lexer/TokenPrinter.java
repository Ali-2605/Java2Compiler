import lexer.LexicalException;
import lexer.Token;
import lexer.TokenType;

public class TokenPrinter {

    public static void printTokens(String source) {
        Scanner scanner = new Scanner(source);
        
        System.out.println("--- TOKEN STREAM VISUALIZATION ---");
        System.out.printf("%-15s | %-12s | %-10s | %s%n", "TYPE", "LEXEME", "LITERAL", "POSITION");
        System.out.println("------------------------------------------------------------");

        try {
            Token token;
            do {
                token = scanner.nextToken();
                System.out.println(token);
            } while (token.type != TokenType.EOF);
        } catch (LexicalException e) {
            System.err.println("\n[LEXICAL ERROR]: " + e.getMessage());
        }
        
        System.out.println("------------------------------------------------------------");
    }
}