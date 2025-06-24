package com.macslang.semantic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymbolTable {
private final Map<String, Symbol> symbols = new HashMap();
private final SymbolTable enclosing;

public SymbolTable() {
    this(null);
}

public SymbolTable(SymbolTable enclosing) {
    this.enclosing = enclosing;
}

public void define(Symbol symbol) {
    symbols.put(symbol.getName(), symbol);
}

public Symbol resolve(String name) {
    if (symbols.containsKey(name)) {
        return symbols.get(name);
    }
    
    if (enclosing != null) {
        return enclosing.resolve(name);
    }
    
    return null;
}

public boolean existsInCurrentScope(String name) {
    return symbols.containsKey(name);
}

public List<Symbol> getAllSymbols() {
    return new ArrayList(symbols.values());
}
}