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
import me.abje.zero.interpreter.obj.StringObj;
import me.abje.zero.lexer.Token;

/**
 * A binary operator expression, in the form of <code>left + right</code>.
 */
public class OperatorExpr extends Expr {
    /**
     * The left expression.
     */
    private final Expr left;

    /**
     * The operator token type.
     */
    private final Token.Type token;

    /**
     * The right expression.
     */
    private final Expr right;

    /**
     * Creates a new operator expression.
     *
     * @param left  The left expression.
     * @param token The operator token type.
     * @param right The right expression.
     */
    public OperatorExpr(Expr left, Token.Type token, Expr right) {
        this.left = left;
        this.token = token;
        this.right = right;
    }

    /**
     * Returns the left expression.
     */
    public Expr getLeft() {
        return left;
    }

    /**
     * Returns the operator token type.
     */
    public Token.Type getToken() {
        return token;
    }

    /**
     * Returns the right expression.
     */
    public Expr getRight() {
        return right;
    }

    @Override
    public String toString() {
        return "OPERATOR(" + left + ", " + token + ", " + right + ")";
    }

    @Override
    public Obj evaluate(Interpreter interpreter) {
        switch (token) {
            case PLUS:
                Obj leftObj = interpreter.next(left);
                Obj rightObj = interpreter.next(right);
                if (leftObj instanceof NumberObj && rightObj instanceof NumberObj) {
                    return new NumberObj(((NumberObj) leftObj).getValue() + ((NumberObj) rightObj).getValue());
                } else {
                    return new StringObj(leftObj.toString() + rightObj.toString());
                }
            case MINUS:
                return new NumberObj(as(left, NumberObj.class, interpreter).getValue() -
                        as(right, NumberObj.class, interpreter).getValue());
            case TIMES:
                return new NumberObj(as(left, NumberObj.class, interpreter).getValue() *
                        as(right, NumberObj.class, interpreter).getValue());
            case DIVIDE:
                return new NumberObj(as(left, NumberObj.class, interpreter).getValue() /
                        as(right, NumberObj.class, interpreter).getValue());
            case POW:
                return new NumberObj((float) Math.pow(as(left, NumberObj.class, interpreter).getValue(),
                        as(right, NumberObj.class, interpreter).getValue()));
            case EQEQ:
                return new BooleanObj(interpreter.next(left).equals(interpreter.next(right)));
            case NEQ:
                return new BooleanObj(!interpreter.next(left).equals(interpreter.next(right)));
            case LT:
                return new BooleanObj(as(left, NumberObj.class, interpreter).getValue() <
                        as(right, NumberObj.class, interpreter).getValue());
            case LTE:
                return new BooleanObj(as(left, NumberObj.class, interpreter).getValue() <=
                        as(right, NumberObj.class, interpreter).getValue());
            case GT:
                return new BooleanObj(as(left, NumberObj.class, interpreter).getValue() >
                        as(right, NumberObj.class, interpreter).getValue());
            case GTE:
                return new BooleanObj(as(left, NumberObj.class, interpreter).getValue() >=
                        as(right, NumberObj.class, interpreter).getValue());
            case ANDAND:
                return new BooleanObj(interpreter.next(left).isTruthy() && interpreter.next(right).isTruthy());
            case OROR:
                return new BooleanObj(interpreter.next(left).isTruthy() || interpreter.next(right).isTruthy());
            default:
                throw new InterpreterException("invalid operator");
        }
    }

    private <T extends Obj> T as(Expr expr, Class<T> clazz, Interpreter interpreter) {
        Obj obj = interpreter.next(expr);
        if (clazz.isInstance(obj)) {
            //noinspection unchecked
            return (T) obj;
        } else {
            throw new InterpreterException("invalid type");
        }
    }
}
