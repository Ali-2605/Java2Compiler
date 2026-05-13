package ast;
import ast.Visitor;
import util.SourcePosition;

public class IdentifierExpr implements Expr {
	private final String name;
	private final SourcePosition position;

	public IdentifierExpr(String name, SourcePosition position) {
		this.name = name;
		this.position = position;
	}

	public String getName() {
		return name;
	}

	@Override
	public SourcePosition getPosition() {
		return position;
	}
	@Override
	public <R> R accept(Visitor<R> visitor) {
    	return visitor.visitIdentifierExpr(this);
	}
}
