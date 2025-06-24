package com.macslang.parser.ast;

import com.macslang.parser.ast.statement.Statement;
import java.util.List;

public class Program implements Node {
private final List<Statement> statements;

public Program(List<Statement> statements) {
    this.statements = statements;
}

public List<Statement> getStatements() {
    return statements;
}

@Override
public <T> T accept(NodeVisitor<T> visitor) {
    return visitor.visitProgram(this);
}
}