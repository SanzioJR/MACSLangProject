package com.macslang.parser.ast.statement;

import com.macslang.parser.ast.NodeVisitor;
import com.macslang.parser.ast.expression.Expression;

public class WhileStatement implements Statement {
private final Expression condition;
private final Statement body;

public WhileStatement(Expression condition, Statement body) {
    this.condition = condition;
    this.body = body;
}

public Expression getCondition() {
    return condition;
}

public Statement getBody() {
    return body;
}

@Override
public <T> T accept(NodeVisitor<T> visitor) {
    return visitor.visitWhileStatement(this);
}
}