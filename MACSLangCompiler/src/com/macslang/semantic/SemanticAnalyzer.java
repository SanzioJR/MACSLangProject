package com.macslang.semantic;

import com.macslang.lexer.Token;
import com.macslang.lexer.TokenType;
import com.macslang.parser.ast.NodeVisitor;
import com.macslang.parser.ast.Program;
import com.macslang.parser.ast.expression.*;
import com.macslang.parser.ast.statement.*;

import java.util.ArrayList;
import java.util.List;

public class SemanticAnalyzer implements NodeVisitor<Type> {
private SymbolTable currentScope;
private Type currentFunctionReturnType;
private boolean hasReturn;

public SemanticAnalyzer() {
    this.currentScope = new SymbolTable();
}

public void analyze(Program program) {
    visitProgram(program);
}

@Override
public Type visitProgram(Program program) {
    for (Statement stmt : program.getStatements()) {
        stmt.accept(this);
    }
    return null;
}

@Override
public Type visitVarDecl(VarDecl varDecl) {
    Token nameToken = varDecl.getName();
    Token typeToken = varDecl.getType();
    Type type = Type.fromTokenType(typeToken.getType());
    
    if (currentScope.existsInCurrentScope(nameToken.getLexeme())) {
        throw new SemanticException("Variável já declarada neste escopo: " + nameToken.getLexeme(), nameToken);
    }
    
    if (varDecl.getInitializer() != null) {
        Type initType = varDecl.getInitializer().accept(this);
        if (!type.isCompatibleWith(initType)) {
            throw new SemanticException(
                "Tipo incompatível na inicialização. Esperado " + type + ", encontrado " + initType, 
                nameToken
            );
        }
    }
    
    Symbol symbol = new Symbol(nameToken.getLexeme(), type, Symbol.SymbolKind.VARIABLE, nameToken);
    currentScope.define(symbol);
    
    return null;
}

@Override
public Type visitFuncDecl(FuncDecl funcDecl) {
    Token nameToken = funcDecl.getName();
    Token returnTypeToken = funcDecl.getReturnType();
    Type returnType = Type.fromTokenType(returnTypeToken.getType());
    
    if (currentScope.existsInCurrentScope(nameToken.getLexeme())) {
        throw new SemanticException("Função já declarada neste escopo: " + nameToken.getLexeme(), nameToken);
    }
    
    List<Type> paramTypes = new ArrayList();
    for (FuncDecl.Parameter param : funcDecl.getParameters()) {
        Type paramType = Type.fromTokenType(param.getType().getType());
        paramTypes.add(paramType);
    }
    
    Symbol symbol = new Symbol(nameToken.getLexeme(), returnType, Symbol.SymbolKind.FUNCTION, nameToken);
    currentScope.define(symbol);
    
    // Criar novo escopo para o corpo da função
    SymbolTable previousScope = currentScope;
    currentScope = new SymbolTable(previousScope);
    
    // Definir parâmetros no novo escopo
    for (FuncDecl.Parameter param : funcDecl.getParameters()) {
        Token paramName = param.getName();
        Type paramType = Type.fromTokenType(param.getType().getType());
        
        if (currentScope.existsInCurrentScope(paramName.getLexeme())) {
            throw new SemanticException("Parâmetro duplicado: " + paramName.getLexeme(), paramName);
        }
        
        Symbol paramSymbol = new Symbol(paramName.getLexeme(), paramType, Symbol.SymbolKind.PARAMETER, paramName);
        currentScope.define(paramSymbol);
    }
    
    // Verificar corpo da função
    Type previousReturnType = currentFunctionReturnType;
    boolean previousHasReturn = hasReturn;
    
    currentFunctionReturnType = returnType;
    hasReturn = false;
    
    funcDecl.getBody().accept(this);
    
    // Verificar se a função tem return quando necessário
    if (returnType != Type.VOID && !hasReturn) {
        throw new SemanticException(
            "Função '" + nameToken.getLexeme() + "' deve retornar um valor do tipo " + returnType,
            nameToken
        );
    }
    
    // Restaurar estado anterior
    currentScope = previousScope;
    currentFunctionReturnType = previousReturnType;
    hasReturn = previousHasReturn;
    
    return null;
}

@Override
public Type visitBlockStatement(BlockStatement block) {
    // Criar novo escopo para o bloco
    SymbolTable previousScope = currentScope;
    currentScope = new SymbolTable(previousScope);
    
    for (Statement stmt : block.getStatements()) {
        stmt.accept(this);
    }
    
    // Restaurar escopo anterior
    currentScope = previousScope;
    
    return null;
}

@Override
public Type visitIfStatement(IfStatement ifStmt) {
    Type conditionType = ifStmt.getCondition().accept(this);
    
    if (conditionType != Type.BOOL) {
        throw new SemanticException(
            "Condição do if deve ser do tipo bool, encontrado " + conditionType,
            null
        );
    }
    
    ifStmt.getThenBranch().accept(this);
    
    if (ifStmt.getElseBranch() != null) {
        ifStmt.getElseBranch().accept(this);
    }
    
    return null;
}

@Override
public Type visitWhileStatement(WhileStatement whileStmt) {
    Type conditionType = whileStmt.getCondition().accept(this);
    
    if (conditionType != Type.BOOL) {
        throw new SemanticException(
            "Condição do while deve ser do tipo bool, encontrado " + conditionType,
            null
        );
    }
    
    whileStmt.getBody().accept(this);
    
    return null;
}
@Override
public Type visitForStatement(ForStatement forStmt) {
    // Criar novo escopo para o for
    SymbolTable previousScope = currentScope;
    currentScope = new SymbolTable(previousScope);
    
    if (forStmt.getInitializer() != null) {
        forStmt.getInitializer().accept(this);
    }
    
    if (forStmt.getCondition() != null) {
        Type conditionType = forStmt.getCondition().accept(this);
        if (conditionType != Type.BOOL) {
            throw new SemanticException(
                "Condição do for deve ser do tipo bool, encontrado " + conditionType,
                null
            );
        }
    }
    
    if (forStmt.getIncrement() != null) {
        forStmt.getIncrement().accept(this);
    }
    
    forStmt.getBody().accept(this);
    
    // Restaurar escopo anterior
    currentScope = previousScope;
    
    return null;
}

@Override
public Type visitReturnStatement(ReturnStatement returnStmt) {
    if (currentFunctionReturnType == null) {
        throw new SemanticException("Return fora de função", returnStmt.getKeyword());
    }
    
    hasReturn = true;
    
    if (returnStmt.getValue() == null) {
        if (currentFunctionReturnType != Type.VOID) {
            throw new SemanticException(
                "Função deve retornar um valor do tipo " + currentFunctionReturnType,
                returnStmt.getKeyword()
            );
        }
    } else {
        Type valueType = returnStmt.getValue().accept(this);
        if (!currentFunctionReturnType.isCompatibleWith(valueType)) {
            throw new SemanticException(
                "Tipo de retorno incompatível. Esperado " + currentFunctionReturnType + ", encontrado " + valueType,
                returnStmt.getKeyword()
            );
        }
    }
    
    return null;
}

@Override
public Type visitPrintStatement(PrintStatement printStmt) {
    printStmt.getExpression().accept(this);
    return null;
}

@Override
public Type visitInputStatement(InputStatement inputStmt) {
    String varName = inputStmt.getVariable().getLexeme();
    Symbol symbol = currentScope.resolve(varName);
    
    if (symbol == null) {
        throw new SemanticException("Variável não declarada: " + varName, inputStmt.getVariable());
    }
    
    return null;
}

@Override
public Type visitExpressionStatement(ExpressionStatement exprStmt) {
    exprStmt.getExpression().accept(this);
    return null;
}

@Override
public Type visitBinaryExpr(BinaryExpr expr) {
Type leftType = expr.getLeft().accept(this);
Type rightType = expr.getRight().accept(this);
TokenType operator = expr.getOperator().getType();

switch (operator) {
    case PLUS:
    case MINUS:
    case MULTIPLY:
    case DIVIDE:
    case MOD:  // Adicionado aqui
        if (leftType == Type.INT && rightType == Type.INT) {
            return Type.INT;
        } else if ((leftType == Type.INT || leftType == Type.FLOAT) && 
                   (rightType == Type.INT || rightType == Type.FLOAT)) {
            // Para MOD, restringimos apenas a inteiros
            if (operator == TokenType.MOD && (leftType == Type.FLOAT || rightType == Type.FLOAT)) {
                throw new SemanticException(
                    "Operador % só pode ser aplicado a inteiros",
                    expr.getOperator()
                );
            }
            return Type.FLOAT;
        } else if (operator == TokenType.PLUS && 
                  (leftType == Type.STRING || rightType == Type.STRING)) {
            return Type.STRING;
        } else {
            throw new SemanticException(
                "Operador " + expr.getOperator().getLexeme() + 
                " não pode ser aplicado a tipos " + leftType + " e " + rightType,
                expr.getOperator()
            );
        }
            
        case EQUALS:
        case NOT_EQUALS:
            if (!leftType.isCompatibleWith(rightType) && !rightType.isCompatibleWith(leftType)) {
                throw new SemanticException(
                    "Não é possível comparar tipos incompatíveis " + leftType + " e " + rightType,
                    expr.getOperator()
                );
            }
            return Type.BOOL;
            
        case LESS:
        case LESS_EQUALS:
        case GREATER:
        case GREATER_EQUALS:
            if ((leftType == Type.INT || leftType == Type.FLOAT) && 
                (rightType == Type.INT || rightType == Type.FLOAT)) {
                return Type.BOOL;
            } else {
                throw new SemanticException(
                    "Operador " + expr.getOperator().getLexeme() + 
                    " só pode ser aplicado a tipos numéricos",
                    expr.getOperator()
                );
            }
            
        case AND:
        case OR:
            if (leftType == Type.BOOL && rightType == Type.BOOL) {
                return Type.BOOL;
            } else {
                throw new SemanticException(
                    "Operador " + expr.getOperator().getLexeme() + 
                    " só pode ser aplicado a booleanos",
                    expr.getOperator()
                );
            }
            
        default:
            throw new SemanticException(
                "Operador não suportado: " + expr.getOperator().getLexeme(),
                expr.getOperator()
            );
    }
}

@Override
public Type visitUnaryExpr(UnaryExpr expr) {
    Type rightType = expr.getRight().accept(this);
    TokenType operator = expr.getOperator().getType();
    
    switch (operator) {
        case MINUS:
            if (rightType == Type.INT) {
                return Type.INT;
            } else if (rightType == Type.FLOAT) {
                return Type.FLOAT;
            } else {
                throw new SemanticException(
                    "Operador unário '-' só pode ser aplicado a tipos numéricos",
                    expr.getOperator()
                );
            }
            
        case NOT:
            if (rightType == Type.BOOL) {
                return Type.BOOL;
            } else {
                throw new SemanticException(
                    "Operador unário '!' só pode ser aplicado a booleanos",
                    expr.getOperator()
                );
            }
            
        default:
            throw new SemanticException(
                "Operador unário não suportado: " + expr.getOperator().getLexeme(),
                expr.getOperator()
            );
    }
}

@Override
public Type visitLiteralExpr(LiteralExpr expr) {
    Object value = expr.getValue();
    
    if (value instanceof Integer) return Type.INT;
    if (value instanceof Double) return Type.FLOAT;
    if (value instanceof Character) return Type.CHAR;
    if (value instanceof Boolean) return Type.BOOL;
    if (value instanceof String) return Type.STRING;
    
    return null;
}

@Override
public Type visitVarExpr(VarExpr expr) {
    String name = expr.getName().getLexeme();
    Symbol symbol = currentScope.resolve(name);
    
    if (symbol == null) {
        throw new SemanticException("Variável não declarada: " + name, expr.getName());
    }
    
    return symbol.getType();
}

@Override
public Type visitAssignExpr(AssignExpr expr) {
    String name = expr.getName().getLexeme();
    Symbol symbol = currentScope.resolve(name);
    
    if (symbol == null) {
        throw new SemanticException("Variável não declarada: " + name, expr.getName());
    }
    
    Type valueType = expr.getValue().accept(this);
    
    if (!symbol.getType().isCompatibleWith(valueType)) {
        throw new SemanticException(
            "Tipo incompatível na atribuição. Esperado " + symbol.getType() + ", encontrado " + valueType,
            expr.getName()
        );
    }
    
    return valueType;
}

@Override
public Type visitFuncCallExpr(FuncCallExpr expr) {
    String name = expr.getName().getLexeme();
    Symbol symbol = currentScope.resolve(name);
    
    if (symbol == null) {
        throw new SemanticException("Função não declarada: " + name, expr.getName());
    }
    
    if (symbol.getKind() != Symbol.SymbolKind.FUNCTION) {
        throw new SemanticException("'" + name + "' não é uma função", expr.getName());
    }
    
    // Verificar argumentos
    // Nota: Em uma implementação completa, verificaríamos o número e tipo dos argumentos
    // com base na definição da função
    
    for (Expression arg : expr.getArguments()) {
        arg.accept(this);
    }
    
    return symbol.getType();
}
}