package me.abje.zero.parser.expr;

import me.abje.zero.interpreter.Interpreter;
import me.abje.zero.interpreter.obj.Obj;

public class IndexExpr extends Expr {
    private Expr target;
    private Expr index;

    public IndexExpr(Expr target, Expr index) {
        this.target = target;
        this.index = index;
    }

    @Override
    public Obj evaluate(Interpreter interpreter) {
        return interpreter.next(target).getAtIndex(interpreter.next(index));
    }

    public Expr getTarget() {
        return target;
    }

    public Expr getIndex() {
        return index;
    }
}
