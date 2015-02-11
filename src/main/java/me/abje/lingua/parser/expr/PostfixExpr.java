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
import me.abje.lingua.interpreter.InterpreterException;
import me.abje.lingua.interpreter.obj.NumberObj;
import me.abje.lingua.interpreter.obj.Obj;
import me.abje.lingua.lexer.Token;

/**
 * A postfix operator expression, in the form of <code>expr++</code>.
 */
public class PostfixExpr extends Expr {
    /**
     * The left-side expression operated on by this operator.
     */
    private final Expr expr;

    /**
     * The type of the operator.
     */
    private final Token.Type type;

    /**
     * Creates a new postfix expression.
     *
     * @param expr The left-side expression operated on by the operator.
     * @param type The type of the operator.
     */
    public PostfixExpr(Expr expr, Token.Type type) {
        this.expr = expr;
        this.type = type;
    }

    /**
     * Returns the left-side expression.
     */
    public Expr getExpr() {
        return expr;
    }

    /**
     * Returns the type of the operator.
     */
    public Token.Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return "POSTFIX(" + expr + ", " + type + ")";
    }

    @Override
    public Obj evaluate(Interpreter interpreter) {
        switch (type) {
            case BANG:
                NumberObj operand = (NumberObj) interpreter.next(expr);
                int current = (int) operand.getValue();
                int result = 1;
                if (current == 0) {
                    return new NumberObj(1);
                } else while (current > 0) {
                    result *= current;
                    current -= 1;
                }
                return new NumberObj(result);
            default:
                throw new InterpreterException("InvalidOperationException", "invalid postfix operator", interpreter);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PostfixExpr that = (PostfixExpr) o;

        return expr.equals(that.expr) && type == that.type;
    }

    @Override
    public int hashCode() {
        int result = expr.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }
}
