package com.macslang.parser.ast.statement;

import com.macslang.parser.ast.NodeVisitor;
import com.macslang.parser.ast.expression.Expression;

public class ForStatement implements Statement {
private final Statement initializer;
private final Expression condition;
private final Expression increment;
private final Statement body;

public ForStatement(Statement initializer, Expression condition, Expression increment, Statement body) {
    this.initializer = initializer;
    this.condition = condition;
    this.increment = increment;
    this.body = body;
}

public Statement getInitializer() {
    return initializer;
}

public Expression getCondition() {
    return condition;
}

public Expression getIncrement() {
    return increment;
}

public Statement getBody() {
    return body;
}

@Override
public <T> T accept(NodeVisitor<T> visitor) {
    return visitor.visitForStatement(this);
}
}