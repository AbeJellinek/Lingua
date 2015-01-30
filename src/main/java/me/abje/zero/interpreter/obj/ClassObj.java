package me.abje.zero.interpreter.obj;

import me.abje.zero.interpreter.Interpreter;
import me.abje.zero.parser.expr.AssignmentExpr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassObj extends Obj {
    private final String name;
    private final List<FunctionObj> functions;
    private final List<AssignmentExpr> fields;
    private final Map<String, FunctionObj> functionMap = new HashMap<>();
    private final Map<String, AssignmentExpr> fieldMap = new HashMap<>();

    public ClassObj(String name, List<FunctionObj> functions, List<AssignmentExpr> fields) {
        this.name = name;
        this.functions = functions;
        this.fields = fields;

        for (FunctionObj fn : functions) {
            functionMap.put(fn.getName(), fn);
        }

        for (AssignmentExpr fn : fields) {
            fieldMap.put(fn.getName(), fn);
        }
    }

    public String getName() {
        return name;
    }

    public List<FunctionObj> getFunctions() {
        return functions;
    }

    @Override
    public Obj call(Interpreter interpreter, List<Obj> args) {
        UserObj instance = new UserObj(this);

        interpreter.getEnv().pushFrame();
        interpreter.getEnv().define("self", instance);
        fields.forEach(field -> instance.setMember(field.getName(), interpreter.next(field.getValue())));
        if (functionMap.containsKey("init")) {
            functionMap.get("init").call(interpreter, args);
        }
        interpreter.getEnv().popFrame();

        return instance;
    }

    public List<AssignmentExpr> getFields() {
        return fields;
    }

    public Map<String, AssignmentExpr> getFieldMap() {
        return fieldMap;
    }

    public Map<String, FunctionObj> getFunctionMap() {
        return functionMap;
    }
}
