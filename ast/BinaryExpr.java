package ast;
import ast.Visitor;
import lexer.Token;
import util.SourcePosition;

public class BinaryExpr implements Expr {
	private final Expr left;
	private final Token operator;
	private final Expr right;
	private final SourcePosition position;

	public BinaryExpr(Expr left, Token operator, Expr right, SourcePosition position) {
		this.left = left;
		this.operator = operator;
		this.right = right;
		this.position = position;
	}

	public Expr getLeft() {
		return left;
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
    	return visitor.visitBinaryExpr(this);
	}
}
