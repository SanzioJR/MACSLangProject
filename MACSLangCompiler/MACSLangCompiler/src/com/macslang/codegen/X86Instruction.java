package com.macslang.codegen;

public class X86Instruction {
private final String opcode;
private final String operand1;
private final String operand2;
private final String comment;

public X86Instruction(String opcode) {
    this(opcode, null, null, null);
}

public X86Instruction(String opcode, String operand1) {
    this(opcode, operand1, null, null);
}

public X86Instruction(String opcode, String operand1, String operand2) {
    this(opcode, operand1, operand2, null);
}

public X86Instruction(String opcode, String operand1, String operand2, String comment) {
    this.opcode = opcode;
    this.operand1 = operand1;
    this.operand2 = operand2;
    this.comment = comment;
}

@Override
public String toString() {
    StringBuilder sb = new StringBuilder();
    
    sb.append(opcode);
    
    if (operand1 != null) {
        sb.append(" ").append(operand1);
        
        if (operand2 != null) {
            sb.append(", ").append(operand2);
        }
    }
    
    if (comment != null) {
        sb.append(" ; ").append(comment);
    }
    
    return sb.toString();
}
}