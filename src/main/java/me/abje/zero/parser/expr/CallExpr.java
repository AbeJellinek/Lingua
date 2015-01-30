package me.abje.zero.parser.expr;

import me.abje.zero.interpreter.Interpreter;
import me.abje.zero.interpreter.obj.Obj;

import java.util.List;
import java.util.stream.Collectors;

public class CallExpr extends Expr {
    private Expr func;
    private List<Expr> args;

    public CallExpr(Expr func, List<Expr> args) {
        this.func = func;
        this.args = args;
    }

    @Override
    public Obj evaluate(Interpreter interpreter) {
        List<Obj> argObjs = args.stream().map(arg -> arg.evaluate(interpreter)).collect(Collectors.toList());
        return func.evaluate(interpreter).call(interpreter, argObjs);
    }

    public Expr getFunc() {
        return func;
    }

    public List<Expr> getArgs() {
        return args;
    }
}
