# Handmade Compiler (Java)

A starter skeleton for building a handmade compiler in Java.

## Project Structure

```text
.
├── Main.java
├── lexer/
├── parser/
├── ast/
└── util/
```

## Getting Started

1. Make sure Java is installed:
   - `java -version`
   - `javac -version`
2. Compile:
   - `javac Main.java lexer/*.java parser/*.java ast/*.java util/*.java`
3. Run:
   - `java Main`

## Next Steps

- Implement token types and scanner logic in `lexer/`
- Build recursive-descent parser in `parser/`
- Define AST behavior in `ast/`
- Add AST pretty-printing in `util/AstPrinter.java`
