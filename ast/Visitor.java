package ast;
public interface Visitor<R> {
    R visitProgram(Program program);
    R visitAssignmentStmt(AssignmentStmt stmt);
    R visitPrintStmt(PrintStmt stmt);
    R visitBinaryExpr(BinaryExpr expr);
    R visitUnaryExpr(UnaryExpr expr);
    R visitGroupExpr(GroupExpr expr);
    R visitLiteralExpr(LiteralExpr expr);
    R visitIdentifierExpr(IdentifierExpr expr);
}
