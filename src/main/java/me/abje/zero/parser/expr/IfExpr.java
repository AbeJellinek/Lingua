package me.abje.zero.parser.expr;

import me.abje.zero.interpreter.Interpreter;
import me.abje.zero.interpreter.obj.Obj;

public class IfExpr extends Expr {
    private final Expr condition;
    private final Expr thenBranch;
    private final Expr elseBranch;

    public IfExpr(Expr condition, Expr thenBranch, Expr elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    public Expr getCondition() {
        return condition;
    }

    public Expr getThenBranch() {
        return thenBranch;
    }

    public Expr getElseBranch() {
        return elseBranch;
    }

    @Override
    public Obj evaluate(Interpreter interpreter) {
        if (interpreter.next(condition).isTruthy()) {
            return interpreter.next(thenBranch);
        } else {
            return interpreter.next(elseBranch);
        }
    }
}
