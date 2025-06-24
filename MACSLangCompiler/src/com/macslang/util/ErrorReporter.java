package com.macslang.util;

public class ErrorReporter {
private int errorCount = 0;

public void error(int line, int column, String message) {
    report(line, column, "", message);
}

public void error(int line, String where, String message) {
    report(line, -1, where, message);
}

private void report(int line, int column, String where, String message) {
    String location = line >= 0 ? "linha " + line : "";
    if (column >= 0) {
        location += (location.isEmpty() ? "" : ", ") + "coluna " + column;
    }
    
    System.err.println("[Erro" + (location.isEmpty() ? "" : " na " + location) + "] " + 
                      (where.isEmpty() ? "" : where + ": ") + message);
    errorCount++;
}

public int getErrorCount() {
    return errorCount;
}

public boolean hasErrors() {
    return errorCount > 0;
}
}