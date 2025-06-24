package com.macslang.semantic;

import com.macslang.lexer.Token;

public class SemanticException extends RuntimeException {
private static final long serialVersionUID = 1L;

private final Token token;

public SemanticException(String message, Token token) {
    super(message);
    this.token = token;
}

public Token getToken() {
    return token;
}

@Override
public String toString() {
    if (token == null) {
        return "Erro semântico: " + getMessage();
    }
    
    return "Erro semântico na linha " + token.getLine() + ", coluna " + token.getColumn() + 
           " em '" + token.getLexeme() + "': " + getMessage();
}
}