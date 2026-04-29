package runtime;

import ast.AssignmentStmt;
import ast.BinaryExpr;
import ast.Expr;
import ast.GroupExpr;
import ast.IdentifierExpr;
import ast.LiteralExpr;
import ast.PrintStmt;
import ast.Program;
import ast.Statement;
import ast.UnaryExpr;
import java.util.HashMap;
import java.util.Map;
import lexer.Token;
import lexer.TokenType;
import util.SourcePosition;

public class Interpreter {
    private final Map<String, Object> environment = new HashMap<>();

    public void execute(Program program) {
        for (Statement statement : program.getStatements()) {
            executeStatement(statement);
        }
    }

    private void executeStatement(Statement statement) {
        if (statement instanceof AssignmentStmt) {
            AssignmentStmt assign = (AssignmentStmt) statement;
            Object value = evaluate(assign.getValue());
            environment.put(assign.getName(), value);
            return;
        }

        if (statement instanceof PrintStmt) {
            PrintStmt printStmt = (PrintStmt) statement;
            Object value = evaluate(printStmt.getValue());
            System.out.println(formatValue(value));
            return;
        }

        throw new EvalException("Unknown statement", statement.getPosition());
    }

    private Object evaluate(Expr expr) {
        if (expr instanceof LiteralExpr) {
            return ((LiteralExpr) expr).getValue();
        }
        if (expr instanceof IdentifierExpr) {
            IdentifierExpr identifierExpr = (IdentifierExpr) expr;
            if (!environment.containsKey(identifierExpr.getName())) {
                throw new EvalException("Undefined variable '" + identifierExpr.getName() + "'",
                        identifierExpr.getPosition());
            }
            return environment.get(identifierExpr.getName());
        }
        if (expr instanceof GroupExpr) {
            return evaluate(((GroupExpr) expr).getExpression());
        }
        if (expr instanceof UnaryExpr) {
            return evaluateUnary((UnaryExpr) expr);
        }
        if (expr instanceof BinaryExpr) {
            return evaluateBinary((BinaryExpr) expr);
        }

        throw new EvalException("Unknown expression", expr.getPosition());
    }

    private Object evaluateUnary(UnaryExpr expr) {
        Token operator = expr.getOperator();
        Object right = evaluate(expr.getRight());
        if (operator.getType() == TokenType.NOT) {
            return toBoolean(right, operator.getPosition());
        }
        if (operator.getType() == TokenType.MINUS) {
            int value = toInteger(right, operator.getPosition());
            return -value;
        }
        throw new EvalException("Unsupported unary operator", operator.getPosition());
    }

    private Object evaluateBinary(BinaryExpr expr) {
        Token operator = expr.getOperator();
        TokenType type = operator.getType();

        if (type == TokenType.AND) {
            boolean left = toBoolean(evaluate(expr.getLeft()), operator.getPosition());
            if (!left) {
                return false;
            }
            boolean right = toBoolean(evaluate(expr.getRight()), operator.getPosition());
            return right;
        }

        if (type == TokenType.OR) {
            boolean left = toBoolean(evaluate(expr.getLeft()), operator.getPosition());
            if (left) {
                return true;
            }
            boolean right = toBoolean(evaluate(expr.getRight()), operator.getPosition());
            return right;
        }

        Object leftValue = evaluate(expr.getLeft());
        Object rightValue = evaluate(expr.getRight());

        switch (type) {
            case PLUS:
                return toInteger(leftValue, operator.getPosition())
                        + toInteger(rightValue, operator.getPosition());
            case MINUS:
                return toInteger(leftValue, operator.getPosition())
                        - toInteger(rightValue, operator.getPosition());
            case STAR:
                return toInteger(leftValue, operator.getPosition())
                        * toInteger(rightValue, operator.getPosition());
            case SLASH:
                int divisor = toInteger(rightValue, operator.getPosition());
                if (divisor == 0) {
                    throw new EvalException("Division by zero", operator.getPosition());
                }
                return toInteger(leftValue, operator.getPosition()) / divisor;
            case LESS:
                return toInteger(leftValue, operator.getPosition())
                        < toInteger(rightValue, operator.getPosition());
            case LESS_EQUAL:
                return toInteger(leftValue, operator.getPosition())
                        <= toInteger(rightValue, operator.getPosition());
            case GREATER:
                return toInteger(leftValue, operator.getPosition())
                        > toInteger(rightValue, operator.getPosition());
            case GREATER_EQUAL:
                return toInteger(leftValue, operator.getPosition())
                        >= toInteger(rightValue, operator.getPosition());
            case EQUAL:
                return equalsValue(leftValue, rightValue, operator.getPosition());
            case NOT_EQUAL:
                return !equalsValue(leftValue, rightValue, operator.getPosition());
            default:
                throw new EvalException("Unsupported binary operator", operator.getPosition());
        }
    }

    private boolean equalsValue(Object left, Object right, SourcePosition position) {
        if (left == null || right == null) {
            return left == right;
        }
        if (left.getClass() != right.getClass()) {
            throw new EvalException("Type mismatch in comparison", position);
        }
        return left.equals(right);
    }

    private int toInteger(Object value, SourcePosition position) {
        if (value instanceof Integer) {
            return (Integer) value;
        }
        throw new EvalException("Expected integer", position);
    }

    private boolean toBoolean(Object value, SourcePosition position) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        throw new EvalException("Expected boolean", position);
    }

    private String formatValue(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value ? ":)" : ":(";
        }
        return value == null ? "null" : value.toString();
    }
}
