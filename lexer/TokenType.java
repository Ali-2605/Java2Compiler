package lexer;

public enum TokenType {
    // Keywords / boolean
    TRUE,//:)
    FALSE,//:(
    AND, OR, NOT,
    PRINT,//leviosa ->-<

    // Identifiers & literals
    IDENTIFIER, NUMBER,

    // Operators
    ASSIGN,        // :-
    EQUAL,         // =
    NOT_EQUAL,     // !=
    LESS,          // <
    LESS_EQUAL,    // <=
    GREATER,       // >
    GREATER_EQUAL, // >=

    // Arithmetic
    PLUS, MINUS, STAR, SLASH,

    // Delimiters
    LPAREN,        // ->
    RPAREN,        // -<
    END,     // ~

    EOF
}