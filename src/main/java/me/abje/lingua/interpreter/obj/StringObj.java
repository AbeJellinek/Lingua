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

import me.abje.lingua.interpreter.InterpreterException;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * A Lingua character string.
 */
public class StringObj extends Obj {
    public static final ClassObj SYNTHETIC = ClassObj.<StringObj>builder("String").
            withFunction("init", (interpreter, self, args) ->
                    new StringObj(args.stream().map(Object::toString).collect(Collectors.joining("")))).
            withFunction("split", (interpreter, self, args) -> {
                if (args.size() != 1)
                    throw new InterpreterException("CallException", "invalid number of arguments for split", interpreter);
                return new ListObj(Arrays.asList((String[]) self.toString().split(args.get(0).toString())).
                        stream().map(StringObj::new).collect(Collectors.toList()));
            }).
            withFunction("trim", (interpreter, self, args) -> {
                if (args.size() != 0)
                    throw new InterpreterException("CallException", "invalid number of arguments for trim", interpreter);
                return new StringObj(self.getValue().trim());
            }).
            withFunction("charAt", (interpreter, self, args) -> {
                if (args.size() != 1)
                    throw new InterpreterException("CallException", "invalid number of arguments for charAt", interpreter);
                return new CharObj(self.value.charAt((int) ((NumberObj) args.get(0)).getValue()));
            }).
            build();

    /**
     * This String's internal value.
     */
    private String value;

    /**
     * Creates a new String with the given value.
     *
     * @param value The value.
     */
    public StringObj(String value) {
        super(SYNTHETIC);
        this.value = value;
    }

    /**
     * Returns this String's value.
     */
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StringObj stringObj = (StringObj) o;

        return value.equals(stringObj.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
