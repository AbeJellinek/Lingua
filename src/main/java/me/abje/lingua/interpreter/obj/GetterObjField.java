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

import me.abje.lingua.interpreter.Interpreter;
import me.abje.lingua.interpreter.InterpreterException;
import me.abje.lingua.util.TriFunction;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class GetterObjField extends ObjField {
    private final BiFunction<Interpreter, Obj, Obj> getter;
    private final TriFunction<Interpreter, Obj, Obj, Obj> setter;

    /**
     * Creates a new field.
     *
     * @param methodName The field's name.
     * @param isStatic   Whether this field is static.
     * @param getter     The field's getter.
     * @param setter     The field's setter.
     */
    public GetterObjField(String methodName,
                          boolean isStatic,
                          BiFunction<Interpreter, Obj, Obj> getter,
                          TriFunction<Interpreter, Obj, Obj, Obj> setter) {
        super(methodName, isStatic);
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    public void init(Interpreter interpreter, Obj self) {
    }

    @Override
    public void set(Interpreter interpreter, Obj self, Obj newValue) {
        if (setter == null) {
            throw new InterpreterException("InvalidOperationException", "field not mutable: " + getName());
        } else {
            setter.apply(interpreter, self, newValue);
        }
    }

    @Override
    public Obj get(Interpreter interpreter, Obj self) {
        return getter.apply(interpreter, self);
    }
}
