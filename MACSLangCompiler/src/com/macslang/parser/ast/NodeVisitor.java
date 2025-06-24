package com.macslang.parser.ast;

import com.macslang.parser.ast.expression.*;
import com.macslang.parser.ast.statement.*;

public interface NodeVisitor<T> {
// Statements
T visitProgram(Program program);
T visitVarDecl(VarDecl varDecl);
T visitFuncDecl(FuncDecl funcDecl);
T visitBlockStatement(BlockStatement block);
T visitIfStatement(IfStatement ifStmt);
T visitWhileStatement(WhileStatement whileStmt);
T visitForStatement(ForStatement forStmt);
T visitReturnStatement(ReturnStatement returnStmt);
T visitPrintStatement(PrintStatement printStmt);
T visitInputStatement(InputStatement inputStmt);
T visitExpressionStatement(ExpressionStatement exprStmt);

// Expressions
T visitBinaryExpr(BinaryExpr expr);
T visitUnaryExpr(UnaryExpr expr);
T visitLiteralExpr(LiteralExpr expr);
T visitVarExpr(VarExpr expr);
T visitAssignExpr(AssignExpr expr);
T visitFuncCallExpr(FuncCallExpr expr);
}