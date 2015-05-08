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

/**
 * Represents a field in a class.
 */
public abstract class ObjField {
    /**
     * The field's name.
     */
    private final String name;

    /**
     * Whether this field is static.
     */
    private final boolean isStatic;

    /**
     * Creates a new field.
     *
     * @param name     The field's name.
     * @param isStatic Whether this field is static.
     */
    public ObjField(String name, boolean isStatic) {
        this.name = name;
        this.isStatic = isStatic;
    }

    /**
     * Returns this field's name.
     */
    public String getName() {
        return name;
    }

    public abstract void init(Interpreter interpreter, Obj self);

    public abstract void set(Interpreter interpreter, Obj self, Obj newValue);

    public abstract Obj get(Interpreter interpreter, Obj self);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ObjField field = (ObjField) o;

        return name.equals(field.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public boolean isStatic() {
        return isStatic;
    }
}
