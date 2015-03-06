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

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class MapObj extends Obj {
    public static final ClassObj SYNTHETIC = ClassObj.<MapObj>builder("Map").
            withFunction("forEach", (interpreter, self, args) -> {
                if (args.size() != 1)
                    throw new InterpreterException("CallException", "invalid number of arguments for forEach", interpreter);
                return self.each(interpreter, args.get(0));
            }).
            withFunction("put", (interpreter, self, args) -> {
                if (args.size() != 2)
                    throw new InterpreterException("CallException", "invalid number of arguments for put", interpreter);
                return self.put(args.get(0), args.get(1));
            }).
            withFunction("size", (interpreter, self, args) -> {
                if (args.size() != 0)
                    throw new InterpreterException("CallException", "invalid number of arguments for size", interpreter);
                return new NumberObj(self.items.size());
            }).
            withFunction("contains", ((interpreter, self, args) -> {
                if (args.size() != 1)
                    throw new InterpreterException("CallException", "invalid number of arguments for contains", interpreter);
                return new BooleanObj(self.contains(args.get(0)));
            })).
            build();

    private final Map<Obj, Obj> items;

    public MapObj(Map<Obj, Obj> items) {
        super(SYNTHETIC);
        this.items = items;
    }

    public Obj get(Obj key) {
        return items.get(key);
    }

    @Override
    public Obj getAtIndex(Obj index) {
        return get(index);
    }

    @Override
    public void setAtIndex(Obj index, Obj value) {
        put(index, value);
    }

    public Obj put(Obj key, Obj value) {
        return items.put(key, value);
    }

    public boolean contains(Obj key) {
        return items.containsKey(key);
    }

    public Set<Obj> keys() {
        return items.keySet();
    }

    public Obj each(Interpreter interpreter, Obj function) {
        for (Map.Entry<Obj, Obj> entry : items.entrySet()) {
            function.call(interpreter, Arrays.asList(entry.getKey(), entry.getValue()));
        }

        return NullObj.get();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("#{ ");
        items.forEach((k, v) -> sb.append(k).append(": ").append(v).append(", "));
        sb.setLength(sb.length() - 2);
        sb.append(" }");
        return sb.toString();
    }
}
