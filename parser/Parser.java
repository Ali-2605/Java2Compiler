package parser;

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
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import lexer.Token;
import lexer.TokenType;
import util.SourcePosition;

public class Parser {
	private static final Set<TokenType> COMPARISON_TYPES = EnumSet.of(
			TokenType.LESS,
			TokenType.LESS_EQUAL,
			TokenType.GREATER,
			TokenType.GREATER_EQUAL,
			TokenType.EQUAL,
			TokenType.NOT_EQUAL
	);

	private final List<Token> tokens;
	private int current = 0;
	private final List<ParseException> errors = new ArrayList<>();

	public Parser(List<Token> tokens) {
		this.tokens = tokens;
	}

	/*
	 * Grammar
	 * program        -> statement* EOF ;
	 * statement      -> assignment | print ;
	 * assignment     -> IDENTIFIER ASSIGN expr END ;
	 * print          -> PRINT expr END ;
	 * expr           -> or ;
	 * or             -> and (OR and)* ;
	 * and            -> comparison (AND comparison)* ;
	 * comparison     -> not ((< | <= | > | >= | = | !=) not)? ;
	 * not            -> NOT not | arithmetic ;
	 * arithmetic     -> term ((+ | -) term)* ;
	 * term           -> factor ((* | /) factor)* ;
	 * factor         -> NUMBER | TRUE | FALSE | IDENTIFIER | LPAREN expr RPAREN ;
	 */
	public Program parseProgram() {
		List<Statement> statements = new ArrayList<>();
		while (!isAtEnd()) {
			try {
				statements.add(parseStatement());
			} catch (ParseException ex) {
				errors.add(ex);
				synchronize();
			}
		}
		SourcePosition position = statements.isEmpty()
				? new SourcePosition(1, 1)
				: statements.get(0).getPosition();
		return new Program(statements, position);
	}

	public List<ParseException> getErrors() {
		return errors;
	}

	private Statement parseStatement() {
		if (match(TokenType.PRINT)) {
			Token keyword = previous();
			Expr value = parseExpr();
			consume(TokenType.END, "Expect '~' after print expression");
			return new PrintStmt(value, keyword.getPosition());
		}

		if (check(TokenType.IDENTIFIER)) {
			Token name = advance();
			consume(TokenType.ASSIGN, "Expect ':-' after identifier");
			Expr value = parseExpr();
			consume(TokenType.END, "Expect '~' after assignment");
			return new AssignmentStmt(name.getLexeme(), value, name.getPosition());
		}

		throw error(peek(), "Expect statement");
	}

	private Expr parseExpr() {
		return parseOr();
	}

	private Expr parseOr() {
		Expr expr = parseAnd();
		while (match(TokenType.OR)) {
			Token operator = previous();
			Expr right = parseAnd();
			expr = new BinaryExpr(expr, operator, right, operator.getPosition());
		}
		return expr;
	}

	private Expr parseAnd() {
		Expr expr = parseComparison();
		while (match(TokenType.AND)) {
			Token operator = previous();
			Expr right = parseComparison();
			expr = new BinaryExpr(expr, operator, right, operator.getPosition());
		}
		return expr;
	}

	private Expr parseComparison() {
		Expr expr = parseNot();
		if (matchAny(COMPARISON_TYPES)) {
			Token operator = previous();
			Expr right = parseNot();
			if (isComparison(peek().getType())) {
				throw error(peek(), "Chained comparisons are not allowed");
			}
			expr = new BinaryExpr(expr, operator, right, operator.getPosition());
		}
		return expr;
	}

	private Expr parseNot() {
		if (match(TokenType.NOT)) {
			Token operator = previous();
			Expr right = parseNot();
			return new UnaryExpr(operator, right, operator.getPosition());
		}
		return parseArithmetic();
	}

	private Expr parseArithmetic() {
		Expr expr = parseTerm();
		while (match(TokenType.PLUS, TokenType.MINUS)) {
			Token operator = previous();
			Expr right = parseTerm();
			expr = new BinaryExpr(expr, operator, right, operator.getPosition());
		}
		return expr;
	}

	private Expr parseTerm() {
		Expr expr = parseFactor();
		while (match(TokenType.STAR, TokenType.SLASH)) {
			Token operator = previous();
			Expr right = parseFactor();
			expr = new BinaryExpr(expr, operator, right, operator.getPosition());
		}
		return expr;
	}

	private Expr parseFactor() {
		if (match(TokenType.TRUE)) {
			return new LiteralExpr(Boolean.TRUE, previous().getPosition());
		}
		if (match(TokenType.FALSE)) {
			return new LiteralExpr(Boolean.FALSE, previous().getPosition());
		}
		if (match(TokenType.NUMBER)) {
			return new LiteralExpr(previous().getLiteral(), previous().getPosition());
		}
		if (match(TokenType.IDENTIFIER)) {
			return new IdentifierExpr(previous().getLexeme(), previous().getPosition());
		}
		if (match(TokenType.LPAREN)) {
			Token left = previous();
			Expr expr = parseExpr();
			consume(TokenType.RPAREN, "Expect '-<' after expression");
			return new GroupExpr(expr, left.getPosition());
		}

		throw error(peek(), "Expect expression");
	}

	private boolean match(TokenType... types) {
		for (TokenType type : types) {
			if (check(type)) {
				advance();
				return true;
			}
		}
		return false;
	}

	private boolean matchAny(Set<TokenType> types) {
		if (!isAtEnd() && types.contains(peek().getType())) {
			advance();
			return true;
		}
		return false;
	}

	private Token consume(TokenType type, String message) {
		if (check(type)) {
			return advance();
		}
		throw error(peek(), message);
	}

	private boolean check(TokenType type) {
		if (isAtEnd()) {
			return false;
		}
		return peek().getType() == type;
	}

	private Token advance() {
		if (!isAtEnd()) {
			current++;
		}
		return previous();
	}

	private boolean isAtEnd() {
		return peek().getType() == TokenType.EOF;
	}

	private Token peek() {
		return tokens.get(current);
	}

	private Token previous() {
		return tokens.get(current - 1);
	}

	private boolean isComparison(TokenType type) {
		return COMPARISON_TYPES.contains(type);
	}

	private ParseException error(Token token, String message) {
		return new ParseException(message, token.getPosition());
	}

	private void synchronize() {
		advance();
		while (!isAtEnd()) {
			if (previous().getType() == TokenType.END) {
				return;
			}
			TokenType next = peek().getType();
			if (next == TokenType.PRINT || next == TokenType.IDENTIFIER) {
				return;
			}
			advance();
		}
	}
}
