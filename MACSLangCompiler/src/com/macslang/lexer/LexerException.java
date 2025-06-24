package com.macslang.lexer;

public class LexerException extends RuntimeException {
private static final long serialVersionUID = 1L;

private final int line;
private final int column;

public LexerException(String message, int line, int column) {
    super(message);
    this.line = line;
    this.column = column;
}

public int getLine() {
    return line;
}

public int getColumn() {
    return column;
}

@Override
public String toString() {
    return "Erro léxico na linha " + line + ", coluna " + column + ": " + getMessage();
}
}