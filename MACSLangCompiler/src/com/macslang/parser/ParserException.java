package com.macslang.parser;

import com.macslang.lexer.Token;

public class ParserException extends RuntimeException {
private static final long serialVersionUID = 1L;

private final Token token;

public ParserException(String message, Token token) {
    super(message);
    this.token = token;
}

public Token getToken() {
    return token;
}

@Override
public String toString() {
    if (token == null) {
        return "Erro sintático: " + getMessage();
    }
    
    return "Erro sintático na linha " + token.getLine() + ", coluna " + token.getColumn() + 
           " em '" + token.getLexeme() + "': " + getMessage();
}
}