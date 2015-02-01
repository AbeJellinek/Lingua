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

/**
 * An index expression, in the form of <code>target[index]</code>.
 */
public class IndexExpr extends Expr {
    /**
     * This expression's target.
     */
    private Expr target;

    /**
     * This expression's index.
     */
    private Expr index;

    /**
     * Creates a new index expression.
     *
     * @param target The expression's target.
     * @param index  The expression's index.
     */
    public IndexExpr(Expr target, Expr index) {
        this.target = target;
        this.index = index;
    }

    @Override
    public Obj evaluate(Interpreter interpreter) {
        return interpreter.next(target).getAtIndex(interpreter.next(index));
    }

    /**
     * Returns this expression's target.
     */
    public Expr getTarget() {
        return target;
    }

    /**
     * Returns this expression's index.
     */
    public Expr getIndex() {
        return index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IndexExpr indexExpr = (IndexExpr) o;

        return index.equals(indexExpr.index) && target.equals(indexExpr.target);
    }

    @Override
    public int hashCode() {
        int result = target.hashCode();
        result = 31 * result + index.hashCode();
        return result;
    }
}
