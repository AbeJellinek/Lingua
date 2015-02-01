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

import me.abje.lingua.interpreter.Interpreter;
import me.abje.lingua.interpreter.obj.ClassObj;
import me.abje.lingua.interpreter.obj.Field;
import me.abje.lingua.interpreter.obj.FunctionObj;
import me.abje.lingua.interpreter.obj.Obj;

import java.util.List;
import java.util.stream.Collectors;

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
     * Creates a new class declaration expression.
     * @param name The class's name.
     * @param functions The class's functions.
     * @param fields The class's fields.
     */
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
