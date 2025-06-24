package com.macslang.parser.ast.statement;

import com.macslang.lexer.Token;
import com.macslang.parser.ast.NodeVisitor;

public class InputStatement implements Statement {
private final Token variable;

public InputStatement(Token variable) {
    this.variable = variable;
}

public Token getVariable() {
    return variable;
}

@Override
public <T> T accept(NodeVisitor<T> visitor) {
    return visitor.visitInputStatement(this);
}
}