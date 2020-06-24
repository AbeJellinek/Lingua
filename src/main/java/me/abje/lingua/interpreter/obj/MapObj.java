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

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class MapObj extends Obj {
    public static final ClassObj SYNTHETIC = bridgeClass(MapObj.class);

    private final Map<Obj, Obj> items;

    public MapObj(Map<Obj, Obj> items) {
        super(SYNTHETIC);
        this.items = items;
    }

    public Obj get(Obj key) {
        return items.getOrDefault(key, NullObj.NULL);
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

    @Bridge
    public boolean contains(Obj key) {
        return items.containsKey(key);
    }

    public Set<Obj> keys() {
        return items.keySet();
    }

    @Bridge
    public int size() {
        return items.size();
    }

    @Bridge
    public Obj forEach(Interpreter interpreter, Obj function) {
        for (Map.Entry<Obj, Obj> entry : items.entrySet()) {
            function.call(interpreter, Arrays.asList(entry.getKey(), entry.getValue()));
        }

        return NullObj.NULL;
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
