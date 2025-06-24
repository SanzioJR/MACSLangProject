package com.macslang.lexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lexer {
private final String source;
private final List<Token> tokens = new ArrayList();

private int start = 0;
private int current = 0;
private int line = 1;
private int column = 1;

private static final Map<String, TokenType> keywords;

static {
    keywords = new HashMap();
    keywords.put("var", TokenType.VAR);
    keywords.put("func", TokenType.FUNC);
    keywords.put("if", TokenType.IF);
    keywords.put("else", TokenType.ELSE);
    keywords.put("while", TokenType.WHILE);
    keywords.put("for", TokenType.FOR);
    keywords.put("return", TokenType.RETURN);
    keywords.put("print", TokenType.PRINT);
    keywords.put("input", TokenType.INPUT);
    keywords.put("int", TokenType.TYPE_INT);
    keywords.put("float", TokenType.TYPE_FLOAT);
    keywords.put("char", TokenType.TYPE_CHAR);
    keywords.put("bool", TokenType.TYPE_BOOL);
    keywords.put("string", TokenType.TYPE_STRING);
    keywords.put("true", TokenType.BOOL_LITERAL);
    keywords.put("false", TokenType.BOOL_LITERAL);
}

public Lexer(String source) {
    this.source = source;
}

public List<Token> scanTokens() {
    while (!isAtEnd()) {
        start = current;
        scanToken();
    }
    
    tokens.add(new Token(TokenType.EOF, "", null, line, column));
    return tokens;
}

private void scanToken() {
char c = advance();

switch (c) {
    // Caracteres simples
    case '(': addToken(TokenType.LEFT_PAREN); break;
    case ')': addToken(TokenType.RIGHT_PAREN); break;
    case '{': addToken(TokenType.LEFT_BRACE); break;
    case '}': addToken(TokenType.RIGHT_BRACE); break;
    case ',': addToken(TokenType.COMMA); break;
    case '-': addToken(TokenType.MINUS); break;
    case '+': addToken(TokenType.PLUS); break;
    case ';': addToken(TokenType.SEMICOLON); break;
    case ':': addToken(TokenType.COLON); break;
    case '*': addToken(TokenType.MULTIPLY); break;
    case '%': addToken(TokenType.MOD); break;  // Adicionado aqui
    
    // Operadores que podem ser compostos
    case '!': addToken(match('=') ? TokenType.NOT_EQUALS : TokenType.NOT); break;
    case '=': addToken(match('=') ? TokenType.EQUALS : TokenType.ASSIGN); break;
    case '<': addToken(match('=') ? TokenType.LESS_EQUALS : TokenType.LESS); break;
    case '>': addToken(match('=') ? TokenType.GREATER_EQUALS : TokenType.GREATER); break;
    
    // Divisão ou comentário
    case '/':
        if (match('/')) {
            // Comentário de linha
            while (peek() != '\n' && !isAtEnd()) advance();
        } else {
            addToken(TokenType.DIVIDE);
        }
        break;
            
        // Espaços em branco
        case ' ':
        case '\r':
        case '\t':
            // Ignorar espaços em branco
            break;
            
        case '\n':
            line++;
            column = 1; // Resetar coluna para 1 na nova linha
            break;
            
        // Literais
        case '"': string(); break;
        case '\'': character(); break;
            
        default:
            if (isDigit(c)) {
                number();
            } else if (isAlpha(c)) {
                identifier();
            } else {
                throw new LexerException("Caractere inesperado: " + c, line, column - 1);
            }
            break;
    }
}

private void identifier() {
    while (isAlphaNumeric(peek())) advance();
    
    String text = source.substring(start, current);
    TokenType type = keywords.getOrDefault(text, TokenType.IDENTIFIER);
    
    Object literal = null;
    if (type == TokenType.BOOL_LITERAL) {
        literal = Boolean.parseBoolean(text);
    }
    
    addToken(type, literal);
}

private void number() {
    boolean isFloat = false;
    
    while (isDigit(peek())) advance();
    
    // Verifica se é um número de ponto flutuante
    if (peek() == '.' && isDigit(peekNext())) {
        isFloat = true;
        
        // Consome o '.'
        advance();
        
        while (isDigit(peek())) advance();
    }
    
    String value = source.substring(start, current);
    
    if (isFloat) {
        addToken(TokenType.FLOAT_LITERAL, Double.parseDouble(value));
    } else {
        addToken(TokenType.INT_LITERAL, Integer.parseInt(value));
    }
}

private void string() {
    while (peek() != '"' && !isAtEnd()) {
        if (peek() == '\n') {
            line++;
            column = 1;
        }
        advance();
    }
    
    if (isAtEnd()) {
        throw new LexerException("String não terminada", line, column);
    }
    
    // Consome o '"' de fechamento
    advance();
    
    // Extrai o valor da string (sem as aspas)
    String value = source.substring(start + 1, current - 1);
    addToken(TokenType.STRING_LITERAL, value);
}

private void character() {
    if (isAtEnd() || peek() == '\n') {
        throw new LexerException("Caractere não terminado", line, column);
    }
    
    char value = advance();
    
    if (peek() != '\'') {
        throw new LexerException("Caractere deve ter apenas um símbolo", line, column);
    }
    
    // Consome o '\'' de fechamento
    advance();
    
    addToken(TokenType.CHAR_LITERAL, value);
}

private boolean match(char expected) {
    if (isAtEnd()) return false;
    if (source.charAt(current) != expected) return false;
    
    current++;
    column++;
    return true;
}

private char peek() {
    if (isAtEnd()) return '\0';
    return source.charAt(current);
}

private char peekNext() {
    if (current + 1 >= source.length()) return '\0';
    return source.charAt(current + 1);
}

private boolean isAlpha(char c) {
    return (c >= 'a' && c <= 'z') ||
           (c >= 'A' && c <= 'Z') ||
            c == '_';
}

private boolean isAlphaNumeric(char c) {
    return isAlpha(c) || isDigit(c);
}

private boolean isDigit(char c) {
    return c >= '0' && c <= '9';
}

private boolean isAtEnd() {
    return current >= source.length();
}

private char advance() {
    column++;
    return source.charAt(current++);
}

private void addToken(TokenType type) {
    addToken(type, null);
}

private void addToken(TokenType type, Object literal) {
    String text = source.substring(start, current);
    tokens.add(new Token(type, text, literal, line, column - text.length()));
}
}