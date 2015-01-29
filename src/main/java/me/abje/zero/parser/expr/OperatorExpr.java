package me.abje.zero.parser.expr;

import me.abje.zero.interpreter.Interpreter;
import me.abje.zero.interpreter.InterpreterException;
import me.abje.zero.interpreter.obj.BooleanObj;
import me.abje.zero.interpreter.obj.NumberObj;
import me.abje.zero.interpreter.obj.Obj;
import me.abje.zero.interpreter.obj.StringObj;
import me.abje.zero.lexer.Token;

public class OperatorExpr implements Expr {
    private final Expr left;
    private final Token.Type token;
    private final Expr right;

    public OperatorExpr(Expr left, Token.Type token, Expr right) {
        this.left = left;
        this.token = token;
        this.right = right;
    }

    public Expr getLeft() {
        return left;
    }

    public Token.Type getToken() {
        return token;
    }

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
