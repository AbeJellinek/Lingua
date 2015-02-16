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
 * A variable assignment expression.
 */
public class AssignmentExpr extends Expr {
    /**
     * The name of the variable.
     */
    private String name;

    /**
     * The expression of the value to be assigned to this variable.
     */
    private Expr value;

    /**
     * Creates a new assignment expression.
     *
     * @param name  The name of the variable.
     * @param value The expression of the value to be assigned.
     */
    public AssignmentExpr(Token token, String name, Expr value) {
        super(token);
        this.name = name;
        this.value = value;
    }

    @Override
    public Obj evaluate(Interpreter interpreter) {
        Obj valueObj = interpreter.next(value);
        if (getAnnotations().contains("var")) {
            interpreter.getEnv().define(name, valueObj);
        } else {
            interpreter.getEnv().put(name, valueObj);
        }
        return valueObj;
    }

    @Override
    public Obj match(Interpreter interpreter, Environment.Frame frame, Obj obj) {
        Obj rightMatch = value.match(interpreter, frame, obj);
        if (rightMatch != null) {
            interpreter.getEnv().define(name, rightMatch);
            return rightMatch;
        } else {
            return null;
        }
    }

    /**
     * Returns the name of the variable.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the expression of the value to be assigned.
     */
    public Expr getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "ASSIGNMENT(" + name + ", " + value + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AssignmentExpr that = (AssignmentExpr) o;

        return name.equals(that.name) && value.equals(that.value);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }
}
