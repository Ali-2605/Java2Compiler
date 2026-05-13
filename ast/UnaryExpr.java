package ast;
import ast.Visitor;
import lexer.Token;
import util.SourcePosition;

public class UnaryExpr implements Expr {
	private final Token operator;
	private final Expr right;
	private final SourcePosition position;

	public UnaryExpr(Token operator, Expr right, SourcePosition position) {
		this.operator = operator;
		this.right = right;
		this.position = position;
	}

	public Token getOperator() {
		return operator;
	}

	public Expr getRight() {
		return right;
	}

	@Override
	public SourcePosition getPosition() {
		return position;
	}
	@Override
	public <R> R accept(Visitor<R> visitor) {
    	return visitor.visitUnaryExpr(this);
	}
}
