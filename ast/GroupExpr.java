package ast;
import ast.Visitor;
import util.SourcePosition;

public class GroupExpr implements Expr {
	private final Expr expression;
	private final SourcePosition position;

	public GroupExpr(Expr expression, SourcePosition position) {
		this.expression = expression;
		this.position = position;
	}

	public Expr getExpression() {
		return expression;
	}

	@Override
	public SourcePosition getPosition() {
		return position;
	}
	@Override
	public <R> R accept(Visitor<R> visitor) {
    	return visitor.visitGroupExpr(this);
	}
}
