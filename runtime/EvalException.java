package runtime;

import util.SourcePosition;

public class EvalException extends RuntimeException {
    private final SourcePosition position;

    public EvalException(String message, SourcePosition position) {
        super(message + " at " + position);
        this.position = position;
    }

    public SourcePosition getPosition() {
        return position;
    }
}
