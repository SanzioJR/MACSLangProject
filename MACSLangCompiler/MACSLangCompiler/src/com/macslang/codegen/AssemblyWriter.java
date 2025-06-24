package com.macslang.codegen;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class AssemblyWriter {
private final List<String> dataSection = new ArrayList();
private final List<String> codeSection = new ArrayList();
private int labelCounter = 0;

public void addData(String name, String directive, String value) {
    dataSection.add(name + " " + directive + " " + value);
}

public void addInstruction(X86Instruction instruction) {
    codeSection.add("    " + instruction.toString());
}

public void addLabel(String label) {
    codeSection.add(label + ":");
}

public String generateUniqueLabel(String prefix) {
    return prefix + "_" + (labelCounter++);
}

public void writeToFile(String filename) throws IOException {
    try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
        // Cabeçalho
        writer.println("; Código assembly gerado pelo compilador MACSLang");
        writer.println();
        
        // Seção de dados
        writer.println("section .data");
        for (String data : dataSection) {
            writer.println("    " + data);
        }
        writer.println();
        
        // Seção de código
        writer.println("section .text");
        writer.println("    global _start");
        writer.println();
        writer.println("_start:");
        
        for (String code : codeSection) {
            writer.println(code);
        }
        
        // Saída do programa
        writer.println();
        writer.println("    ; Saída do programa");
        writer.println("    mov eax, 1      ; syscall exit");
        writer.println("    xor ebx, ebx    ; código de retorno 0");
        writer.println("    int 0x80        ; chamada de sistema");
    }
}
}