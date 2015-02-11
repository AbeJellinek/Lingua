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
import me.abje.lingua.interpreter.obj.Obj;
import me.abje.lingua.lexer.Token;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A function call expression.
 */
public class CallExpr extends Expr {
    /**
     * The left side of the expression (the function to be called).
     */
    private Expr func;

    /**
     * The arguments provided to the function.
     */
    private List<Expr> args;

    /**
     * Creates a new call expression.
     *
     * @param func The function to be called.
     * @param args The arguments provided.
     */
    public CallExpr(Token token, Expr func, List<Expr> args) {
        super(token);
        this.func = func;
        this.args = args;
    }

    @Override
    public Obj evaluate(Interpreter interpreter) {
        List<Obj> argObjs = args.stream().map(interpreter::next).collect(Collectors.toList());
        return interpreter.next(func).call(interpreter, argObjs);
    }

    /**
     * Returns the function to be called.
     */
    public Expr getFunc() {
        return func;
    }

    /**
     * Returns the arguments provided.
     */
    public List<Expr> getArgs() {
        return args;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CallExpr callExpr = (CallExpr) o;

        return args.equals(callExpr.args) && func.equals(callExpr.func);
    }

    @Override
    public int hashCode() {
        int result = func.hashCode();
        result = 31 * result + args.hashCode();
        return result;
    }
}
