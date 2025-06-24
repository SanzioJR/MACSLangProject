package com.macslang.parser.ast.statement;

import com.macslang.lexer.Token;
import com.macslang.parser.ast.NodeVisitor;
import com.macslang.parser.ast.expression.Expression;

public class ReturnStatement implements Statement {
private final Token keyword;
private final Expression value;

public ReturnStatement(Token keyword, Expression value) {
    this.keyword = keyword;
    this.value = value;
}

public Token getKeyword() {
    return keyword;
}

public Expression getValue() {
    return value;
}

@Override
public <T> T accept(NodeVisitor<T> visitor) {
    return visitor.visitReturnStatement(this);
}
}