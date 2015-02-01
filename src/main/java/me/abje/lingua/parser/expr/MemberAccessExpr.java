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
 * An object member access expression, in the form of <code>left.name</code>.
 */
public class MemberAccessExpr extends Expr {
    /**
     * The left side of the expression -- the object which contains the member to be accessed.
     */
    private Expr left;

    /**
     * The name of the member.
     */
    private String name;

    /**
     * Creates a new member access expression.
     *
     * @param left The left side of the expression.
     * @param name The name of the member.
     */
    public MemberAccessExpr(Expr left, String name) {
        this.left = left;
        this.name = name;
    }

    @Override
    public Obj evaluate(Interpreter interpreter) {
        return interpreter.next(left).getMember(name);
    }

    /**
     * Returns the left side of the expression.
     */
    public Expr getLeft() {
        return left;
    }

    /**
     * Returns the name of the member.
     */
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MemberAccessExpr that = (MemberAccessExpr) o;

        return left.equals(that.left) && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        int result = left.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }
}
