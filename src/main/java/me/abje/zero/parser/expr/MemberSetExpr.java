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
import me.abje.zero.interpreter.obj.Obj;

/**
 * A member set expression, in the form of <code>left.name = value</code>.
 */
public class MemberSetExpr extends Expr {
    /**
     * The left side of the expression -- the object which contains the member to be set.
     */
    private final Expr left;

    /**
     * The name of the member to be set.
     */
    private final String name;

    /**
     * The value to set the member to.
     */
    private final Expr value;

    /**
     * Creates a new member set expression.
     *
     * @param left  The left side of the expression.
     * @param name  The name of the member to be set.
     * @param value The value to set it to.
     */
    public MemberSetExpr(Expr left, String name, Expr value) {
        this.left = left;
        this.name = name;
        this.value = value;
    }

    @Override
    public Obj evaluate(Interpreter interpreter) {
        Obj leftObj = interpreter.next(left);
        Obj valueObj = interpreter.next(value);
        leftObj.setMember(name, valueObj);
        return valueObj;
    }

    /**
     * Returns the left side of the expression.
     */
    public Expr getLeft() {
        return left;
    }

    /**
     * Returns the name of the member to be set.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the value to set the member to.
     */
    public Expr getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MemberSetExpr that = (MemberSetExpr) o;

        return left.equals(that.left) && name.equals(that.name) && value.equals(that.value);
    }

    @Override
    public int hashCode() {
        int result = left.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }
}
