package com.macslang.parser.ast.statement;

import com.macslang.lexer.Token;
import com.macslang.parser.ast.NodeVisitor;
import com.macslang.parser.ast.expression.Expression;

public class VarDecl implements Statement {
private final Token name;
private final Token type;
private final Expression initializer;

public VarDecl(Token name, Token type, Expression initializer) {
    this.name = name;
    this.type = type;
    this.initializer = initializer;
}

public Token getName() {
    return name;
}

public Token getType() {
    return type;
}

public Expression getInitializer() {
    return initializer;
}

@Override
public <T> T accept(NodeVisitor<T> visitor) {
    return visitor.visitVarDecl(this);
}
}