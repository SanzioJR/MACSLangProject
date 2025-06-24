package com.macslang.parser.ast.expression;

import com.macslang.lexer.Token;
import com.macslang.parser.ast.NodeVisitor;

public class AssignExpr implements Expression {
private final Token name;
private final Expression value;

public AssignExpr(Token name, Expression value) {
    this.name = name;
    this.value = value;
}

public Token getName() {
    return name;
}

public Expression getValue() {
    return value;
}

@Override
public <T> T accept(NodeVisitor<T> visitor) {
    return visitor.visitAssignExpr(this);
}
}