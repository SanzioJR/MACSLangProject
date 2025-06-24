package com.macslang.parser.ast.statement;

import com.macslang.parser.ast.NodeVisitor;
import java.util.List;

public class BlockStatement implements Statement {
private final List<Statement> statements;

public BlockStatement(List<Statement> statements) {
    this.statements = statements;
}

public List<Statement> getStatements() {
    return statements;
}

@Override
public <T> T accept(NodeVisitor<T> visitor) {
    return visitor.visitBlockStatement(this);
}
}