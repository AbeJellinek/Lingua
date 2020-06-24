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

/**
 * An object with two possible values: true and false.
 */
public class BooleanObj extends Obj {
    public static final ClassObj SYNTHETIC = bridgeClass(BooleanObj.class);

    public static final BooleanObj TRUE = new BooleanObj(true);
    public static final BooleanObj FALSE = new BooleanObj(false);

    /**
     * The value of this object.
     */
    private final boolean value;

    /**
     * Creates a new boolean object with the given value.
     *
     * @param value The value.
     */
    private BooleanObj(boolean value) {
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

    /**
     * Returns a BooleanObj corresponding to the given value.
     *
     * @param b The value.
     * @return If {@literal b} is true, {@link BooleanObj#TRUE}, otherwise {@link BooleanObj#FALSE}.
     */
    public static BooleanObj of(boolean b) {
        return b ? TRUE : FALSE;
    }
}
