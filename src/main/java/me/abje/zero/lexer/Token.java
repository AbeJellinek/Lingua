package me.abje.zero.lexer;

public class Token {
    private Type type;
    private String value;
    private int line, column;

    public Token(Type type, String value, int line, int column) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.column = column;
    }

    public Type getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return type + " " + value;
    }

    public boolean is(Type type) {
        return this.type == type;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public static enum Type {
        LINE,
        LINE_CONTINUATION,
        WHITESPACE,
        NAME,
        NUMBER,
        DOT,
        PLUS,
        MINUS,
        TILDE,
        OPEN_PAREN,
        CLOSE_PAREN,
        TIMES, DIVIDE, POW, COMMA, EQ, OPEN_BRACE, CLOSE_BRACE, ARROW, LT, LTE, GT, GTE, TRUE, FALSE, EQEQ, NEQ, ANDAND, AND, OROR, OR, STRING, IF, ELSE, WHILE, DO, NULL, OPEN_BRACKET, CLOSE_BRACKET, BANG
    }
}
