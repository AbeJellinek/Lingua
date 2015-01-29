package me.abje.zero.parser.expr;

import me.abje.zero.interpreter.Interpreter;
import me.abje.zero.interpreter.obj.Obj;

public class IndexSetExpr implements Expr {
    private Expr target;
    private Expr index;
    private Expr value;

    public IndexSetExpr(Expr target, Expr index, Expr value) {
        this.target = target;
        this.index = index;
        this.value = value;
    }

    @Override
    public Obj evaluate(Interpreter interpreter) {
        Obj targetObj = interpreter.next(target);
        Obj indexObj = interpreter.next(index);
        Obj valueObj = interpreter.next(value);
        targetObj.setAtIndex(indexObj, valueObj);
        return valueObj;
    }

    public Expr getTarget() {
        return target;
    }

    public Expr getIndex() {
        return index;
    }

    public Expr getValue() {
        return value;
    }
}
