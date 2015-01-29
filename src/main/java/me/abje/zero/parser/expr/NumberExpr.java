package me.abje.zero.parser.expr;

import me.abje.zero.interpreter.Interpreter;
import me.abje.zero.interpreter.obj.NumberObj;
import me.abje.zero.interpreter.obj.Obj;
import me.abje.zero.parser.ParseException;

public class NumberExpr implements Expr {
    private float value;

    public NumberExpr(String value) {
        try {
            this.value = Float.parseFloat(value);
        } catch (NumberFormatException e) {
            throw new ParseException("invalid number");
        }
    }

    public float getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("NUMBER(%.0f)", value);
    }

    @Override
    public Obj evaluate(Interpreter interpreter) {
        return new NumberObj(value);
    }
}
