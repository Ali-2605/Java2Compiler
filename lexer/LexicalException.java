package lexer;

import util.SourcePosition;

public class LexicalException extends RuntimeException {
	private final SourcePosition position;

	public LexicalException(String message, SourcePosition position) {
		super(message + " at " + position);
		this.position = position;
	}

	public SourcePosition getPosition() {
		return position;
	}
}
