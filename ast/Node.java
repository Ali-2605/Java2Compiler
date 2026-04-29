package ast;

import util.SourcePosition;

public interface Node {
	SourcePosition getPosition();
	<R> R accept(Visitor<R> visitor);
}
