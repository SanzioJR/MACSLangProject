package com.macslang.parser.ast.statement;

import com.macslang.parser.ast.NodeVisitor;
import com.macslang.parser.ast.expression.Expression;

public class ExpressionStatement implements Statement {
private final Expression expression;

public ExpressionStatement(Expression expression) {
    this.expression = expression;
}

public Expression getExpression() {
    return expression;
}

@Override
public <T> T accept(NodeVisitor<T> visitor) {
    return visitor.visitExpressionStatement(this);
}
}