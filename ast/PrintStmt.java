package ast;

import util.SourcePosition;

public class PrintStmt implements Statement {
	private final Expr value;
	private final SourcePosition position;

	public PrintStmt(Expr value, SourcePosition position) {
		this.value = value;
		this.position = position;
	}

	public Expr getValue() {
		return value;
	}

	@Override
	public SourcePosition getPosition() {
		return position;
	}
}
