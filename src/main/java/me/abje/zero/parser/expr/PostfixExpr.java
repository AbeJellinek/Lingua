package me.abje.zero.parser.expr;

import me.abje.zero.interpreter.Interpreter;
import me.abje.zero.interpreter.InterpreterException;
import me.abje.zero.interpreter.obj.NumberObj;
import me.abje.zero.interpreter.obj.Obj;
import me.abje.zero.lexer.Token;

public class PostfixExpr implements Expr {
    private final Expr expr;
    private final Token.Type type;

    public PostfixExpr(Expr expr, Token.Type type) {
        this.expr = expr;
        this.type = type;
    }

    public Expr getExpr() {
        return expr;
    }

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
                } else {
                    while (current > 0) {
                        result *= current;
                        current -= 1;
                    }
                }
                return new NumberObj(result);
            default:
                throw new InterpreterException("invalid postfix operator");
        }
    }
}
