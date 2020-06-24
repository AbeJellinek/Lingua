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

import com.google.common.base.Joiner;
import me.abje.lingua.interpreter.Bridge;
import me.abje.lingua.interpreter.Interpreter;
import me.abje.lingua.interpreter.InterpreterException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A Lingua list. Mutable, but designed with functional usage in mind.
 */
public class ListObj extends Obj {
    public static final ClassObj SYNTHETIC = bridgeClass(ListObj.class);

    /**
     * This list's items. Must be mutable for mutator methods to work.
     */
    private final List<Obj> items;

    /**
     * Creates a new list with the given items.
     *
     * @param items The items.
     */
    public ListObj(List<Obj> items) {
        super(SYNTHETIC);
        this.items = items;
    }

    /**
     * Returns this list's items.
     */
    public List<Obj> all() {
        return items;
    }

    /**
     * Gets an item in the list.
     *
     * @param i The index of the item.
     * @return The item.
     * @throws me.abje.lingua.interpreter.InterpreterException If the list index is out of bounds.
     */
    public Obj get(int i) {
        if (i >= 0 && i < items.size()) {
            return items.get(i);
        } else {
            throw new InterpreterException("OutOfBoundsException", "list index out of bounds: " + i);
        }
    }

    /**
     * Sets an item in the list.
     *
     * @param i     The index of the item.
     * @param value The new item.
     * @throws me.abje.lingua.interpreter.InterpreterException If the list index is out of bounds.
     */
    public void set(int i, Obj value) {
        if (i >= 0 && i < items.size()) {
            items.set(i, value);
        } else {
            throw new InterpreterException("OutOfBoundsException", "list index out of bounds: " + i);
        }
    }

    @Override
    public Obj getAtIndex(Obj index) {
        if (index instanceof NumberObj)
            return get((int) ((NumberObj) index).getValue());
        else
            throw new InterpreterException("CallException", "list index not a number");
    }

    @Override
    public void setAtIndex(Obj index, Obj value) {
        if (index instanceof NumberObj)
            set((int) ((NumberObj) index).getValue(), value);
        else
            throw new InterpreterException("CallException", "list index not a number");
    }

    /**
     * Maps this list by applying the given function to each item.
     * Returns a new list, and does not mutate the original.
     *
     * @param interpreter The Interpreter to run the function in.
     * @param function    The function to apply to each item.
     * @return The mapped list.
     */
    @Bridge
    public ListObj map(Interpreter interpreter, Obj function) {
        List<Obj> newList = items.stream().map(obj -> function.call(interpreter, Collections.singletonList(obj))).
                collect(Collectors.toList());
        return new ListObj(newList);
    }

    /**
     * Filters this list by applying the given predicate to each item.
     * Returns a new list, and does not mutate the original.
     *
     * @param interpreter The Interpreter to run the predicate in.
     * @param predicate   The predicate to apply to each item.
     * @return The filtered list.
     */
    @Bridge
    public ListObj filter(Interpreter interpreter, Obj predicate) {
        List<Obj> newList = items.stream().filter(obj -> predicate.call(interpreter, Collections.singletonList(obj)).isTruthy()).
                collect(Collectors.toList());
        return new ListObj(newList);
    }

    @Bridge
    public Obj forEach(Interpreter interpreter, Obj function) {
        for (Obj obj : items) {
            function.call(interpreter, Collections.singletonList(obj));
        }
        return NullObj.get();
    }

    /**
     * Reverses this list.
     * Returns a new list, and does not mutate the original.
     *
     * @return The reversed list.
     */
    @Bridge
    public ListObj reverse() {
        List<Obj> newList = new ArrayList<>(items);
        Collections.reverse(newList);
        return new ListObj(newList);
    }

    /**
     * Adds an item to the end of this list.
     *
     * @param obj The item.
     * @return This list.
     */
    @Bridge
    public ListObj add(Obj obj) {
        if (obj == this)
            throw new InterpreterException("InvalidOperationException", "attempt to add collection to itself");
        items.add(obj);
        return this;
    }

    /**
     * Adds an item to position n of this list.
     *
     * @param obj The item.
     * @param n The position.
     * @return This list.
     */
    @Bridge
    public ListObj add(Obj obj, NumberObj n) {
        if (obj == this)
            throw new InterpreterException("InvalidOperationException", "attempt to add collection to itself");
        items.add((int) n.getValue(), obj);
        return this;
    }

    @Bridge
    public Obj remove(NumberObj n) {
        return items.remove((int) n.getValue());
    }

    @Bridge
    public StringObj join() {
        return new StringObj(Joiner.on("").join(items));
    }

    @Bridge
    public StringObj join(Obj separator) {
        return new StringObj(Joiner.on(separator.toString()).join(items));
    }

    @Bridge
    public Obj head() {
        return items.get(0);
    }

    @Bridge
    public ListObj tail() {
        return new ListObj(items.subList(Math.min(1, items.size()), items.size()));
    }

    @Bridge
    public ListObj drop(int n) {
        return new ListObj(items.subList(Math.min(n, items.size()), items.size()));
    }

    @Bridge
    public ListObj dropRight(int n) {
        return new ListObj(items.subList(0, items.size() - Math.min(n, items.size())));
    }

    @Bridge
    public Obj foldLeft(Obj function, Obj start, Interpreter interpreter) {
        if (items.isEmpty())
            return start;
        return tail().foldLeft(function, function.call(interpreter, Arrays.asList(start, head())), interpreter);
    }

    @Bridge
    public Obj foldRight(Obj function, Obj start, Interpreter interpreter) {
        if (items.isEmpty())
            return start;
        return function.call(interpreter, Arrays.asList(head(), tail().foldRight(function, start, interpreter)));
    }

    /**
     * Returns the size of this list.
     */
    @Bridge
    public int size() {
        return items.size();
    }

    /**
     * Lists are truthy when non-empty.
     *
     * @return Whether this list is non-empty.
     */
    @Override
    public boolean isTruthy() {
        return !items.isEmpty();
    }

    @Override
    public String toString() {
        return items.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ListObj listObj = (ListObj) o;

        return items.equals(listObj.items);
    }

    @Override
    public int hashCode() {
        return items.hashCode();
    }
}
