package parser;

import util.SourcePosition;

public class ParseException extends RuntimeException {
	private final SourcePosition position;

	public ParseException(String message, SourcePosition position) {
		super(message + " at " + position);
		this.position = position;
	}

	public SourcePosition getPosition() {
		return position;
	}
}
