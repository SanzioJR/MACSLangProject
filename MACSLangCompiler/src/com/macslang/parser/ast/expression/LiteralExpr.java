package com.macslang.parser.ast.expression;

import com.macslang.parser.ast.NodeVisitor;

public class LiteralExpr implements Expression {
private final Object value;

public LiteralExpr(Object value) {
    this.value = value;
}

public Object getValue() {
    return value;
}

@Override
public <T> T accept(NodeVisitor<T> visitor) {
    return visitor.visitLiteralExpr(this);
}
}