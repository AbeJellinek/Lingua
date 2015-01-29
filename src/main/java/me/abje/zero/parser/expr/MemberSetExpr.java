package me.abje.zero.parser.expr;

import me.abje.zero.interpreter.Interpreter;
import me.abje.zero.interpreter.obj.Obj;

public class MemberSetExpr implements Expr {
    private final Expr left;
    private final String name;
    private final Expr value;

    public MemberSetExpr(Expr left, String name, Expr value) {
        this.left = left;
        this.name = name;
        this.value = value;
    }

    @Override
    public Obj evaluate(Interpreter interpreter) {
        Obj leftObj = interpreter.next(left);
        Obj valueObj = interpreter.next(value);
        leftObj.setMember(name, valueObj);
        return valueObj;
    }

    public Expr getLeft() {
        return left;
    }

    public String getName() {
        return name;
    }

    public Expr getValue() {
        return value;
    }
}
