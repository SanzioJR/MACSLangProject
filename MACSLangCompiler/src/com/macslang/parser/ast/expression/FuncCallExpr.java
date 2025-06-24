package com.macslang.parser.ast.expression;

import com.macslang.lexer.Token;
import com.macslang.parser.ast.NodeVisitor;
import java.util.List;

public class FuncCallExpr implements Expression {
private final Token name;
private final List<Expression> arguments;

public FuncCallExpr(Token name, List<Expression> arguments) {
    this.name = name;
    this.arguments = arguments;
}

public Token getName() {
    return name;
}

public List<Expression> getArguments() {
    return arguments;
}

@Override
public <T> T accept(NodeVisitor<T> visitor) {
    return visitor.visitFuncCallExpr(this);
}
}