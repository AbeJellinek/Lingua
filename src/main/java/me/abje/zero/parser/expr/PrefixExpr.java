package me.abje.zero.parser.expr;

import me.abje.zero.interpreter.Interpreter;
import me.abje.zero.interpreter.InterpreterException;
import me.abje.zero.interpreter.obj.BooleanObj;
import me.abje.zero.interpreter.obj.NumberObj;
import me.abje.zero.interpreter.obj.Obj;
import me.abje.zero.lexer.Token;

public class PrefixExpr implements Expr {
    private Token.Type type;
    private Expr operand;

    public PrefixExpr(Token.Type type, Expr operand) {
        this.type = type;
        this.operand = operand;
    }

    public Token.Type getType() {
        return type;
    }

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
}
