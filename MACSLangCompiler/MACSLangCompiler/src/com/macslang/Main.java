package com.macslang;

import com.macslang.codegen.AssemblyWriter;
import com.macslang.codegen.CodeGenerator;
import com.macslang.lexer.Lexer;
import com.macslang.lexer.Token;
import com.macslang.parser.Parser;
import com.macslang.parser.ast.Program;
import com.macslang.semantic.SemanticAnalyzer;
import com.macslang.semantic.SymbolTable;
import com.macslang.util.FileHandler;

import java.io.IOException;
import java.util.List;

public class Main {
public static void main(String[] args) {
    if (args.length < 1) {
        System.out.println("Uso: java -jar MACSLangCompiler.jar <arquivo.macs>");
        return;
    }
    
    String inputFile = args[0];
    String outputFile = inputFile.replaceAll("\\.macs$", ".asm");
    
    try {
        // Ler o arquivo fonte
        String source = FileHandler.readFile(inputFile);
        
        // Análise léxica
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();
        
        // Análise sintática
        Parser parser = new Parser(tokens);
        Program program = parser.parse();
        
        // Análise semântica
        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
        semanticAnalyzer.analyze(program);
        
        // Geração de código
        AssemblyWriter writer = new AssemblyWriter();
        SymbolTable globalScope = new SymbolTable();
        CodeGenerator codeGenerator = new CodeGenerator(writer, globalScope);
        codeGenerator.generate(program);
        
        // Escrever o código assembly no arquivo de saída
        writer.writeToFile(outputFile);
        
        System.out.println("Compilação concluída com sucesso!");
        System.out.println("Código assembly gerado em: " + outputFile);
        
    } catch (IOException e) {
        System.err.println("Erro ao ler/escrever arquivo: " + e.getMessage());
    } catch (Exception e) {
        System.err.println("Erro durante a compilação: " + e.getMessage());
        e.printStackTrace();
    }
}
}