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

/**
 * An index set expression, in the form of <code>target[index] = value</code>.
 */
public class IndexSetExpr extends Expr {
    /**
     * This expression's target.
     */
    private Expr target;

    /**
     * This expression's index.
     */
    private Expr index;

    /**
     * This expression's value.
     */
    private Expr value;

    /**
     * Creates a new index set expression.
     *
     * @param target The expression's target.
     * @param index  The expression's index.
     * @param value  The expression's value.
     */
    public IndexSetExpr(Token token, Expr target, Expr index, Expr value) {
        super(token);
        this.target = target;
        this.index = index;
        this.value = value;
    }

    @Override
    public Obj evaluate(Interpreter interpreter) {
        Obj targetObj = interpreter.next(target);
        Obj indexObj = interpreter.next(index);
        Obj valueObj = interpreter.next(value);
        targetObj.setAtIndex(indexObj, valueObj);
        return valueObj;
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

    /**
     * Returns this expression's value.
     */
    public Expr getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IndexSetExpr that = (IndexSetExpr) o;

        return index.equals(that.index) && target.equals(that.target) && value.equals(that.value);
    }

    @Override
    public int hashCode() {
        int result = target.hashCode();
        result = 31 * result + index.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return target + "[" + index + "] = " + value;
    }
}
