package me.abje.zero.parser.expr;

import me.abje.zero.interpreter.Interpreter;
import me.abje.zero.interpreter.obj.NullObj;
import me.abje.zero.interpreter.obj.Obj;

public class WhileExpr implements Expr {
    private Expr condition;
    private Expr body;

    public WhileExpr(Expr condition, Expr body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public Obj evaluate(Interpreter interpreter) {
        while (interpreter.next(condition).isTruthy()) {
            interpreter.next(body);
        }
        return NullObj.get();
    }

    public Expr getCondition() {
        return condition;
    }

    public Expr getBody() {
        return body;
    }
}
