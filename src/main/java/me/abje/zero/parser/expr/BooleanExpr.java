package me.abje.zero.parser.expr;

import me.abje.zero.interpreter.Interpreter;
import me.abje.zero.interpreter.obj.BooleanObj;
import me.abje.zero.interpreter.obj.Obj;

public class BooleanExpr extends Expr {
    private boolean value;

    public BooleanExpr(boolean value) {
        this.value = value;
    }

    @Override
    public Obj evaluate(Interpreter interpreter) {
        return new BooleanObj(value);
    }

    public boolean getValue() {
        return value;
    }

}
