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

import me.abje.lingua.interpreter.Interpreter;
import me.abje.lingua.interpreter.obj.FunctionObj;
import me.abje.lingua.interpreter.obj.Obj;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A named function declaration expression.
 */
public class FunctionExpr extends Expr {
    /**
     * This function's name.
     */
    private String name;

    /**
     * This function's formal argument list.
     */
    private List<String> argNames;

    /**
     * This function's body.
     */
    private Expr body;

    /**
     * Creates a new function declaration expression.
     *
     * @param name     The function's name.
     * @param argNames The function's argument names.
     * @param body     The function's body.
     */
    public FunctionExpr(String name, List<String> argNames, Expr body) {
        this.name = name;
        this.argNames = argNames;
        this.body = body;
    }

    @Override
    public Obj evaluate(Interpreter interpreter) {
        Obj functionObj = new FunctionObj(name, argNames, body);
        interpreter.getEnv().put(name, functionObj);
        return functionObj;
    }

    @Override
    public String toString() {
        return "FUNCTION(" + name + ", " + argNames.stream().collect(Collectors.joining(", ", "(", ")")) +
                ", " + body + ")";
    }

    /**
     * Returns this function's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns this function's argument names.
     */
    public List<String> getArgNames() {
        return argNames;
    }

    /**
     * Returns this function's body.
     */
    public Expr getBody() {
        return body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FunctionExpr that = (FunctionExpr) o;

        return argNames.equals(that.argNames) && body.equals(that.body) && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + argNames.hashCode();
        result = 31 * result + body.hashCode();
        return result;
    }
}
