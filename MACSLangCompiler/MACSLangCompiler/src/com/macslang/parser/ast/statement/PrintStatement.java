package com.macslang.parser.ast.statement;

import com.macslang.parser.ast.NodeVisitor;
import com.macslang.parser.ast.expression.Expression;

public class PrintStatement implements Statement {
private final Expression expression;

public PrintStatement(Expression expression) {
    this.expression = expression;
}

public Expression getExpression() {
    return expression;
}

@Override
public <T> T accept(NodeVisitor<T> visitor) {
    return visitor.visitPrintStatement(this);
}
}