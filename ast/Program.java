package ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import util.SourcePosition;

public class Program implements Node {
	private final List<Statement> statements;
	private final SourcePosition position;

	public Program(List<Statement> statements, SourcePosition position) {
		if (statements == null) {
			this.statements = Collections.emptyList();
		} else {
			this.statements = Collections.unmodifiableList(new ArrayList<>(statements));
		}
		this.position = position;
	}

	public List<Statement> getStatements() {
		return statements;
	}

	@Override
	public SourcePosition getPosition() {
		return position;
	}
}
