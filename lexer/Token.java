package lexer;

public class Token {
    public final TokenType type;
    public final String lexeme;
    public final Object literal; // Added: Stores the actual value (e.g., Integer 5)
    public final int line;
    public final int column;

    public Token(TokenType type, String lexeme, Object literal, int line, int column) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
        this.column = column;
    }

    // Overload for tokens without literals (like operators)
    public Token(TokenType type, String lexeme, int line, int column) {
        this(type, lexeme, null, line, column);
    }

    @Override
    public String toString() {
        return String.format("%-15s | %-12s | %s at %d:%d", 
            type, lexeme, (literal != null ? literal : ""), line, column);
    }
}