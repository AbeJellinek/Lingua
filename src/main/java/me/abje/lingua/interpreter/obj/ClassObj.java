/*
 * Copyright (c) 2015 Abe Jellinek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package me.abje.lingua.interpreter.obj;

import me.abje.lingua.interpreter.FieldBridge;
import me.abje.lingua.interpreter.Interpreter;
import me.abje.lingua.interpreter.InterpreterException;
import me.abje.lingua.interpreter.Static;
import me.abje.lingua.util.TriFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A Lingua class. Classes have names, functions, and fields.
 */
public class ClassObj extends Obj {
    public static final ClassObj SYNTHETIC = bridgeClass(ClassObj.class);

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
     * The fields provided by this class, in map form.
     * The keys are the names of the fields, and the values the fields themselves.
     */
    private final Map<String, ObjField> fieldMap = new HashMap<>();

    /**
     * Indicates whether this class is synthetic.
     * A synthetic class is a class defined in the JVM, not Lingua.
     */
    private boolean synthetic = false;

    /**
     * This class's superclass.
     */
    private ClassObj superClass;

    /**
     * Creates a new class.
     *
     * @param name       The class's name.
     * @param functions  The functions provided by the class in map form.
     * @param fields     The class's fields.
     * @param superClass This class's superclass.
     */
    public ClassObj(String name, Map<String, Obj> functions, List<ObjField> fields, ClassObj superClass) {
        super(null);
        this.name = name;
        this.functionMap = functions;
        this.superClass = superClass;

        for (ObjField fn : fields) {
            fieldMap.put(fn.getName(), fn);
        }
    }

    /**
     * Creates a new class.
     *
     * @param name       The class's name.
     * @param functions  The functions provided by the class.
     * @param fields     The class's fields.
     * @param superClass This class's superclass.
     */
    public ClassObj(String name, List<FunctionObj> functions, List<ObjField> fields, List<ObjField> staticFields, ClassObj superClass) {
        this(name, functions.stream().collect(Collectors.toMap(FunctionObj::getName, Function.identity())),
                fields, superClass);
    }

    /**
     * Creates a new class builder.
     *
     * @param name The new class's name.
     * @return The Builder.
     */
    public static <O> Builder<O> builder(String name) {
        return new Builder<>(name);
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
            UserObj superInstance = new UserObj(superClass);
            {
                Obj current = superInstance;
                while (current.getType().superClass != null) {
                    current.setSuperInst(new UserObj(current.getType().superClass));
                    current = current.getSuperInst();
                }
            }
            UserObj instance = new UserObj(this);
            instance.setSuperInst(superInstance);

            interpreter.getEnv().pushFrame(name + ".<init>");
            superClass.fieldMap.forEach((name, field) -> field.init(interpreter, superInstance));
            fieldMap.forEach((name, field) -> field.init(interpreter, instance));
            if (functionMap.containsKey("init")) {
                Obj init = instance.getMember(interpreter, "init");
                init.setSuperInst(superInstance);
                if (init instanceof SyntheticFunctionObj) {
                    ((SyntheticFunctionObj) init).setSelf(instance);
                } else if (init instanceof FunctionObj) {
                    init = ((FunctionObj) init).withSelf(instance);
                }
                init.call(interpreter, args);
            }
            interpreter.getEnv().popFrame();

            return instance;
        }
    }

    @Override
    public Obj getMember(Interpreter interpreter, String name) {
        if (fieldMap.containsKey(name) && fieldMap.get(name).isStatic()) {
            return fieldMap.get(name).get(interpreter, this);
        } else if (superClass != null) {
            return superClass.getMember(interpreter, name);
        } else {
            throw new InterpreterException("UndefinedException", "unknown field: " + name);
        }
    }

    @Override
    public void setMember(Interpreter interpreter, String name, Obj value) {
        if (fieldMap.containsKey(name) && fieldMap.get(name).isStatic()) {
            fieldMap.get(name).set(interpreter, this, value);
        } else if (superClass != null) {
            superClass.setMember(interpreter, name, value);
        } else {
            throw new InterpreterException("UndefinedException", "unknown field: " + name);
        }
    }

    /**
     * Returns this class's fields.
     */
    public Map<String, ObjField> getFieldMap() {
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
        return name;
    }

    public boolean isSubclassOf(Obj other) {
        ClassObj clazz = this;
        while (clazz != null) {
            if (clazz.equals(other)) {
                return true;
            }
            clazz = clazz.superClass;
        }
        return false;
    }

    /**
     * Builds a synthetic class.
     */
    public static class Builder<O> {
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
        private List<ObjField> fields = new ArrayList<>();

        /**
         * Creates a new builder for a class with the given name.
         *
         * @param name The name.
         */
        public Builder(String name) {
            this.name = name;
            this.functions.put("init", new Obj(FunctionObj.SYNTHETIC) {
                @Override
                public Obj call(Interpreter interpreter, List<Obj> args) {
                    throw new InterpreterException("InvalidOperationException", "cannot initialize instance of synthetic class", interpreter);
                }
            });
        }

        /**
         * Defines a new function.
         *
         * @param name The function's name.
         * @param body The function's body.
         * @return This builder, for chaining.
         */
        public Builder<O> withFunction(String name, TriFunction<Interpreter, O, List<Obj>, Obj> body) {
            this.functions.put(name, new SyntheticFunctionObj() {
                @Override
                public Obj call(Interpreter interpreter, List<Obj> args) {
                    interpreter.getEnv().pushFrame(Builder.this.name + "." + name);
                    interpreter.getEnv().getStack().peek().setFileName("<native>");
                    @SuppressWarnings("unchecked")
                    Obj result = body.apply(interpreter, (O) getSelf(), args);
                    interpreter.getEnv().popFrame();
                    return result;
                }

                @Override
                public String toString() {
                    return "<function " + name + ">";
                }
            });
            return this;
        }

        /**
         * Adds fields to the class.
         *
         * @param fields The fields to add.
         * @return This builder, for chaining.
         */
        public Builder<O> withFields(List<ObjField> fields) {
            this.fields.addAll(fields);
            return this;
        }

        /**
         * Creates the class represented by this builder.
         *
         * @return The new class.
         */
        public ClassObj build() {
            ClassObj clazz = new ClassObj(name, functions, fields, Obj.SYNTHETIC);
            clazz.synthetic = true;
            return clazz;
        }
    }
}
