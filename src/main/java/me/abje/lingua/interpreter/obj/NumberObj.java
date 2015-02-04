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

import java.util.List;

/**
 * A Lingua number. Represented by a Java float.
 */
public class NumberObj extends Obj {
    public static final ClassObj SYNTHETIC = ClassObj.builder("Number").
            withFunction("init", (interpreter, self, args) -> {
                if (args.size() != 1)
                    throw new InterpreterException("wrong number of arguments for Number constructor");
                try {
                    return new NumberObj(Float.parseFloat(args.get(0).toString()));
                } catch (NumberFormatException e) {
                    throw new InterpreterException("not a number: " + args.get(0));
                }
            }).
            build();
    /**
     * This Number's value.
     */
    private float value;

    /**
     * Creates a new Number with the given value.
     *
     * @param value The value.
     */
    public NumberObj(float value) {
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
        return String.format("%.0f", value);
    }

    @Override
    public Obj call(Interpreter interpreter, List<Obj> args) {
        if (args.size() != 1)
            throw new InterpreterException("too many arguments for number application");
        if (!(args.get(0) instanceof NumberObj))
            throw new InterpreterException("argument for number application must be a number");
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
}
