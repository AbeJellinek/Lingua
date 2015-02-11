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
import me.abje.lingua.interpreter.obj.NullObj;
import me.abje.lingua.interpreter.obj.Obj;
import me.abje.lingua.lexer.Token;

/**
 * A while loop expression.
 */
public class WhileExpr extends Expr {
    /**
     * The loop condition.
     */
    private final Expr condition;

    /**
     * The loop body.
     */
    private final Expr body;

    /**
     * Whether this loop is a do-while or while-do loop.
     */
    private final boolean doWhile;

    /**
     * Creates a new while loop expression.
     *
     * @param condition The loop condition.
     * @param body      The loop body.
     * @param doWhile   Whether the loop is a do-while or while-do loop.
     */
    public WhileExpr(Token token, Expr condition, Expr body, boolean doWhile) {
        super(token);
        this.condition = condition;
        this.body = body;
        this.doWhile = doWhile;
    }

    @Override
    public Obj evaluate(Interpreter interpreter) {
        if (doWhile) {
            do {
                interpreter.next(body);
            } while (interpreter.next(condition).isTruthy());
        } else {
            while (interpreter.next(condition).isTruthy()) {
                interpreter.next(body);
            }
        }
        return NullObj.get();
    }

    /**
     * Returns the loop condition.
     */
    public Expr getCondition() {
        return condition;
    }

    /**
     * Returns the loop body.
     */
    public Expr getBody() {
        return body;
    }

    /**
     * Returns whether this loop is a do-while or while-do loop.
     */
    public boolean isDoWhile() {
        return doWhile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WhileExpr whileExpr = (WhileExpr) o;

        return doWhile == whileExpr.doWhile && body.equals(whileExpr.body) && condition.equals(whileExpr.condition);
    }

    @Override
    public int hashCode() {
        int result = condition.hashCode();
        result = 31 * result + body.hashCode();
        result = 31 * result + (doWhile ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "WHILE(" + condition + ", " + body + ", " + doWhile + ")";
    }
}
