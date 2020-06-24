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
import me.abje.lingua.parser.expr.Expr;

import java.util.Objects;

public class DefaultObjField extends ObjField {
    /**
     * The field's default value, computed when an instance is created.
     */
    private final Expr defaultValue;

    /**
     * Creates a new field.
     *
     * @param name         The field's name.
     * @param isStatic     Whether this field is static.
     * @param defaultValue The field's uncomputed default value.
     */
    public DefaultObjField(String name, boolean isStatic, Expr defaultValue) {
        super(name, isStatic);
        this.defaultValue = defaultValue;
    }

    /**
     * Returns this field's default value.
     */
    public Expr getDefaultValue() {
        return defaultValue;
    }

    @Override
    public void init(Interpreter interpreter, Obj self) {
        self.setMember(interpreter, getName(), interpreter.next(defaultValue));
    }

    @Override
    public void set(Interpreter interpreter, Obj self, Obj newValue) {
        self.getMembers().put(getName(), newValue);
    }

    @Override
    public Obj get(Interpreter interpreter, Obj self) {
        return self.getMembers().getOrDefault(getName(), NullObj.NULL);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        DefaultObjField that = (DefaultObjField) o;

        return Objects.equals(defaultValue, that.defaultValue);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (defaultValue != null ? defaultValue.hashCode() : 0);
        return result;
    }
}
