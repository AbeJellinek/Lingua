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

package me.abje.zero.parser.expr;

import me.abje.zero.interpreter.Interpreter;
import me.abje.zero.interpreter.obj.NullObj;
import me.abje.zero.interpreter.obj.Obj;

/**
 * A while loop expression.
 */
public class WhileExpr extends Expr {
    /**
     * The loop condition.
     */
    private Expr condition;

    /**
     * The loop body.
     */
    private Expr body;

    /**
     * Creates a new while loop expression.
     * @param condition The loop condition.
     * @param body The loop body.
     */
    public WhileExpr(Expr condition, Expr body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public Obj evaluate(Interpreter interpreter) {
        while (interpreter.next(condition).isTruthy()) {
            interpreter.next(body);
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
}
