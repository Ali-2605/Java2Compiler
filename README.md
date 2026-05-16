# Java2Compiler

A hand-built compiler front-end and tree-walking interpreter for a custom boolean/arithmetic rule language with emoji-inspired syntax.

## Project Overview

Java2Compiler implements a complete compiler pipeline from source text to execution:

```
SOURCE → LEXER → TOKENS → PARSER → AST → INTERPRETER → OUTPUT
```

The project includes:
- **Lexer**: Tokenizes source code
- **Parser**: Validates grammar and builds an Abstract Syntax Tree (AST)
- **Interpreter**: Evaluates the AST and produces output

## Language Features

The language supports assignments, print statements, and boolean/arithmetic expressions with custom emoji-like operators:

- **Assignment**: `name :- 123 ~`
- **Print**: `leviosa value ~`
- **Operators**: `:)` (true), `:(` (false), `:{` (AND), `:[` (OR), `:!` (NOT), `->` and `-<` (grouping)

## Building the Project

Compile all Java files:

```bash
javac $(find . -name "*.java")
```

Or use your IDE to build the project directly.

## Running the App

Run a source file:

```bash
java Main <file.txt>
```

### CLI Flags

- `-t`: Print the token stream
- `-a`: Print the Abstract Syntax Tree

Example with flags:

```bash
java Main -t -a program.txt
```

## Running the Tests

Run the test suite:

```bash
java tests.TestRunner
```

The test runner automatically discovers and runs all test cases in `tests/cases/`, validating both valid programs and error handling for invalid syntax.

## Project Structure

- `Main.java` - CLI entry point
- `lexer/` - Tokenization and lexical analysis
- `parser/` - Syntax analysis and AST construction
- `ast/` - AST node definitions
- `runtime/` - Interpretation and execution
- `tests/` - Test suite and test cases
- `util/` - Shared utilities
