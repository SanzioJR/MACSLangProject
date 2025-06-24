package com.macslang.parser;

import com.macslang.lexer.Token;
import com.macslang.lexer.TokenType;
import com.macslang.parser.ast.Program;
import com.macslang.parser.ast.expression.*;
import com.macslang.parser.ast.statement.*;

import java.util.ArrayList;
import java.util.List;

public class Parser {
private final List<Token> tokens;
private int current = 0;

public Parser(List<Token> tokens) {
    this.tokens = tokens;
}

public Program parse() {
    List<Statement> statements = new ArrayList();
    
    while (!isAtEnd()) {
        statements.add(declaration());
    }
    
    return new Program(statements);
}

private Statement declaration() {
    try {
        if (match(TokenType.VAR)) return varDeclaration();
        if (match(TokenType.FUNC)) return funcDeclaration();
        
        return statement();
    } catch (ParserException e) {
        synchronize();
        return null;
    }
}

private Statement varDeclaration() {
    Token name = consume(TokenType.IDENTIFIER, "Esperado nome da variável.");
    
    consume(TokenType.COLON, "Esperado ':' após nome da variável.");
    Token type = consumeType("Esperado tipo da variável.");
    
    Expression initializer = null;
    if (match(TokenType.ASSIGN)) {
        initializer = expression();
    }
    
    consume(TokenType.SEMICOLON, "Esperado ';' após declaração de variável.");
    return new VarDecl(name, type, initializer);
}

private Statement funcDeclaration() {
    Token name = consume(TokenType.IDENTIFIER, "Esperado nome da função.");
    
    consume(TokenType.LEFT_PAREN, "Esperado '(' após nome da função.");
    List<FuncDecl.Parameter> parameters = new ArrayList();
    
    if (!check(TokenType.RIGHT_PAREN)) {
        do {
            Token paramName = consume(TokenType.IDENTIFIER, "Esperado nome do parâmetro.");
            consume(TokenType.COLON, "Esperado ':' após nome do parâmetro.");
            Token paramType = consumeType("Esperado tipo do parâmetro.");
            
            parameters.add(new FuncDecl.Parameter(paramName, paramType));
        } while (match(TokenType.COMMA));
    }
    
    consume(TokenType.RIGHT_PAREN, "Esperado ')' após parâmetros.");
    
    // Tipo de retorno
    consume(TokenType.COLON, "Esperado ':' após parâmetros.");
    Token returnType = consumeType("Esperado tipo de retorno da função.");
    
    // Corpo da função
    consume(TokenType.LEFT_BRACE, "Esperado '{' antes do corpo da função.");
    BlockStatement body = blockStatement();
    
    return new FuncDecl(name, parameters, returnType, body);
}

private Statement statement() {
    if (match(TokenType.IF)) return ifStatement();
    if (match(TokenType.WHILE)) return whileStatement();
    if (match(TokenType.FOR)) return forStatement();
    if (match(TokenType.RETURN)) return returnStatement();
    if (match(TokenType.PRINT)) return printStatement();
    if (match(TokenType.INPUT)) return inputStatement();
    if (match(TokenType.LEFT_BRACE)) return blockStatement();
    
    return expressionStatement();
}

private Statement ifStatement() {
    consume(TokenType.LEFT_PAREN, "Esperado '(' após 'if'.");
    Expression condition = expression();
    consume(TokenType.RIGHT_PAREN, "Esperado ')' após condição.");
    
    Statement thenBranch = statement();
    Statement elseBranch = null;
    
    if (match(TokenType.ELSE)) {
        elseBranch = statement();
    }
    
    return new IfStatement(condition, thenBranch, elseBranch);
}

private Statement whileStatement() {
    consume(TokenType.LEFT_PAREN, "Esperado '(' após 'while'.");
    Expression condition = expression();
    consume(TokenType.RIGHT_PAREN, "Esperado ')' após condição.");
    
    Statement body = statement();
    
    return new WhileStatement(condition, body);
}

private Statement forStatement() {
    consume(TokenType.LEFT_PAREN, "Esperado '(' após 'for'.");
    
    // Inicialização
    Statement initializer;
    if (match(TokenType.SEMICOLON)) {
        initializer = null;
    } else if (match(TokenType.VAR)) {
        initializer = varDeclaration();
    } else {
        initializer = expressionStatement();
    }
    
    // Condição
    Expression condition = null;
    if (!check(TokenType.SEMICOLON)) {
        condition = expression();
    }
    consume(TokenType.SEMICOLON, "Esperado ';' após condição do loop.");
    
    // Incremento
    Expression increment = null;
    if (!check(TokenType.RIGHT_PAREN)) {
        increment = expression();
    }
    consume(TokenType.RIGHT_PAREN, "Esperado ')' após cláusulas do for.");
    
    // Corpo
    Statement body = statement();
    
    return new ForStatement(initializer, condition, increment, body);
}

private Statement returnStatement() {
    Token keyword = previous();
    Expression value = null;
    
    if (!check(TokenType.SEMICOLON)) {
        value = expression();
    }
    
    consume(TokenType.SEMICOLON, "Esperado ';' após valor de retorno.");
    return new ReturnStatement(keyword, value);
}

private Statement printStatement() {
    consume(TokenType.LEFT_PAREN, "Esperado '(' após 'print'.");
    Expression value = expression();
    consume(TokenType.RIGHT_PAREN, "Esperado ')' após expressão.");
    consume(TokenType.SEMICOLON, "Esperado ';' após instrução print.");
    
    return new PrintStatement(value);
}

private Statement inputStatement() {
    consume(TokenType.LEFT_PAREN, "Esperado '(' após 'input'.");
    Token variable = consume(TokenType.IDENTIFIER, "Esperado nome de variável.");
    consume(TokenType.RIGHT_PAREN, "Esperado ')' após variável.");
    consume(TokenType.SEMICOLON, "Esperado ';' após instrução input.");
    
    return new InputStatement(variable);
}

private BlockStatement blockStatement() {
    List<Statement> statements = new ArrayList();
    
    while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
        statements.add(declaration());
    }
    
    consume(TokenType.RIGHT_BRACE, "Esperado '}' após bloco.");
    return new BlockStatement(statements);
}

private Statement expressionStatement() {
    Expression expr = expression();
    consume(TokenType.SEMICOLON, "Esperado ';' após expressão.");
    return new ExpressionStatement(expr);
}

private Expression expression() {
    return assignment();
}

private Expression assignment() {
    Expression expr = or();
    
    if (match(TokenType.ASSIGN)) {
        Token equals = previous();
        Expression value = assignment();
        
        if (expr instanceof VarExpr) {
            Token name = ((VarExpr) expr).getName();
            return new AssignExpr(name, value);
        }
        
        throw new ParserException("Alvo inválido para atribuição.", equals);
    }
    
    return expr;
}

private Expression or() {
    Expression expr = and();
    
    while (match(TokenType.OR)) {
        Token operator = previous();
        Expression right = and();
        expr = new BinaryExpr(expr, operator, right);
    }
    
    return expr;
}

private Expression and() {
    Expression expr = equality();
    
    while (match(TokenType.AND)) {
        Token operator = previous();
        Expression right = equality();
        expr = new BinaryExpr(expr, operator, right);
    }
    
    return expr;
}

private Expression equality() {
    Expression expr = comparison();
    
    while (match(TokenType.EQUALS, TokenType.NOT_EQUALS)) {
        Token operator = previous();
        Expression right = comparison();
        expr = new BinaryExpr(expr, operator, right);
    }
    
    return expr;
}

private Expression comparison() {
    Expression expr = term();
    
    while (match(TokenType.LESS, TokenType.LESS_EQUALS, TokenType.GREATER, TokenType.GREATER_EQUALS)) {
        Token operator = previous();
        Expression right = term();
        expr = new BinaryExpr(expr, operator, right);
    }
    
    return expr;
}

private Expression term() {
    Expression expr = factor();
    
    while (match(TokenType.PLUS, TokenType.MINUS)) {
        Token operator = previous();
        Expression right = factor();
        expr = new BinaryExpr(expr, operator, right);
    }
    
    return expr;
}

private Expression factor() {
Expression expr = unary();

while (match(TokenType.MULTIPLY, TokenType.DIVIDE, TokenType.MOD)) {  // Adicionado MOD aqui
    Token operator = previous();
    Expression right = unary();
    expr = new BinaryExpr(expr, operator, right);
}

return expr;
}

private Expression unary() {
    if (match(TokenType.NOT, TokenType.MINUS)) {
        Token operator = previous();
        Expression right = unary();
        return new UnaryExpr(operator, right);
    }
    
    return call();
}

private Expression call() {
    Expression expr = primary();
    
    while (true) {
        if (match(TokenType.LEFT_PAREN)) {
            expr = finishCall(expr);
        } else {
            break;
        }
    }
    
    return expr;
}

private Expression finishCall(Expression callee) {
    List<Expression> arguments = new ArrayList();
    
    if (!check(TokenType.RIGHT_PAREN)) {
        do {
            arguments.add(expression());
        } while (match(TokenType.COMMA));
    }
    
    Token paren = consume(TokenType.RIGHT_PAREN, "Esperado ')' após argumentos.");
    
    if (!(callee instanceof VarExpr)) {
        throw new ParserException("Pode chamar apenas funções.", paren);
    }
    
    Token name = ((VarExpr) callee).getName();
    return new FuncCallExpr(name, arguments);
}

private Expression primary() {
    if (match(TokenType.BOOL_LITERAL)) return new LiteralExpr(previous().getLiteral());
    if (match(TokenType.INT_LITERAL)) return new LiteralExpr(previous().getLiteral());
    if (match(TokenType.FLOAT_LITERAL)) return new LiteralExpr(previous().getLiteral());
    if (match(TokenType.CHAR_LITERAL)) return new LiteralExpr(previous().getLiteral());
    if (match(TokenType.STRING_LITERAL)) return new LiteralExpr(previous().getLiteral());
    
    if (match(TokenType.IDENTIFIER)) {
        return new VarExpr(previous());
    }
    
    if (match(TokenType.LEFT_PAREN)) {
        Expression expr = expression();
        consume(TokenType.RIGHT_PAREN, "Esperado ')' após expressão.");
        return expr;
    }
    
    throw new ParserException("Esperado expressão.", peek());
}

private Token consumeType(String message) {
    if (check(TokenType.TYPE_INT) || check(TokenType.TYPE_FLOAT) || 
        check(TokenType.TYPE_CHAR) || check(TokenType.TYPE_BOOL) || 
        check(TokenType.TYPE_STRING)) {
        return advance();
    }
    
    throw new ParserException(message, peek());
}

private boolean match(TokenType... types) {
    for (TokenType type : types) {
        if (check(type)) {
            advance();
            return true;
        }
    }
    
    return false;
}

private boolean check(TokenType type) {
    if (isAtEnd()) return false;
    return peek().getType() == type;
}

private Token advance() {
    if (!isAtEnd()) current++;
    return previous();
}

private boolean isAtEnd() {
    return peek().getType() == TokenType.EOF;
}

private Token peek() {
    return tokens.get(current);
}

private Token previous() {
    return tokens.get(current - 1);
}

private Token consume(TokenType type, String message) {
    if (check(type)) return advance();
    
    throw new ParserException(message, peek());
}

private void synchronize() {
    advance();
    
    while (!isAtEnd()) {
        if (previous().getType() == TokenType.SEMICOLON) return;
        
        switch (peek().getType()) {
            case VAR:
            case FUNC:
            case IF:
            case WHILE:
            case FOR:
            case RETURN:
            case PRINT:
            case INPUT:
                return;
            default:
                // Continua procurando
        }
        
        advance();
    }
}
}