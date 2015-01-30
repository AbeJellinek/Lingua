package me.abje.zero.parser.expr;

import me.abje.zero.interpreter.Interpreter;
import me.abje.zero.interpreter.obj.Obj;

public class MemberAccessExpr extends Expr {
    private Expr left;
    private String name;

    public MemberAccessExpr(Expr left, String name) {
        this.left = left;
        this.name = name;
    }

    @Override
    public Obj evaluate(Interpreter interpreter) {
        return interpreter.next(left).getMember(name);
    }

    public Expr getLeft() {
        return left;
    }

    public String getName() {
        return name;
    }
}
