package me.abje.zero.interpreter.obj;

import me.abje.zero.interpreter.Interpreter;
import me.abje.zero.interpreter.InterpreterException;
import me.abje.zero.util.TriFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ClassObj extends Obj {
    private final String name;
    private final Map<String, Obj> functionMap;
    private final List<Field> fields;
    private final Map<String, Field> fieldMap = new HashMap<>();
    private boolean synthetic = false;

    public ClassObj(String name, Map<String, Obj> functions, List<Field> fields) {
        super(null);
        this.name = name;
        this.functionMap = functions;
        this.fields = fields;

        for (Field fn : fields) {
            fieldMap.put(fn.getName(), fn);
        }
    }

    public ClassObj(String name, List<FunctionObj> functions, List<Field> fields) {
        this(name, functions.stream().collect(Collectors.toMap(FunctionObj::getName, Function.identity())), fields);
    }

    public String getName() {
        return name;
    }

    @Override
    public Obj call(Interpreter interpreter, List<Obj> args) {
        if (synthetic) {
            return functionMap.get("init").call(interpreter, args);
        } else {
            UserObj instance = new UserObj(this);

            interpreter.getEnv().pushFrame();
            interpreter.getEnv().define("self", instance);
            fieldMap.forEach((name, field) -> instance.setMember(name, interpreter.next(field.getDefaultValue())));
            if (functionMap.containsKey("init")) {
                functionMap.get("init").call(interpreter, args);
            }
            interpreter.getEnv().popFrame();

            return instance;
        }
    }

    public Map<String, Field> getFieldMap() {
        return fieldMap;
    }

    public List<Field> getFields() {
        return fields;
    }

    public Map<String, Obj> getFunctionMap() {
        return functionMap;
    }

    @Override
    public String toString() {
        return "class " + name;
    }

    public static Builder builder(String name) {
        return new Builder(name);
    }

    public static class Builder {
        private String name;
        private Map<String, Obj> functions = new HashMap<>();
        private List<Field> fields = new ArrayList<>();

        public Builder(String name) {
            this.name = name;
            this.functions.put("init", new Obj(FunctionObj.SYNTHETIC) {
                @Override
                public Obj call(Interpreter interpreter, List<Obj> args) {
                    throw new InterpreterException("cannot initialize instance of synthetic class");
                }
            });
        }

        public Builder withFunction(String name, TriFunction<Interpreter, Obj, List<Obj>, Obj> body) {
            this.functions.put(name, new SyntheticFunctionObj() {
                @Override
                public Obj call(Interpreter interpreter, List<Obj> args) {
                    return body.apply(interpreter, getSelf(), args);
                }
            });
            return this;
        }

        public Builder withFields(List<Field> fields) {
            this.fields.addAll(fields);
            return this;
        }

        public ClassObj build() {
            ClassObj clazz = new ClassObj(name, functions, fields);
            clazz.synthetic = true;
            return clazz;
        }
    }
}
