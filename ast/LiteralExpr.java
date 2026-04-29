package ast;

import util.SourcePosition;

public class LiteralExpr implements Expr {
	private final Object value;
	private final SourcePosition position;

	public LiteralExpr(Object value, SourcePosition position) {
		this.value = value;
		this.position = position;
	}

	public Object getValue() {
		return value;
	}

	@Override
	public SourcePosition getPosition() {
		return position;
	}
}
