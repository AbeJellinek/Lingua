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

package me.abje.zero.interpreter.obj;

/**
 * An object with two possible values: true and false.
 */
public class BooleanObj extends Obj {
    /**
     * The value of this object.
     */
    private boolean value;

    /**
     * Creates a new boolean object with the given value.
     *
     * @param value The value.
     */
    public BooleanObj(boolean value) {
        super(SYNTHETIC);
        this.value = value;
    }

    /**
     * Returns this boolean's value.
     */
    public boolean getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BooleanObj that = (BooleanObj) o;

        return value == that.value;
    }

    @Override
    public int hashCode() {
        return (value ? 1 : 0);
    }

    /**
     * Booleans are truthy depending on their value.
     *
     * @return Whether this boolean is `true`.
     */
    @Override
    public boolean isTruthy() {
        return value;
    }

    public static final ClassObj SYNTHETIC = ClassObj.builder("Boolean").build();
}
