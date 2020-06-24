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
 * An object member access expression, in the form of <code>left.name</code>.
 */
public class MemberAccessExpr extends Expr {
    /**
     * The left side of the expression -- the object which contains the member to be accessed.
     */
    private final Expr left;

    /**
     * The name of the member.
     */
    private final String name;

    /**
     * Whether this member access is a nullable access (returns null if left side is null).
     */
    private final boolean nullable;

    /**
     * Creates a new member access expression.
     *
     * @param left     The left side of the expression.
     * @param name     The name of the member.
     * @param nullable Whether this member access is a nullable access (returns null if left side is null).
     */
    public MemberAccessExpr(Token token, Expr left, String name, boolean nullable) {
        super(token);
        this.left = left;
        this.name = name;
        this.nullable = nullable;
    }

    @Override
    public Obj evaluate(Interpreter interpreter) {
        if (nullable) {
            Obj leftObj = interpreter.next(left);
            if (leftObj != NullObj.get()) {
                return leftObj.getMember(interpreter, name);
            } else {
                return NullObj.get();
            }
        } else {
            return interpreter.next(left).getMember(interpreter, name);
        }
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

    /**
     * Returns whether this member access is a nullable access (returns null if left side is null).
     */
    public boolean isNullable() {
        return nullable;
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

    public String getPath() {
        return left + "." + name;
    }

    @Override
    public String toString() {
        return getPath();
    }
}
