package ast;

public class ASTPrinter implements Visitor<String> {

    public void print(Program program) {
        System.out.println("\n--- ABSTRACT SYNTAX TREE ---");
        System.out.print(program.accept(this));
        System.out.println("-----------------------------\n");
    }

    // Helper to create indentation
    private String indent(int level) {
        return "  ".repeat(level);
    }

    @Override
    public String visitProgram(Program program) {
        StringBuilder sb = new StringBuilder("Program\n");
        for (Statement stmt : program.getStatements()) {
            // Start statements at level 1
            sb.append(indent(1)).append(stmt.accept(this));
        }
        return sb.toString();
    }

    @Override
    public String visitAssignmentStmt(AssignmentStmt stmt) {
        return "Assignment: " + stmt.getName() + "\n" +
               formatChild("Value", stmt.getValue(), 2);
    }

    @Override
    public String visitPrintStmt(PrintStmt stmt) {
        return "Print (leviosa)\n" + 
               formatChild("Expr", stmt.getValue(), 2);
    }

    @Override
    public String visitBinaryExpr(BinaryExpr expr) {
        return "Binary(" + expr.getOperator().getLexeme() + ")\n" +
               formatChild("Left", expr.getLeft(), 1) +
               formatChild("Right", expr.getRight(), 1);
    }

    @Override
    public String visitUnaryExpr(UnaryExpr expr) {
        return "Unary(" + expr.getOperator().getLexeme() + ")\n" +
               formatChild("Right", expr.getRight(), 1);
    }

    @Override
    public String visitGroupExpr(GroupExpr expr) {
        return "Grouping\n" + 
               formatChild("Inside", expr.getExpression(), 1);
    }

    @Override
    public String visitLiteralExpr(LiteralExpr expr) {
        return "Literal[" + expr.getValue() + "]\n";
    }

    @Override
    public String visitIdentifierExpr(IdentifierExpr expr) {
        return "Id[" + expr.getName() + "]\n";
    }

    /**
     * This is the secret sauce. It takes the result of a child's accept() 
     * and shifts the entire block to the right.
     */
    private String formatChild(String label, Expr expr, int level) {
        String result = expr.accept(this);
        String[] lines = result.split("\n");
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < lines.length; i++) {
            sb.append(indent(level));
            if (i == 0) sb.append(label).append(": ");
            else sb.append(" ".repeat(label.length() + 2)); // Align lines after the label
            
            sb.append(lines[i]).append("\n");
        }
        return sb.toString();
    }
}