package me.abje.zero.parser.expr;

import me.abje.zero.interpreter.Interpreter;
import me.abje.zero.interpreter.obj.Obj;

public interface Expr {
    public Obj evaluate(Interpreter interpreter);
}
