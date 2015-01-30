/*
 * Copyright (c) 2015 Abe Jellinek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

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
        TIMES, DIVIDE, POW, COMMA, EQ, OPEN_BRACE, CLOSE_BRACE, ARROW, LT, LTE, GT, GTE, TRUE, FALSE, EQEQ, NEQ, ANDAND, AND, OROR, OR, STRING, IF, ELSE, WHILE, DO, NULL, OPEN_BRACKET, CLOSE_BRACKET, CLASS, ANNOTATION, BANG
    }
}
