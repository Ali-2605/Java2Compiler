package lexer;

public class TokenPrinter {

    public static void printTokens(String source) {
        Scanner scanner = new Scanner(source);
        
        System.out.println("--- TOKEN STREAM VISUALIZATION ---");
        System.out.printf("%-15s | %-12s | %-10s | %s%n", "TYPE", "LEXEME", "LITERAL", "POSITION");
        System.out.println("------------------------------------------------------------");

        try {
            for (Token token : scanner.scanTokens()) {
                System.out.println(token);
            }
        } catch (LexicalException e) {
            System.err.println("\n[LEXICAL ERROR]: " + e.getMessage());
        }
        
        System.out.println("------------------------------------------------------------");
    }
}