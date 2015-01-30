package me.abje.zero.parser.expr;

import me.abje.zero.interpreter.Interpreter;
import me.abje.zero.interpreter.obj.Obj;

import java.util.ArrayList;
import java.util.List;

public abstract class Expr {
    private List<String> annotations = new ArrayList<>();

    public abstract Obj evaluate(Interpreter interpreter);

    public List<String> getAnnotations() {
        return annotations;
    }
}
