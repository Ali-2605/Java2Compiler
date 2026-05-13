# Java2Compiler Codebase Overview

This document explains how the Java2Compiler codebase is organized, how data flows through the system, and where to make changes.

## Project summary

Java2Compiler is a hand-built compiler front end plus a small tree-walking interpreter for a custom boolean/arithmetic rule language. The pipeline is:

```
SOURCE -> LEXER -> TOKENS -> PARSER -> AST -> INTERPRETER -> OUTPUT
```

Key stages:
- Lexing: convert source text to a stream of tokens
- Parsing: validate grammar and build an AST
- Interpretation: evaluate the AST and print results

## Repository layout

- Main.java: CLI entry point, orchestrates lexing, parsing, printing, and execution
- ast/: AST node definitions and the visitor interface
- lexer/: scanner/tokenization logic and token types
- parser/: recursive descent parser and syntax errors
- runtime/: interpreter and runtime errors
- tests/: test runner and test input cases
- util/: shared utilities (source locations)

## Entry point and CLI

Main.java drives the full pipeline:
1. Read the input file
2. Run the scanner to get tokens
3. Optionally print tokens (-t)
4. Parse tokens into an AST
5. Optionally print AST (-a)
6. Interpret the program

Supported CLI flags:
- -t: print token stream using TokenPrinter
- -a: print AST using ASTPrinter

## Language overview

Statements are terminated with ~ and include assignment and print.

Examples:
```
name :- 123 ~
leviosa name ~
```

### Literal values
- Boolean true: :)
- Boolean false: :(
- Integer literals: digits only

### Operators and keywords

| Concept | Lexeme | Notes |
| --- | --- | --- |
| Assignment | :- | Used in assignment statements |
| Print keyword | leviosa | print statement |
| End statement | ~ | required terminator |
| True | :) | boolean literal |
| False | :( | boolean literal |
| AND | :{ | boolean and |
| OR | :[ | boolean or |
| NOT | :! | boolean not |
| Grouping | -> expr -< | parenthesized grouping |
| Comment | `...` | ignored by scanner |

### Grammar (simplified)

```
program        -> statement* EOF
statement      -> assignment | print
assignment     -> IDENTIFIER ASSIGN expr END
print          -> PRINT expr END
expr           -> or
or             -> and (OR and)*
and            -> comparison (AND comparison)*
comparison     -> not ((RELOP) not)?
not            -> NOT not | arithmetic
arithmetic     -> term ((+|-) term)*
term           -> factor ((*|/) factor)*
factor         -> NUMBER | TRUE | FALSE | IDENTIFIER | LPAREN expr RPAREN
```

Notes:
- Comparisons do not chain (a < b < c is rejected)
- Precedence (low to high): OR, AND, comparison, NOT, +/-, */

## Lexer (lexer/)

Key classes:
- TokenType: enum of all token kinds
- Token: immutable token representation with type, lexeme, literal, and source position
- Scanner: converts source text into tokens
- LexicalException: error for bad characters, malformed identifiers, overflow, etc.
- TokenPrinter: debug utility for printing tokens

Scanner highlights:
- Tracks line/column via SourcePosition
- Recognizes custom lexemes like :), :(, :{, :[, :!, :-, ~, and -> -<
- Validates identifiers (letters only) and integer overflow

## Parser (parser/)

Key classes:
- Parser: recursive descent parser that builds the AST
- ParseException: syntax error with position

Parser highlights:
- Builds a Program node containing a list of statements
- Enforces operator precedence via rule ordering
- Rejects chained comparisons
- Includes statement-level synchronization to continue after errors

## AST (ast/)

Core interfaces:
- Node: base interface with getPosition() and accept(Visitor)
- Expr: marker interface for expression nodes
- Statement: marker interface for statement nodes
- Visitor: visitor interface used by Interpreter and ASTPrinter

Main AST nodes:
- Program
- AssignmentStmt
- PrintStmt
- BinaryExpr
- UnaryExpr
- GroupExpr
- LiteralExpr
- IdentifierExpr

ASTPrinter:
- Visitor implementation that prints a tree view for debugging
- Activated with -a

## Interpreter (runtime/)

Key classes:
- Interpreter: evaluates the AST using the Visitor pattern
- EvalException: runtime errors (undefined variables, type mismatch, divide by zero)

Interpreter highlights:
- Environment stored in HashMap<String, Object>
- Integers support arithmetic and comparisons
- Booleans support logical operations
- Outputs booleans as :) or :(
- Uses type-checking helpers to validate operands

## Error handling

All errors report source positions:
- LexicalException: invalid characters, malformed tokens, overflow
- ParseException: unexpected tokens or grammar violations
- EvalException: runtime errors during evaluation

## Tests (tests/)

- TestRunner scans tests/cases for .txt files
- Files named invalid_*.txt should trigger lexer or parser errors
- Files named valid_*.txt should parse cleanly and contain at least one statement
- Test summary prints total/passed/failed and exits with non-zero on failure

## Data flow example

Source:
```
total :- 1 + 23 ~
```

Flow:
1. Scanner produces tokens: IDENTIFIER(total) ASSIGN NUMBER(1) PLUS NUMBER(23) END EOF
2. Parser builds AST: AssignmentStmt(name=total, value=BinaryExpr(+))
3. Interpreter evaluates BinaryExpr -> 24 and stores total in the environment

## Extension points

Common ways to extend the language:
- New operators: add TokenType, scanner recognition, parser rule updates, and Interpreter logic
- New statements: add a Statement node, parser rule, and Interpreter visit method
- New expressions: add an Expr node, parser rule, and Visitor methods
- Better diagnostics: extend error messages with context or snippets
