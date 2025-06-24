package com.macslang.semantic;

import com.macslang.lexer.Token;

public class Symbol {
private final String name;
private final Type type;
private final SymbolKind kind;
private final Token declaration;

public enum SymbolKind {
    VARIABLE,
    FUNCTION,
    PARAMETER
}

public Symbol(String name, Type type, SymbolKind kind, Token declaration) {
    this.name = name;
    this.type = type;
    this.kind = kind;
    this.declaration = declaration;
}

public String getName() {
    return name;
}

public Type getType() {
    return type;
}

public SymbolKind getKind() {
    return kind;
}

public Token getDeclaration() {
    return declaration;
}

@Override
public String toString() {
    return name + " (" + kind + "): " + type;
}
}