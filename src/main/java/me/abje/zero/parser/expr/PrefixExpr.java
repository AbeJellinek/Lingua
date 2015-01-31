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
import me.abje.zero.interpreter.InterpreterException;
import me.abje.zero.interpreter.obj.BooleanObj;
import me.abje.zero.interpreter.obj.NumberObj;
import me.abje.zero.interpreter.obj.Obj;
import me.abje.zero.lexer.Token;

/**
 * A prefix operator expression, in the form of <code>++operand</code>.
 */
public class PrefixExpr extends Expr {
    /**
     * The type of the operator.
     */
    private Token.Type type;

    /**
     * The right-side expression operated on by this operator.
     */
    private Expr operand;

    /**
     * Creates a new prefix operator expression.
     *
     * @param type    The type of the operator.
     * @param operand The right-side expression.
     */
    public PrefixExpr(Token.Type type, Expr operand) {
        this.type = type;
        this.operand = operand;
    }

    /**
     * Returns the type of the operator.
     */
    public Token.Type getType() {
        return type;
    }

    /**
     * Returns the right-side expression.
     */
    public Expr getOperand() {
        return operand;
    }

    @Override
    public String toString() {
        return "PREFIX(" + type + ", " + operand + ")";
    }

    @Override
    public Obj evaluate(Interpreter interpreter) {
        Obj obj = interpreter.next(operand);
        switch (type) {
            case MINUS:
                if (obj instanceof NumberObj) {
                    return new NumberObj(-((NumberObj) obj).getValue());
                } else {
                    throw new InterpreterException("operand is not a number");
                }
            case PLUS:
                if (obj instanceof NumberObj) {
                    return obj;
                } else {
                    throw new InterpreterException("operand is not a number");
                }
            case BANG:
                return new BooleanObj(obj.isTruthy());
            default:
                throw new InterpreterException("invalid prefix operator");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PrefixExpr that = (PrefixExpr) o;

        return operand.equals(that.operand) && type == that.type;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + operand.hashCode();
        return result;
    }
}
