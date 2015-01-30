package me.abje.zero.parser.expr;

import me.abje.zero.interpreter.Interpreter;
import me.abje.zero.interpreter.obj.ClassObj;
import me.abje.zero.interpreter.obj.Field;
import me.abje.zero.interpreter.obj.FunctionObj;
import me.abje.zero.interpreter.obj.Obj;

import java.util.List;
import java.util.stream.Collectors;

public class ClassExpr implements Expr {
    private final String name;
    private final List<FunctionExpr> functions;
    private final List<Field> fields;

    public ClassExpr(String name, List<FunctionExpr> functions, List<Field> fields) {
        this.name = name;
        this.functions = functions;
        this.fields = fields;
    }

    @Override
    public Obj evaluate(Interpreter interpreter) {
        List<FunctionObj> functionObjs = functions.stream().
                map(expr -> (FunctionObj) interpreter.next(expr)).collect(Collectors.toList());
        ClassObj clazz = new ClassObj(name, functionObjs, fields);
        interpreter.getEnv().define(name, clazz);
        return clazz;
    }

    public String getName() {
        return name;
    }

    public List<FunctionExpr> getFunctions() {
        return functions;
    }

    public List<Field> getFields() {
        return fields;
    }
}
