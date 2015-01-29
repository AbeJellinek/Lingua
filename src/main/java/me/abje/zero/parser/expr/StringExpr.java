package me.abje.zero.parser.expr;

import me.abje.zero.interpreter.Interpreter;
import me.abje.zero.interpreter.obj.Obj;
import me.abje.zero.interpreter.obj.StringObj;

public class StringExpr implements Expr {
    private String value;

    public StringExpr(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public Obj evaluate(Interpreter interpreter) {
        return new StringObj(value);
    }
}
