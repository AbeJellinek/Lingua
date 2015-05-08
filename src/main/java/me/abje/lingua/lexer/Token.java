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

import java.util.Objects;

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
     * This token's file name.
     */
    private String file;

    /**
     * Creates a new token with the given parameters.
     *
     * @param type  The token's type.
     * @param value The token's value.
     * @param line  The token's line number.
     * @param file  The token's file name.
     */
    public Token(Type type, String value, int line, String file) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.file = file;
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

    public String getFile() {
        return file;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return Objects.equals(line, token.line) &&
                Objects.equals(type, token.type) &&
                Objects.equals(value, token.value) &&
                Objects.equals(file, token.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value, line, file);
    }

    /**
     * An enum of the possible token types.
     */
    public enum Type {
        LINE("end of line"), LINE_CONTINUATION("line continuation"), WHITESPACE("whitespace"), NAME("name"),
        NUMBER("number"), DOT("dot"), PLUS("+"), MINUS("-"), TILDE("~"), OPEN_PAREN("("), CLOSE_PAREN(")"), TIMES("*"),
        DIVIDE("/"), POW("^"), COMMA(","), EQ("="), OPEN_BRACE("{"), CLOSE_BRACE("}"), ARROW("->"), LT("<"), LTE("<="),
        GT(">"), GTE(">="), TRUE("boolean literal (true)"), FALSE("boolean literal (false)"), EQEQ("=="), NEQ("!="),
        ANDAND("&&"), AND("&"), OROR("||"), OR("|"), STRING("string literal"), IF("if"), ELSE("else"), WHILE("while"),
        DO("do"), NULL("null literal"), OPEN_BRACKET("["), CLOSE_BRACKET(")"), CLASS("class"), ANNOTATION("annotation"),
        PLUS_EQ("+="), MINUS_EQ("-="), TIMES_EQ("*="), DIVIDE_EQ("/="), PLUSPLUS("++"), MINUSMINUS("--"), IS("is"),
        IMPORT("import"), COLON("colon"), TRY("try"), CATCH("catch"), MATCH("match"), OPEN_MAP_BRACE("#{"), HASH("#"),
        OPEN_SET_BRACE("#["), CHAR("character literal"), TRIPLE_DOT("..."), DOUBLE_DOT(".."), BANG("!"),
        INTERRODOT("?."), QUESTION_MARK("?"), FAT_ARROW("=>"), VAR("var"), ELVIS("?:");

        private final String name;

        Type(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
