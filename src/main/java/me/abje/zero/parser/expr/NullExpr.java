package me.abje.zero.parser.expr;

import me.abje.zero.interpreter.Interpreter;
import me.abje.zero.interpreter.obj.Obj;
import me.abje.zero.interpreter.obj.NullObj;

public class NullExpr implements Expr {
    @Override
    public Obj evaluate(Interpreter interpreter) {
        return NullObj.get();
    }
}
