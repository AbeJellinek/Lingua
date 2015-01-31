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
 * An if[-else] expression.
 */
public class IfExpr extends Expr {
    /**
     * The condition expression.
     */
    private final Expr condition;

    /**
     * The "then" branch expression.
     */
    private final Expr thenBranch;

    /**
     * The "else" branch expression. Can be null.
     */
    private final Expr elseBranch;

    /**
     * Creates a new if expression.
     *
     * @param condition  The condition expression.
     * @param thenBranch The "then" branch expression.
     * @param elseBranch The "else" branch expression.
     */
    public IfExpr(Expr condition, Expr thenBranch, Expr elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    /**
     * Returns this if's condition.
     */
    public Expr getCondition() {
        return condition;
    }

    /**
     * Returns this if's "then" branch.
     */
    public Expr getThenBranch() {
        return thenBranch;
    }

    /**
     * Returns this if's "else" branch. Can be null.
     */
    public Expr getElseBranch() {
        return elseBranch;
    }

    @Override
    public Obj evaluate(Interpreter interpreter) {
        if (interpreter.next(condition).isTruthy()) {
            return interpreter.next(thenBranch);
        } else {
            if (elseBranch != null) {
                return interpreter.next(elseBranch);
            } else {
                return NullObj.get();
            }
        }
    }
}
