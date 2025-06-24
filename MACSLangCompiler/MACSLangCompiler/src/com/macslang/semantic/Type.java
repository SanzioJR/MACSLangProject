package com.macslang.semantic;

import com.macslang.lexer.TokenType;

public enum Type {
INT,
FLOAT,
CHAR,
BOOL,
STRING,
VOID;

public static Type fromTokenType(TokenType tokenType) {
    switch (tokenType) {
        case TYPE_INT: return INT;
        case TYPE_FLOAT: return FLOAT;
        case TYPE_CHAR: return CHAR;
        case TYPE_BOOL: return BOOL;
        case TYPE_STRING: return STRING;
        default: throw new IllegalArgumentException("Token não é um tipo: " + tokenType);
    }
}

public boolean isCompatibleWith(Type other) {
    // Regras de compatibilidade de tipos
    if (this == other) return true;
    
    // Conversões numéricas implícitas
    if (this == FLOAT && other == INT) return true;
    
    return false;
}
}