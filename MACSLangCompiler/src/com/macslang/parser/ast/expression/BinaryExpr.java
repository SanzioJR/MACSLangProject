package com.macslang.parser.ast.expression;

import com.macslang.lexer.Token;
import com.macslang.parser.ast.NodeVisitor;

public class BinaryExpr implements Expression {
private final Expression left;
private final Token operator;
private final Expression right;

public BinaryExpr(Expression left, Token operator, Expression right) {
    this.left = left;
    this.operator = operator;
    this.right = right;
}

public Expression getLeft() {
    return left;
}

public Token getOperator() {
    return operator;
}

public Expression getRight() {
    return right;
}

@Override
public <T> T accept(NodeVisitor<T> visitor) {
    return visitor.visitBinaryExpr(this);
}
}