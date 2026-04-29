package lexer;

import java.util.ArrayList;
import java.util.List;
import util.SourcePosition;

public class Scanner {
	private final String source;
	private final List<Token> tokens = new ArrayList<>();
	private int start = 0;
	private int current = 0;
	private int line = 1;
	private int column = 1;
	private int tokenLine = 1;
	private int tokenColumn = 1;

	public Scanner(String source) {
		this.source = source == null ? "" : source;
	}

	public List<Token> scanTokens() {
		while (!isAtEnd()) {
			start = current;
			tokenLine = line;
			tokenColumn = column;
			scanToken();
		}

		tokens.add(new Token(TokenType.EOF, "", null, new SourcePosition(line, column)));
		return tokens;
	}

	private void scanToken() {
		char c = advance();
		switch (c) {
			case ' ':
			case '\r':
			case '\t':
				return;
			case '\n':
				return;
			case '~':
				addToken(TokenType.END);
				return;
			case '`':
				skipBacktickComment();
				return;
			case ':':
				scanColonToken();
				return;
			case '-':
				if (match('>')) {
					addToken(TokenType.LPAREN);
				} else if (match('<')) {
					addToken(TokenType.RPAREN);
				} else {
					addToken(TokenType.MINUS);
				}
				return;
			case '+':
				addToken(TokenType.PLUS);
				return;
			case '*':
				addToken(TokenType.STAR);
				return;
			case '/':
				addToken(TokenType.SLASH);
				return;
			case '<':
				addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
				return;
			case '>':
				addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
				return;
			case '=':
				addToken(TokenType.EQUAL);
				return;
			case '!':
				if (match('=')) {
					addToken(TokenType.NOT_EQUAL);
					return;
				}
				throw error("Unexpected character '!'");
			default:
				if (isDigit(c)) {
					scanNumber();
					return;
				}
				if (isAlpha(c)) {
					scanIdentifier();
					return;
				}
				throw error("Unexpected character '" + c + "'");
		}
	}

	private void scanColonToken() {
		if (match(')')) {
			addToken(TokenType.TRUE, Boolean.TRUE);
			return;
		}
		if (match('(')) {
			addToken(TokenType.FALSE, Boolean.FALSE);
			return;
		}
		if (match('{')) {
			addToken(TokenType.AND);
			return;
		}
		if (match('[')) {
			addToken(TokenType.OR);
			return;
		}
		if (match('!')) {
			addToken(TokenType.NOT);
			return;
		}
		if (match('-')) {
			addToken(TokenType.ASSIGN);
			return;
		}
		throw error("Unexpected ':' sequence");
	}

	private void scanNumber() {
		while (isDigit(peek())) {
			advance();
		}

		String text = source.substring(start, current);
		try {
			int value = Integer.parseInt(text);
			addToken(TokenType.NUMBER, value);
		} catch (NumberFormatException ex) {
			throw error("Number literal out of range");
		}
	}

	private void scanIdentifier() {
		while (isAlpha(peek())) {
			advance();
		}

		if (isDigit(peek())) {
			throw error("Identifiers cannot contain digits");
		}

		String text = source.substring(start, current);
		if (text.equals("leviosa")) {
			addToken(TokenType.PRINT);
		} else {
			addToken(TokenType.IDENTIFIER);
		}
	}

	private void skipBacktickComment() {
		while (!isAtEnd() && peek() != '`') {
			advance();
		}
		if (isAtEnd()) {
			throw error("Unterminated comment");
		}
		advance();
	}


	private boolean match(char expected) {
		if (isAtEnd()) {
			return false;
		}
		if (source.charAt(current) != expected) {
			return false;
		}
		advance();
		return true;
	}

	private char peek() {
		if (isAtEnd()) {
			return '\0';
		}
		return source.charAt(current);
	}

	private char advance() {
		char c = source.charAt(current++);
		if (c == '\n') {
			line++;
			column = 1;
		} else {
			column++;
		}
		return c;
	}

	private boolean isAtEnd() {
		return current >= source.length();
	}

	private boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}

	private boolean isAlpha(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
	}

	private void addToken(TokenType type) {
		addToken(type, null);
	}

	private void addToken(TokenType type, Object literal) {
		String text = source.substring(start, current);
		tokens.add(new Token(type, text, literal, new SourcePosition(tokenLine, tokenColumn)));
	}

	private LexicalException error(String message) {
		return new LexicalException(message, new SourcePosition(tokenLine, tokenColumn));
	}
}
