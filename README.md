# Boolean Rule Language Compiler (Java)

This project is a hand-built compiler front end and small interpreter for a custom
boolean rule language.

The language uses a custom surface syntax (emoji-inspired operators), while still
implementing the required boolean/comparison/arithmetic parsing behavior.

## Language Scope

Implemented required features:

- Boolean literals
- Identifiers
- Logical operators (`and`, `or`, `not`) via custom tokens
- Comparison operators (`<`, `>`, `=`, `!=`, `<=`, `>=`)
- Arithmetic subexpressions inside comparisons
- Assignment statements
- Output statements
- AST construction for arithmetic and boolean expressions
- Syntax error reporting with line/column positions
- Comment support
- Simplified interpreter behavior

## Custom Syntax Mapping

The grammar behavior matches the boolean rule variant, with this custom token mapping:

- `:-` -> assignment (`:=` equivalent)
- `~` -> statement terminator (`;` equivalent)
- `leviosa` -> print keyword (`print` equivalent)
- `:)` -> `true`
- `:(` -> `false`
- `:{` -> logical `and`
- `:[` -> logical `or`
- `:!` -> logical `not`
- `-> expr -<` -> grouped expression (`(expr)` equivalent)
- `` `comment` `` -> comment block

## Token Specification

Token categories from `lexer/TokenType.java`:

- Literals and names: `IDENTIFIER`, `NUMBER`, `TRUE`, `FALSE`
- Statements: `PRINT`, `ASSIGN`, `END`
- Arithmetic: `PLUS`, `MINUS`, `STAR`, `SLASH`
- Boolean/logical: `AND`, `OR`, `NOT`
- Comparison: `EQUAL`, `NOT_EQUAL`, `LESS`, `LESS_EQUAL`, `GREATER`, `GREATER_EQUAL`
- Grouping: `LPAREN`, `RPAREN`
- Stream end: `EOF`

Each token stores:

- token type
- lexeme (raw source slice)
- optional literal value (`Integer` or `Boolean`)
- source position (line and column)

## Grammar Description

Grammar used by recursive descent parser (`parser/Parser.java`):

```text
program        -> statement* EOF ;
statement      -> assignment | print ;
assignment     -> IDENTIFIER ASSIGN expr END ;
print          -> PRINT expr END ;
expr           -> or ;
or             -> and (OR and)* ;
and            -> comparison (AND comparison)* ;
comparison     -> not ((< | <= | > | >= | = | !=) not)? ;
not            -> NOT not | arithmetic ;
arithmetic     -> term ((+ | -) term)* ;
term           -> factor ((* | /) factor)* ;
factor         -> NUMBER | TRUE | FALSE | IDENTIFIER | LPAREN expr RPAREN ;
```

Notes:

- Precedence is encoded by parser function layering.
- Chained comparisons are rejected intentionally.
- Parser uses recovery (`synchronize`) after statement-level failures.

## Scanner and Parser Diagnostics

- Lexical errors: thrown as `LexicalException` with source position.
- Syntax errors: collected as `ParseException` with source position.
- Runtime type/evaluation errors: thrown as `EvalException`.

Scanner-only visibility:

- `java Main -t <file>` prints token stream without changing parse/eval logic.

## AST Design 

Root and statement nodes:

- `Program`
- `AssignmentStmt`
- `PrintStmt`

Expression nodes:

- `BinaryExpr`
- `UnaryExpr`
- `LiteralExpr`
- `IdentifierExpr`
- `GroupExpr`

All nodes carry source position information.

### AST Output Examples (Structural)

Input:

```text
adult :- age >= 18 ~
leviosa adult ~
```

Example structure:

```text
Program
   AssignmentStmt(name=adult)
      BinaryExpr(op=>=)
         IdentifierExpr(age)
         LiteralExpr(18)
   PrintStmt
      IdentifierExpr(adult)
```

Input:

```text
approved :- income > 5000 :{ :! blocked ~
```

Example structure:

```text
Program
   AssignmentStmt(name=approved)
      BinaryExpr(op=AND)
         BinaryExpr(op=>)
            IdentifierExpr(income)
            LiteralExpr(5000)
         UnaryExpr(op=NOT)
            IdentifierExpr(blocked)
```

## Test Cases

Located under `tests/cases/`.

- Valid: nontrivial arithmetic/boolean/grouping combinations
- Invalid: lexer failures, parser failures, chained comparison rejection

Run tests:

```bash
javac Main.java lexer/*.java parser/*.java ast/*.java util/*.java runtime/*.java tests/TestRunner.java
java tests.TestRunner
```

## Demo

Run program:

```bash
java Main demo.java2
```

Run with token stream output:

```bash
java Main -t demo.java2
```

This demonstrates scanner output, parser integration, and interpreter output in one flow.

## Short Design Report

Design decisions:

1. Recursive descent parser was chosen for direct grammar-to-code mapping and control over precedence.
2. Lexer stores both lexeme and optional typed literal value for clearer downstream semantics.
3. AST separates syntax from runtime evaluation to keep parser clean and testable.
4. Position-aware exceptions provide actionable diagnostics for both lexical and parse phases.
5. Comparison parsing is intentionally non-chainable to enforce explicit boolean expressions.