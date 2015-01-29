package me.abje.zero.parser.expr;

import me.abje.zero.interpreter.Interpreter;
import me.abje.zero.interpreter.obj.Obj;

public class NameExpr implements Expr {
    private String value;

    public NameExpr(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "NAME(" + value + ")";
    }

    @Override
    public Obj evaluate(Interpreter interpreter) {
        return interpreter.getEnv().get(value);
    }
}
