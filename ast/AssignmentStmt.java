package ast;
import ast.Visitor;
import util.SourcePosition;

public class AssignmentStmt implements Statement {
	private final String name;
	private final Expr value;
	private final SourcePosition position;

	public AssignmentStmt(String name, Expr value, SourcePosition position) {
		this.name = name;
		this.value = value;
		this.position = position;
	}

	public String getName() {
		return name;
	}

	public Expr getValue() {
		return value;
	}

	@Override
	public SourcePosition getPosition() {
		return position;
	}
	@Override
	public <R> R accept(Visitor<R> visitor) {
    	return visitor.visitAssignmentStmt(this);
	}
}
