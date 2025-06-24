package com.macslang.parser.ast.statement;

import com.macslang.lexer.Token;
import com.macslang.parser.ast.NodeVisitor;
import java.util.List;

public class FuncDecl implements Statement {
private final Token name;
private final List<Parameter> parameters;
private final Token returnType;
private final BlockStatement body;

public static class Parameter {
    private final Token name;
    private final Token type;
    
    public Parameter(Token name, Token type) {
        this.name = name;
        this.type = type;
    }
    
    public Token getName() {
        return name;
    }
    
    public Token getType() {
        return type;
    }
}

public FuncDecl(Token name, List<Parameter> parameters, Token returnType, BlockStatement body) {
    this.name = name;
    this.parameters = parameters;
    this.returnType = returnType;
    this.body = body;
}

public Token getName() {
    return name;
}

public List<Parameter> getParameters() {
    return parameters;
}

public Token getReturnType() {
    return returnType;
}

public BlockStatement getBody() {
    return body;
}

@Override
public <T> T accept(NodeVisitor<T> visitor) {
    return visitor.visitFuncDecl(this);
}
}