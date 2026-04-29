package lexer;

import util.SourcePosition;

public class Token {
	private final TokenType type;
	private final String lexeme;
	private final Object literal;
	private final SourcePosition position;

	public Token(TokenType type, String lexeme, Object literal, SourcePosition position) {
		this.type = type;
		this.lexeme = lexeme;
		this.literal = literal;
		this.position = position;
	}

	public TokenType getType() {
		return type;
	}

	public String getLexeme() {
		return lexeme;
	}

	public Object getLiteral() {
		return literal;
	}

	public SourcePosition getPosition() {
		return position;
	}

	@Override
	public String toString() {
		String literalText = literal == null ? "null" : literal.toString();
		return type + " " + lexeme + " " + literalText + " @" + position;
	}
}
