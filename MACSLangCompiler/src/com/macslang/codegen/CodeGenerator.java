package com.macslang.codegen;

import com.macslang.lexer.Token;
import com.macslang.lexer.TokenType;
import com.macslang.parser.ast.NodeVisitor;
import com.macslang.parser.ast.Program;
import com.macslang.parser.ast.expression.*;
import com.macslang.parser.ast.statement.*;
import com.macslang.semantic.Symbol;
import com.macslang.semantic.SymbolTable;
import com.macslang.semantic.Type;

import java.util.HashMap;
import java.util.Map;

public class CodeGenerator implements NodeVisitor<Void> {
private final AssemblyWriter writer;
private final SymbolTable globalScope;
private final Map<String, Integer> localVariables = new HashMap();
private int stackOffset = 0;
private int stringCounter = 0;

public CodeGenerator(AssemblyWriter writer, SymbolTable globalScope) {
    this.writer = writer;
    this.globalScope = globalScope;
}

public void generate(Program program) {
    // Adicionar funções de biblioteca
    addLibraryFunctions();
    
    // Gerar código para o programa
    program.accept(this);
}


private void addLibraryFunctions() {
    // Implementação...
}

@Override
public Void visitProgram(Program program) {
    // Implementação...
    return null;
}

// Implementação dos métodos de visita para cada tipo de nó da AST

// Por exemplo, para uma declaração de variável:
@Override
public Void visitVarDecl(VarDecl varDecl) {
    String name = varDecl.getName().getLexeme();
    
    // Alocar espaço na pilha para a variável
    writer.addInstruction(new X86Instruction("sub", "esp", "4", "alocar espaço para " + name));
    localVariables.put(name, stackOffset);
    stackOffset += 4;
    
    // Inicializar a variável, se necessário
    if (varDecl.getInitializer() != null) {
        varDecl.getInitializer().accept(this);
        writer.addInstruction(new X86Instruction("pop", "eax", null, "valor inicial"));
        writer.addInstruction(new X86Instruction("mov", "[ebp-" + localVariables.get(name) + "]", "eax"));
    }
    
    return null;
}

// Outros métodos de visita seriam implementados de forma semelhante

@Override
public Void visitFuncDecl(FuncDecl funcDecl) {
    // Implementação simplificada
    return null;
}

@Override
public Void visitBlockStatement(BlockStatement block) {
    for (Statement stmt : block.getStatements()) {
        stmt.accept(this);
    }
    return null;
}

@Override
public Void visitIfStatement(IfStatement ifStmt) {
    // Implementação simplificada
    return null;
}

@Override
public Void visitWhileStatement(WhileStatement whileStmt) {
    // Implementação simplificada
    return null;
}

@Override
public Void visitForStatement(ForStatement forStmt) {
    // Implementação simplificada
    return null;
}

@Override
public Void visitReturnStatement(ReturnStatement returnStmt) {
    // Implementação simplificada
    return null;
}

@Override
public Void visitPrintStatement(PrintStatement printStmt) {
    // Avaliar a expressão e colocar o resultado na pilha
    printStmt.getExpression().accept(this);
    
    // Chamar a função print
    writer.addInstruction(new X86Instruction("call", "print"));
    writer.addInstruction(new X86Instruction("add", "esp", "4", "limpar argumento"));
    
    return null;
}

@Override
public Void visitInputStatement(InputStatement inputStmt) {
    // Implementação simplificada
    return null;
}

@Override
public Void visitExpressionStatement(ExpressionStatement exprStmt) {
    exprStmt.getExpression().accept(this);
    writer.addInstruction(new X86Instruction("add", "esp", "4", "descartar resultado"));
    return null;
}

@Override
public Void visitBinaryExpr(BinaryExpr expr) {
// Avaliar os operandos
expr.getLeft().accept(this);
expr.getRight().accept(this);

// Gerar código para a operação
writer.addInstruction(new X86Instruction("pop", "ebx"));  // Segundo operando
writer.addInstruction(new X86Instruction("pop", "eax"));  // Primeiro operando

switch (expr.getOperator().getType()) {
    case PLUS:
        writer.addInstruction(new X86Instruction("add", "eax", "ebx"));
        break;
    case MINUS:
        writer.addInstruction(new X86Instruction("sub", "eax", "ebx"));
        break;
    case MULTIPLY:
        writer.addInstruction(new X86Instruction("imul", "eax", "ebx"));
        break;
    case DIVIDE:
        writer.addInstruction(new X86Instruction("cdq"));  // Estender eax para edx:eax
        writer.addInstruction(new X86Instruction("idiv", "ebx"));
        break;
    case MOD:  // Adicionado aqui
        writer.addInstruction(new X86Instruction("cdq"));  // Estender eax para edx:eax
        writer.addInstruction(new X86Instruction("idiv", "ebx"));
        writer.addInstruction(new X86Instruction("mov", "eax", "edx"));  // O resto fica em edx
        break;
    case EQUALS:
        writer.addInstruction(new X86Instruction("cmp", "eax", "ebx"));
        writer.addInstruction(new X86Instruction("sete", "al"));
        writer.addInstruction(new X86Instruction("movzx", "eax", "al"));
        break;
    case NOT_EQUALS:
        writer.addInstruction(new X86Instruction("cmp", "eax", "ebx"));
        writer.addInstruction(new X86Instruction("setne", "al"));
        writer.addInstruction(new X86Instruction("movzx", "eax", "al"));
        break;
    case LESS:
        writer.addInstruction(new X86Instruction("cmp", "eax", "ebx"));
        writer.addInstruction(new X86Instruction("setl", "al"));
        writer.addInstruction(new X86Instruction("movzx", "eax", "al"));
        break;
    case LESS_EQUALS:
        writer.addInstruction(new X86Instruction("cmp", "eax", "ebx"));
        writer.addInstruction(new X86Instruction("setle", "al"));
        writer.addInstruction(new X86Instruction("movzx", "eax", "al"));
        break;
    case GREATER:
        writer.addInstruction(new X86Instruction("cmp", "eax", "ebx"));
        writer.addInstruction(new X86Instruction("setg", "al"));
        writer.addInstruction(new X86Instruction("movzx", "eax", "al"));
        break;
    case GREATER_EQUALS:
        writer.addInstruction(new X86Instruction("cmp", "eax", "ebx"));
        writer.addInstruction(new X86Instruction("setge", "al"));
        writer.addInstruction(new X86Instruction("movzx", "eax", "al"));
        break;
    case AND:
        writer.addInstruction(new X86Instruction("and", "eax", "ebx"));
        break;
    case OR:
        writer.addInstruction(new X86Instruction("or", "eax", "ebx"));
        break;
}

writer.addInstruction(new X86Instruction("push", "eax"));  // Empilhar o resultado
return null;
}

@Override
public Void visitUnaryExpr(UnaryExpr expr) {
    // Implementação simplificada
    return null;
}

@Override
public Void visitLiteralExpr(LiteralExpr expr) {
    Object value = expr.getValue();
    
    if (value instanceof Integer) {
        writer.addInstruction(new X86Instruction("push", value.toString()));
    } else if (value instanceof Double) {
        // Simplificado: tratando float como int
        writer.addInstruction(new X86Instruction("push", Integer.toString(((Double) value).intValue())));
    } else if (value instanceof Boolean) {
        writer.addInstruction(new X86Instruction("push", ((Boolean) value) ? "1" : "0"));
    } else if (value instanceof Character) {
        writer.addInstruction(new X86Instruction("push", Integer.toString((int) ((Character) value))));
    } else if (value instanceof String) {
        String stringLabel = "str" + (stringCounter++);
        writer.addData(stringLabel, "db", "\"" + value + "\", 0");
        writer.addInstruction(new X86Instruction("push", stringLabel));
    }
    
    return null;
}

@Override
public Void visitVarExpr(VarExpr expr) {
    String name = expr.getName().getLexeme();
    Integer offset = localVariables.get(name);
    
    if (offset != null) {
        writer.addInstruction(new X86Instruction("mov", "eax", "[ebp-" + offset + "]", name));
        writer.addInstruction(new X86Instruction("push", "eax"));
    } else {
        // Variável global ou erro
        writer.addInstruction(new X86Instruction("push", "0", null, "erro: variável não encontrada"));
    }
    
    return null;
}

@Override
public Void visitAssignExpr(AssignExpr expr) {
    // Avaliar o valor a ser atribuído
    expr.getValue().accept(this);
    
    String name = expr.getName().getLexeme();
    Integer offset = localVariables.get(name);
    
    if (offset != null) {
        writer.addInstruction(new X86Instruction("pop", "eax"));
        writer.addInstruction(new X86Instruction("mov", "[ebp-" + offset + "]", "eax", name));
        writer.addInstruction(new X86Instruction("push", "eax", null, "resultado da atribuição"));
    } else {
        // Variável global ou erro
        writer.addInstruction(new X86Instruction("add", "esp", "4", "descartar valor"));
        writer.addInstruction(new X86Instruction("push", "0", null, "erro: variável não encontrada"));
    }
    
    return null;
}

@Override
public Void visitFuncCallExpr(FuncCallExpr expr) {
    // Implementação simplificada
    return null;
}
}