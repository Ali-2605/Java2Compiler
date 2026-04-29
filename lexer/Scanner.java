import lexer.LexicalException;
import lexer.Token;
import lexer.TokenType;

public class Scanner {
    private final String source;
    private int index = 0;
    private int line = 1;
    private int column = 1;

    public Scanner(String source) {
        this.source = source;
    }

    public Token nextToken() {
        skipWhitespace();

        if (isAtEnd()) {
            return new Token(TokenType.EOF, "", line, column);
        }

        int startColumn = column;
        int startIndex = index; // Capture start position for substrings
        char c = peek();

        // Multi-character custom symbols
        if (c == ':') return colonToken(startColumn, startIndex);
        if (c == '-') return dashToken(startColumn, startIndex);
        
        // Comparisons
        if (c == '<' || c == '>' || c == '=' || c == '!') return comparisonToken(startColumn, startIndex);

        // Single character symbols
        advance(); 
        return switch (c) {
            case '~' -> new Token(TokenType.END, "~", line, startColumn);
            case '+' -> new Token(TokenType.PLUS, "+", line, startColumn);
            case '*' -> new Token(TokenType.STAR, "*", line, startColumn);
            case '/' -> new Token(TokenType.SLASH, "/", line, startColumn);
            default -> {
                if (Character.isDigit(c)) yield number(startColumn, startIndex);
                if (Character.isLetter(c)) yield identifier(startColumn, startIndex);
                throw error("Unexpected character '" + c + "'", startColumn);
            }
        };
    }

    private Token colonToken(int startCol, int startIdx) {
        advance(); // consume ':'
        char next = peek();

        return switch (next) {
            case ')' -> createToken(TokenType.TRUE, startCol, startIdx, 2);
            case '(' -> createToken(TokenType.FALSE, startCol, startIdx, 2);
            case '{' -> createToken(TokenType.AND, startCol, startIdx, 2);
            case '[' -> createToken(TokenType.OR, startCol, startIdx, 2);
            case '!' -> createToken(TokenType.NOT, startCol, startIdx, 2);
            case '-' -> createToken(TokenType.ASSIGN, startCol, startIdx, 2); // Fixed: moved from dashToken
            default -> throw error("Invalid sequence after ':'", startCol);
        };
    }

    private Token dashToken(int startCol, int startIdx) {
        advance(); // consume '-'
        char next = peek();

        return switch (next) {
            case '>' -> createToken(TokenType.LPAREN, startCol, startIdx, 2);
            case '<' -> createToken(TokenType.RPAREN, startCol, startIdx, 2);
            default -> throw error("Invalid sequence after '-': expected > or <", startCol);
        };
    }

    private Token comparisonToken(int startCol, int startIdx) {
        char c = advance();
        
        if (c == '<' && match('=')) return new Token(TokenType.LESS_EQUAL, "<=", line, startCol);
        if (c == '<') return new Token(TokenType.LESS, "<", line, startCol);
        if (c == '>' && match('=')) return new Token(TokenType.GREATER_EQUAL, ">=", line, startCol);
        if (c == '>') return new Token(TokenType.GREATER, ">", line, startCol);
        if (c == '!' && match('=')) return new Token(TokenType.NOT_EQUAL, "!=", line, startCol);
        if (c == '=') return new Token(TokenType.EQUAL, "=", line, startCol);

        throw error("Invalid comparison operator", startCol);
    }

    private Token number(int startCol, int startIdx) {
        // First digit already consumed in nextToken's default switch
        while (Character.isDigit(peek())) advance();
        String text = source.substring(startIdx, index);
        return new Token(TokenType.NUMBER, text, line, startCol);
    }

    private Token identifier(int startCol, int startIdx) {
        // First letter already consumed in nextToken's default switch
        while (Character.isLetter(peek())) advance();
        String text = source.substring(startIdx, index);

        TokenType type = text.equals("leviosa") ? TokenType.PRINT : TokenType.IDENTIFIER;
        return new Token(type, text, line, startCol);
    }

    // Helper to reduce repetition for fixed-length tokens
    private Token createToken(TokenType type, int col, int startIdx, int length) {
        for (int i = 0; i < length - 1; i++) advance(); 
        String text = source.substring(startIdx, index);
        return new Token(type, text, line, col);
    }

    private void skipWhitespace() {
        while (!isAtEnd()) {
            char c = peek();
            switch (c) {
                case ' ', '\t', '\r' -> advance();
                case '\n' -> {
                    line++;
                    column = 1;
                    index++; // Manual increment to avoid column++ in advance()
                }
                default -> { return; }
            }
        }
    }

    private char advance() {
        char c = source.charAt(index++);
        column++;
        return c;
    }

    private boolean match(char expected) {
        if (peek() == expected) {
            advance();
            return true;
        }
        return false;
    }

    private char peek() {
        return isAtEnd() ? '\0' : source.charAt(index);
    }

    private boolean isAtEnd() {
        return index >= source.length();
    }

    private LexicalException error(String msg, int col) {
        return new LexicalException(msg + " at line " + line + ", column " + col);
    }
}