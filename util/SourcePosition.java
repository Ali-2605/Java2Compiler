package util;

public class SourcePosition {
	private final int line;
	private final int column;

	public SourcePosition(int line, int column) {
		this.line = line;
		this.column = column;
	}

	public int getLine() {
		return line;
	}

	public int getColumn() {
		return column;
	}

	@Override
	public String toString() {
		return line + ":" + column;
	}
}
