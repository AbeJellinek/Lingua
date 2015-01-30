package me.abje.zero.parser.expr;

import me.abje.zero.interpreter.Interpreter;
import me.abje.zero.interpreter.obj.ListObj;
import me.abje.zero.interpreter.obj.Obj;

import java.util.List;
import java.util.stream.Collectors;

public class ListExpr extends Expr {
    private List<Expr> items;

    public ListExpr(List<Expr> items) {
        this.items = items;
    }

    public List<Expr> getItems() {
        return items;
    }

    @Override
    public Obj evaluate(Interpreter interpreter) {
        List<Obj> itemObjs = items.stream().map(interpreter::next).collect(Collectors.toList());
        return new ListObj(itemObjs);
    }
}
