package com.macslang.parser.ast.expression;

import com.macslang.lexer.Token;
import com.macslang.parser.ast.NodeVisitor;

public class VarExpr implements Expression {
private final Token name;

public VarExpr(Token name) {
    this.name = name;
}

public Token getName() {
    return name;
}

@Override
public <T> T accept(NodeVisitor<T> visitor) {
    return visitor.visitVarExpr(this);
}
}