package me.abje.zero.parser.expr;

import me.abje.zero.interpreter.Interpreter;
import me.abje.zero.interpreter.obj.FunctionObj;
import me.abje.zero.interpreter.obj.Obj;

import java.util.List;
import java.util.stream.Collectors;

public class FunctionExpr implements Expr {
    private String name;
    private List<String> argNames;
    private Expr body;

    public FunctionExpr(String name, List<String> argNames, Expr body) {
        this.name = name;
        this.argNames = argNames;
        this.body = body;
    }

    @Override
    public Obj evaluate(Interpreter interpreter) {
        Obj functionObj = new FunctionObj(name, argNames, body);
        interpreter.getEnv().put(name, functionObj);
        return functionObj;
    }

    @Override
    public String toString() {
        return "FUNCTION(" + name + ", " + argNames.stream().collect(Collectors.joining(", ", "(", ")")) +
                ", " + body + ")";
    }

}
