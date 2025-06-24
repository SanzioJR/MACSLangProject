package com.macslang.parser.ast.expression;

import com.macslang.lexer.Token;
import com.macslang.parser.ast.NodeVisitor;

public class UnaryExpr implements Expression {
private final Token operator;
private final Expression right;

public UnaryExpr(Token operator, Expression right) {
    this.operator = operator;
    this.right = right;
}

public Token getOperator() {
    return operator;
}

public Expression getRight() {
    return right;
}

@Override
public <T> T accept(NodeVisitor<T> visitor) {
    return visitor.visitUnaryExpr(this);
}
}