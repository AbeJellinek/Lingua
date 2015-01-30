package me.abje.zero.parser.expr;

import me.abje.zero.interpreter.Interpreter;
import me.abje.zero.interpreter.obj.Obj;

public class AssignmentExpr implements Expr {
    private String name;
    private Expr value;

    public AssignmentExpr(String name, Expr value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public Obj evaluate(Interpreter interpreter) {
        Obj valueObj = value.evaluate(interpreter);
        interpreter.getEnv().put(name, valueObj);
        return valueObj;
    }

    public String getName() {
        return name;
    }

    public Expr getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "ASSIGNMENT(" + name + ", " + value + ")";
    }
}
