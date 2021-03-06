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
import me.abje.lingua.interpreter.obj.*;
import me.abje.lingua.lexer.Token;

/**
 * A binary operator expression, in the form of <code>left + right</code>.
 */
public class OperatorExpr extends Expr {
    /**
     * The left expression.
     */
    private final Expr left;

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
    public OperatorExpr(Token token, Expr left, Expr right) {
        super(token);
        this.left = left;
        this.right = right;
    }

    /**
     * Creates a new operator expression.
     *
     * @param left  The left expression.
     * @param token The operator token type.
     * @param right The right expression.
     */
    public OperatorExpr(Token.Type token, Expr left, Expr right) {
        // FIXME: `token.getName()` may not work for everything
        super(new Token(token, token.getName(), 1, "<none>"));
        this.left = left;
        this.right = right;
    }

    /**
     * Returns the left expression.
     */
    public Expr getLeft() {
        return left;
    }

    /**
     * Returns the right expression.
     */
    public Expr getRight() {
        return right;
    }

    @Override
    public String toString() {
        return left + " " + getToken().getValue() + " " + right;
    }

    @Override
    public Obj evaluate(Interpreter interpreter) {
        try {
            switch (getToken().getType()) {
                case PLUS: {
                    Obj leftObj = interpreter.next(left);
                    Obj rightObj = interpreter.next(right);
                    if (leftObj instanceof NumberObj && rightObj instanceof NumberObj) {
                        return NumberObj.of(((NumberObj) leftObj).getValue() + ((NumberObj) rightObj).getValue());
                    } else {
                        return new StringObj(leftObj.toString() + rightObj.toString());
                    }
                }
                case MINUS:
                    return NumberObj.of(((NumberObj) next(left, interpreter)).getValue() -
                            ((NumberObj) next(right, interpreter)).getValue());
                case TIMES:
                    return NumberObj.of(((NumberObj) next(left, interpreter)).getValue() *
                            ((NumberObj) next(right, interpreter)).getValue());
                case DIVIDE:
                    return NumberObj.of(((NumberObj) next(left, interpreter)).getValue() /
                            ((NumberObj) next(right, interpreter)).getValue());
                case POW:
                    return NumberObj.of((float) Math.pow(((NumberObj) next(left, interpreter)).getValue(),
                            ((NumberObj) next(right, interpreter)).getValue()));
                case EQEQ:
                    return BooleanObj.of(interpreter.next(left).equals(interpreter.next(right)));
                case NEQ:
                    return BooleanObj.of(!interpreter.next(left).equals(interpreter.next(right)));
                case LT:
                    return BooleanObj.of(((NumberObj) next(left, interpreter)).getValue() <
                            ((NumberObj) next(right, interpreter)).getValue());
                case LTE:
                    return BooleanObj.of(((NumberObj) next(left, interpreter)).getValue() <=
                            ((NumberObj) next(right, interpreter)).getValue());
                case GT:
                    return BooleanObj.of(((NumberObj) next(left, interpreter)).getValue() >
                            ((NumberObj) next(right, interpreter)).getValue());
                case GTE:
                    return BooleanObj.of(((NumberObj) next(left, interpreter)).getValue() >=
                            ((NumberObj) next(right, interpreter)).getValue());
                case ANDAND:
                    return BooleanObj.of(interpreter.next(left).isTruthy() && interpreter.next(right).isTruthy());
                case OROR:
                    return BooleanObj.of(interpreter.next(left).isTruthy() || interpreter.next(right).isTruthy());
                case IS:
                    return BooleanObj.of(interpreter.next(left).getType().isSubclassOf(interpreter.next(right)));
                case ELVIS: {
                    Obj leftObj = interpreter.next(left);
                    if (leftObj != NullObj.NULL)
                        return leftObj;
                    else
                        return interpreter.next(right);
                }
                default:
                    throw new InterpreterException("InvalidOperationException", "invalid operator", interpreter);
            }
        } catch (ClassCastException e) {
            throw new InterpreterException("CallException", "invalid type", interpreter);
        }
    }

    private Obj next(Expr expr, Interpreter interpreter) {
        return interpreter.next(expr);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OperatorExpr that = (OperatorExpr) o;

        return left.equals(that.left) && right.equals(that.right) && getToken().equals(that.getToken());
    }

    @Override
    public int hashCode() {
        int result = left.hashCode();
        result = 31 * result + getToken().hashCode();
        result = 31 * result + right.hashCode();
        return result;
    }
}
