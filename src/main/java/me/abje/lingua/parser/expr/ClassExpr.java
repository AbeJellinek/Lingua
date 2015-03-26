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

package me.abje.lingua.parser.expr;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import me.abje.lingua.interpreter.Interpreter;
import me.abje.lingua.interpreter.InterpreterException;
import me.abje.lingua.interpreter.obj.*;
import me.abje.lingua.lexer.Token;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class declaration expression.
 */
public class ClassExpr extends Expr {
    /**
     * The class's name.
     */
    private final String name;

    /**
     * The class's functions.
     */
    private final List<FunctionExpr> functions;

    /**
     * The class's fields.
     */
    private final List<Field> fields;

    /**
     * This class's superclass name.
     */
    private final String superClassName;

    /**
     * Creates a new class declaration expression.
     *
     * @param name           The class's name.
     * @param functions      The class's functions.
     * @param fields         The class's fields.
     * @param superClassName This class's superclass name.
     */
    public ClassExpr(Token token, String name, List<FunctionExpr> functions, List<Field> fields, String superClassName) {
        super(token);
        this.name = name;
        this.functions = functions;
        this.fields = fields;
        this.superClassName = superClassName;
    }

    @Override
    public Obj evaluate(Interpreter interpreter) {
        ClassObj superClass = (ClassObj) interpreter.getEnv().get(superClassName);
        ListMultimap<String, FunctionObj> objs = ArrayListMultimap.create();
        for (Expr expr : functions) {
            FunctionObj obj = (FunctionObj) interpreter.next(expr);
            objs.put(obj.getName(), obj);
        }

        Map<String, Obj> flattened = new HashMap<>();
        for (Map.Entry<String, Collection<FunctionObj>> entry : objs.asMap().entrySet()) {
            String name = entry.getKey();
            Collection<FunctionObj> possibilities = entry.getValue();
            flattened.put(name, new SyntheticFunctionObj(FunctionObj.SYNTHETIC) {
                @Override
                public Obj call(Interpreter interpreter, List<Obj> args) {
                    for (FunctionObj function : possibilities) {
                        Obj result;
                        interpreter.getEnv().pushFrame("<" + function.getName() + ":args>");
                        if (function.isApplicable(interpreter, args)) {
                            interpreter.getEnv().popFrame();
                            result = function.withSelf(getSelf()).withSuper(getSuperInst()).call(interpreter, args);
                            return result;
                        } else {
                            interpreter.getEnv().popFrame();
                        }
                    }

                    if (superClass.getFunctionMap().containsKey(name) && !name.equals("init")) {
                        return superClass.getMember(name).call(interpreter, args);
                    } else {
                        throw new InterpreterException("CallException", "invalid arguments for function " + name);
                    }
                }
            });
        }
        ClassObj clazz = new ClassObj(name, flattened, fields, superClass);
        interpreter.getEnv().define(name, clazz);
        return clazz;
    }

    /**
     * Returns this class's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns this class's functions.
     */
    public List<FunctionExpr> getFunctions() {
        return functions;
    }

    /**
     * Returns this class's fields.
     */
    public List<Field> getFields() {
        return fields;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClassExpr classExpr = (ClassExpr) o;

        return fields.equals(classExpr.fields) && functions.equals(classExpr.functions) && name.equals(classExpr.name);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + functions.hashCode();
        result = 31 * result + fields.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "CLASS(" + name + ", " + functions + ", " + fields + ")";
    }
}
