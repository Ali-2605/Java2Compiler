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
2 * 3
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

## Token demo.java2 visualization 

--- TOKEN STREAM VISUALIZATION ---
+-----------------+--------------+------------+------------+
| TYPE            | LEXEME       | LITERAL    | POSITION   |
+-----------------+--------------+------------+------------+
| IDENTIFIER      | 'total'      |            | 1:1        |
| ASSIGN          | ':-'         |            | 1:7        |
| NUMBER          | '1'          | 1          | 1:11       |
| PLUS            | '+'          |            | 1:13       |
| NUMBER          | '23'         | 23         | 1:15       |
| PLUS            | '+'          |            | 1:18       |
| NUMBER          | '24'         | 24         | 1:20       |
| STAR            | '*'          |            | 1:24       |
| NUMBER          | '35'         | 35         | 1:26       |
| END             | '~'          |            | 1:29       |
| IDENTIFIER      | 'ok'         |            | 3:1        |
| ASSIGN          | ':-'         |            | 3:4        |
| TRUE            | ':)'         | true       | 3:7        |
| AND             | ':{'         |            | 3:10       |
| NOT             | ':!'         |            | 3:13       |
| FALSE           | ':('         | false      | 3:16       |
| END             | '~'          |            | 3:18       |
| PRINT           | 'leviosa'    |            | 4:1        |
| LPAREN          | '->'         |            | 4:9        |
| IDENTIFIER      | 'total'      |            | 4:12       |
| RPAREN          | '-<'         |            | 4:18       |
| END             | '~'          |            | 4:20       |
| PRINT           | 'leviosa'    |            | 5:1        |
| LPAREN          | '->'         |            | 5:9        |
| IDENTIFIER      | 'ok'         |            | 5:11       |
| RPAREN          | '-<'         |            | 5:13       |
| END             | '~'          |            | 5:15       |
| EOF             | ''           |            | 6:1        |
+-----------------+--------------+------------+------------+

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
## AST demo.java2 visualization

--- ABSTRACT SYNTAX TREE ---
Program
  Assignment: total
    Value: Binary(+)
             Left: Binary(+)
                     Left: Literal[1]
                     Right: Literal[23]
             Right: Binary(*)
                      Left: Literal[24]
                      Right: Literal[35]
  Assignment: ok
    Value: Binary(:{)
             Left: Literal[true]
             Right: Unary(:!)
                      Right: Literal[false]
  Print (leviosa)
    Expr: Grouping
            Inside: Id[total]
  Print (leviosa)
    Expr: Grouping
            Inside: Id[ok]
-----------------------------

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

```bash
java Main -t -a demo.java2
```
This demonstrates scanner output, parser integration, interpreter, and AST output in one flow. 

## Short Design Report

Design decisions:

1. Recursive descent parser was chosen for direct grammar-to-code mapping and control over precedence.
2. Lexer stores both lexeme and optional typed literal value for clearer downstream semantics.
3. AST separates syntax from runtime evaluation to keep parser clean and testable.
4. Position-aware exceptions provide actionable diagnostics for both lexical and parse phases.
5. Comparison parsing is intentionally non-chainable to enforce explicit boolean expressions.

## TA Checklist Answers

Use this section as the short defense for the rubric.

### Boolean Rule

- `not` has the highest precedence among boolean operators, then `and`, then `or`.
- Comparisons are parsed in a separate grammar layer from arithmetic, so `1 + 2 < 3 * 4` is handled as arithmetic on both sides of a comparison.
- Boolean and arithmetic structure are distinguishable in the AST: boolean operations use `BinaryExpr` or `UnaryExpr` with logical operator tokens, while arithmetic operations use the same node types with arithmetic operator tokens. The tree shape and operator token type make the meaning explicit.

### Language Scope

- The language is a small, well-defined rule language with assignments, printing, arithmetic, boolean logic, comparisons, literals, identifiers, and grouping.
- The scope is manageable because the project only targets a recursive-descent front end plus a simple interpreter.
- Required features are implemented, and the repository includes both valid and invalid sample inputs under `tests/cases/`.

### Scanner / Lexical Analysis

- Token categories are defined in `lexer/TokenType.java`.
- Tokens keep both the token type and the raw lexeme, and the token printer shows both fields separately.
- Keywords, identifiers, literals, operators, and delimiters are all handled explicitly by the scanner.
- Scanner output is visible independently with `java Main -t <file>`.
- Lexical errors are reported with a message and source position through `LexicalException`.

### Parser / Recursive Descent

- The grammar is recursive-descent friendly and is implemented directly in `parser/Parser.java`.
- The parser methods mirror grammar nonterminals such as `parseOr`, `parseAnd`, `parseComparison`, `parseNot`, `parseArithmetic`, `parseTerm`, and `parseFactor`.
- Lookahead is handled with `peek()`, `check()`, `match()`, and `consume()`.
- Valid inputs parse successfully, and invalid inputs are rejected with parser errors instead of silent acceptance.

### AST / Structural Output

- The project builds a real AST rooted at `Program`.
- The AST is different from token output because tokens are flat scanner results, while the AST is a hierarchical tree of statements and expressions.
- Node kinds are meaningful: `Program`, `AssignmentStmt`, `PrintStmt`, `BinaryExpr`, `UnaryExpr`, `LiteralExpr`, `IdentifierExpr`, and `GroupExpr`.
- The AST design can be explained as syntax-first structure that the interpreter later evaluates.

### Testing and Diagnostics

- Valid, invalid, and nested cases are present in `tests/cases/`.
- Syntax errors include a source position and a readable message.
- Malformed input fails gracefully instead of crashing the parser.

### Understanding and Explanation

- The grammar, scanner/parser interface, and parser flow are documented in this README and reflected in the code structure.
- The project is not just an evaluator demo; it performs real scanning, parsing, AST construction, and then interpretation.

### Evidence You Can Quote

- `tests.TestRunner` passes all current cases: 15/15.
- `java Main -t <file>` prints the token stream with both lexeme and token type.
- `tests/cases/` includes valid programs, nested grouping precedence, lexical failures, parser failures, and chained-comparison rejection.
