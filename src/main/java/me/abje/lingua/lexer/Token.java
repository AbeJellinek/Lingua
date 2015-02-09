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

package me.abje.lingua.lexer;

/**
 * A source token. Has a type, value, and position.
 */
public class Token {
    /**
     * This token's type.
     */
    private Type type;

    /**
     * This token's value.
     */
    private String value;

    /**
     * This token's line number.
     */
    private int line;

    /**
     * This token's column number.
     */
    private int column;

    /**
     * Creates a new token with the given parameters.
     *
     * @param type   The token's type.
     * @param value  The token's value.
     * @param line   The token's line number.
     * @param column The token's column number.
     */
    public Token(Type type, String value, int line, int column) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.column = column;
    }

    /**
     * Returns this token's type.
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns this token's value.
     */
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return type + " " + value;
    }

    /**
     * Returns whether this token's type equals the given type.
     *
     * @param type The type to compare to
     * @return Whether this token's type equals the given type.
     */
    public boolean is(Type type) {
        return this.type == type;
    }

    /**
     * Returns this token's line number.
     */
    public int getLine() {
        return line;
    }

    /**
     * Returns this token's column number.
     */
    public int getColumn() {
        return column;
    }

    /**
     * An enum of the possible token types.
     */
    public static enum Type {
        LINE, LINE_CONTINUATION, WHITESPACE, NAME, NUMBER, DOT, PLUS, MINUS, TILDE, OPEN_PAREN, CLOSE_PAREN, TIMES,
        DIVIDE, POW, COMMA, EQ, OPEN_BRACE, CLOSE_BRACE, ARROW, LT, LTE, GT, GTE, TRUE, FALSE, EQEQ, NEQ, ANDAND, AND,
        OROR, OR, STRING, IF, ELSE, WHILE, DO, NULL, OPEN_BRACKET, CLOSE_BRACKET, CLASS, ANNOTATION, PLUS_EQ, MINUS_EQ, TIMES_EQ, DIVIDE_EQ, PLUSPLUS, MINUSMINUS, IS, BANG
    }
}
