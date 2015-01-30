package me.abje.zero.parser.expr;

import me.abje.zero.interpreter.Interpreter;
import me.abje.zero.interpreter.obj.Obj;

import java.util.List;

public class BlockExpr extends Expr {
    private List<Expr> exprs;

    public BlockExpr(List<Expr> exprs) {
        this.exprs = exprs;
    }

    @Override
    public Obj evaluate(Interpreter interpreter) {
        interpreter.getEnv().pushFrame();
        Obj result = null;
        for (Expr expr : exprs) {
            result = expr.evaluate(interpreter);
        }
        interpreter.getEnv().popFrame();
        return result;
    }

    public List<Expr> getExprs() {
        return exprs;
    }
}
