package com.macslang.lexer;

public class Token {
private final TokenType type;
private final String lexeme;
private final Object literal;
private final int line;
private final int column;

public Token(TokenType type, String lexeme, Object literal, int line, int column) {
    this.type = type;
    this.lexeme = lexeme;
    this.literal = literal;
    this.line = line;
    this.column = column;
}

public TokenType getType() {
    return type;
}

public String getLexeme() {
    return lexeme;
}

public Object getLiteral() {
    return literal;
}

public int getLine() {
    return line;
}

public int getColumn() {
    return column;
}

@Override
public String toString() {
    return type + " " + lexeme + " " + (literal != null ? literal : "");
}
}