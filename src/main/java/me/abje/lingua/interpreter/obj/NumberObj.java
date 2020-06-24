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

import me.abje.lingua.interpreter.Bridge;
import me.abje.lingua.interpreter.Interpreter;
import me.abje.lingua.interpreter.InterpreterException;

import java.util.List;

/**
 * A Lingua number. Represented by a Java float.
 */
public class NumberObj extends Obj {
    public static final ClassObj SYNTHETIC = bridgeClass(NumberObj.class);
    private static final NumberObj[] CACHE = new NumberObj[256];

    static {
        for (int i = -128; i < 128; i++) {
            CACHE[i + 128] = new NumberObj(i);
        }
    }

    /**
     * This Number's value.
     */
    private final float value;

    /**
     * Creates a new Number with the given value.
     *
     * @param value The value.
     */
    private NumberObj(float value) {
        super(SYNTHETIC);
        this.value = value;
    }

    /**
     * Returns this Number's value.
     */
    public float getValue() {
        return value;
    }

    @Override
    public String toString() {
        if (value == (int) value)
            return String.valueOf((int) value);
        return String.valueOf(value);
    }

    @Override
    public Obj call(Interpreter interpreter, List<Obj> args) {
        if (args.size() != 1)
            throw new InterpreterException("CallException", "too many arguments for number application", interpreter);
        if (!(args.get(0) instanceof NumberObj))
            throw new InterpreterException("CallException", "argument for number application must be a number", interpreter);
        return new NumberObj(value * ((NumberObj) args.get(0)).getValue());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NumberObj numberObj = (NumberObj) o;

        return Float.compare(numberObj.value, value) == 0;

    }

    @Override
    public int hashCode() {
        return (value != +0.0f ? Float.floatToIntBits(value) : 0);
    }

    /**
     * Numbers are truthy depending on their values.
     *
     * @return Whether this Number is not 0.0.
     */
    @Override
    public boolean isTruthy() {
        return value != 0;
    }

    @Bridge
    public static NumberObj init(Obj source) {
        try {
            return new NumberObj(Float.parseFloat(source.toString()));
        } catch (NumberFormatException e) {
            throw new InterpreterException("ConversionException", "not a number: " + source);
        }
    }

    public static NumberObj of(float f) {
        int i = (int) f;
        if (f == i && i <= 127 && i >= -128)
            return CACHE[i + 128];
        return new NumberObj(f);
    }
}
