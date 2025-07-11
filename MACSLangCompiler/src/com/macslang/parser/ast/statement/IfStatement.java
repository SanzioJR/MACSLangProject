package com.macslang.parser.ast.statement;

import com.macslang.parser.ast.NodeVisitor;
import com.macslang.parser.ast.expression.Expression;

public class IfStatement implements Statement {
private final Expression condition;
private final Statement thenBranch;
private final Statement elseBranch;

public IfStatement(Expression condition, Statement thenBranch, Statement elseBranch) {
    this.condition = condition;
    this.thenBranch = thenBranch;
    this.elseBranch = elseBranch;
}

public Expression getCondition() {
    return condition;
}

public Statement getThenBranch() {
    return thenBranch;
}

public Statement getElseBranch() {
    return elseBranch;
}

@Override
public <T> T accept(NodeVisitor<T> visitor) {
    return visitor.visitIfStatement(this);
}
}