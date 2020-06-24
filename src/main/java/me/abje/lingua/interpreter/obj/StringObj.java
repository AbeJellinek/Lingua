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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A Lingua character string.
 */
public class StringObj extends Obj {
    public static final ClassObj SYNTHETIC = bridgeClass(StringObj.class);

    /**
     * This String's internal value.
     */
    private final String value;

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

    @Bridge
    public static StringObj init(Obj obj) {
        return new StringObj(obj.toString());
    }

    @Bridge(anyLength = true)
    public static StringObj init(Obj... objs) {
        return new StringObj(Arrays.stream(objs).map(Obj::toString).collect(Collectors.joining()));
    }

    @Bridge
    public ListObj split(Obj delimiter) {
        ListObj list = new ListObj(new ArrayList<>());
        for (String s : value.split(delimiter.toString())) {
            list.add(new StringObj(s));
        }
        return list;
    }

    @Bridge
    public StringObj trim() {
        return new StringObj(value.trim());
    }

    @Bridge
    public CharObj charAt(NumberObj index) {
        // todo cache this
        return CharObj.of(value.charAt((int) index.getValue()));
    }

    @Bridge
    public ListObj chars() {
        char[] charArr = value.toCharArray();
        List<Obj> charList = new ArrayList<>();
        for (char c : charArr) {
            charList.add(CharObj.of(c));
        }
        return new ListObj(charList);
    }

    @Bridge
    public StringObj replace(StringObj from, StringObj to) {
        return new StringObj(value.replace(from.getValue(), to.getValue()));
    }

    @Bridge
    public NumberObj length() {
        return NumberObj.of(value.length());
    }
}
