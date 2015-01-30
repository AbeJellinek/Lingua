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

/**
 * A Zero class. Classes have names, functions, and fields.
 */
public class ClassObj extends Obj {
    /**
     * This class's name.
     */
    private final String name;

    /**
     * A map of the functions provided by this class.
     * The keys are the names of the functions, and the values the functions themselves.
     */
    private final Map<String, Obj> functionMap;

    /**
     * The fields provided by this class.
     */
    private final List<Field> fields;

    /**
     * The fields provided by this class, in map form.
     * The keys are the names of the fields, and the values the fields themselves.
     */
    private final Map<String, Field> fieldMap = new HashMap<>();

    /**
     * Indicates whether this class is synthetic.
     * A synthetic class is a class defined in the JVM, not Zero.
     */
    private boolean synthetic = false;

    /**
     * Creates a new class.
     *
     * @param name      The class's name.
     * @param functions The functions provided by the class in map form.
     * @param fields    The class's fields.
     */
    public ClassObj(String name, Map<String, Obj> functions, List<Field> fields) {
        super(null);
        this.name = name;
        this.functionMap = functions;
        this.fields = fields;

        for (Field fn : fields) {
            fieldMap.put(fn.getName(), fn);
        }
    }

    /**
     * Creates a new class.
     *
     * @param name      The class's name.
     * @param functions The functions provided by the class.
     * @param fields    The class's fields.
     */
    public ClassObj(String name, List<FunctionObj> functions, List<Field> fields) {
        this(name, functions.stream().collect(Collectors.toMap(FunctionObj::getName, Function.identity())), fields);
    }

    /**
     * Returns this class's name.
     */
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

    /**
     * Returns this class's fields.
     */
    public Map<String, Field> getFieldMap() {
        return fieldMap;
    }

    /**
     * Returns this class's functions.
     */
    public Map<String, Obj> getFunctionMap() {
        return functionMap;
    }

    @Override
    public String toString() {
        return "class " + name;
    }

    /**
     * Creates a new class builder.
     * @param name The new class's name.
     * @return The Builder.
     */
    public static Builder builder(String name) {
        return new Builder(name);
    }

    /**
     * Builds a synthetic class.
     */
    public static class Builder {
        /**
         * The class's name.
         */
        private String name;

        /**
         * The class's functions.
         */
        private Map<String, Obj> functions = new HashMap<>();

        /**
         * The class's fields.
         */
        private List<Field> fields = new ArrayList<>();

        /**
         * Creates a new builder for a class with the given name.
         * @param name The name.
         */
        public Builder(String name) {
            this.name = name;
            this.functions.put("init", new Obj(FunctionObj.SYNTHETIC) {
                @Override
                public Obj call(Interpreter interpreter, List<Obj> args) {
                    throw new InterpreterException("cannot initialize instance of synthetic class");
                }
            });
        }

        /**
         * Defines a new function.
         * @param name The function's name.
         * @param body The function's body.
         * @return This builder, for chaining.
         */
        public Builder withFunction(String name, TriFunction<Interpreter, Obj, List<Obj>, Obj> body) {
            this.functions.put(name, new SyntheticFunctionObj() {
                @Override
                public Obj call(Interpreter interpreter, List<Obj> args) {
                    return body.apply(interpreter, getSelf(), args);
                }
            });
            return this;
        }

        /**
         * Adds fields to the class.
         * @param fields The fields to add.
         * @return This builder, for chaining.
         */
        public Builder withFields(List<Field> fields) {
            this.fields.addAll(fields);
            return this;
        }

        /**
         * Creates the class represented by this builder.
         * @return The new class.
         */
        public ClassObj build() {
            ClassObj clazz = new ClassObj(name, functions, fields);
            clazz.synthetic = true;
            return clazz;
        }
    }
}
