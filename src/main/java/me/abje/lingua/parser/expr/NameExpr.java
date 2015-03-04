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

package me.abje.lingua.parser.expr;

import me.abje.lingua.interpreter.Environment;
import me.abje.lingua.interpreter.Interpreter;
import me.abje.lingua.interpreter.obj.Obj;
import me.abje.lingua.lexer.Token;

/**
 * A name expression, in the form of <code>xyz</code>.
 */
public class NameExpr extends Expr {
    /**
     * The string value of this name.
     */
    private String value;

    /**
     * Creates a new name expression.
     *
     * @param value The string value of the name.
     */
    public NameExpr(Token token, String value) {
        super(token);
        this.value = value;
    }

    /**
     * Returns the string value of this name.
     */
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "NAME(" + value + ")";
    }

    @Override
    public Obj evaluate(Interpreter interpreter) {
        return interpreter.getEnv().get(value);
    }

    @Override
    public Obj match(Interpreter interpreter, Environment.Frame frame, Obj obj) {
        if (!value.equals("_")) {
            if (getAnnotations().contains("var")) {
                interpreter.getEnv().define(value, obj);
            } else {
                interpreter.getEnv().put(value, obj);
            }
        }
        return obj;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NameExpr nameExpr = (NameExpr) o;

        return value.equals(nameExpr.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
