package me.abje.zero.interpreter.obj;

import me.abje.zero.interpreter.Interpreter;
import me.abje.zero.interpreter.InterpreterException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ListObj extends Obj {
    private List<Obj> items;

    public ListObj(List<Obj> items) {
        super(SYNTHETIC);
        this.items = items;
    }

    public List<Obj> all() {
        return items;
    }

    public Obj get(int i) {
        if (i >= 0 && i < items.size()) {
            return items.get(i);
        } else {
            throw new InterpreterException("list index out of bounds: " + i);
        }
    }

    public void set(int i, Obj value) {
        if (i >= 0 && i < items.size()) {
            items.set(i, value);
        } else {
            throw new InterpreterException("list index out of bounds: " + i);
        }
    }

    @Override
    public Obj getAtIndex(Obj index) {
        if (index instanceof NumberObj)
            return get((int) ((NumberObj) index).getValue());
        else
            throw new InterpreterException("list index not a number");
    }

    @Override
    public void setAtIndex(Obj index, Obj value) {
        if (index instanceof NumberObj)
            set((int) ((NumberObj) index).getValue(), value);
        else
            throw new InterpreterException("list index not a number");
    }

    public ListObj map(Interpreter interpreter, Obj function) {
        List<Obj> newList = items.stream().map(obj -> function.call(interpreter, Arrays.asList(obj))).
                collect(Collectors.toList());
        return new ListObj(newList);
    }

    public ListObj filter(Interpreter interpreter, Obj predicate) {
        List<Obj> newList = items.stream().filter(obj -> predicate.call(interpreter, Arrays.asList(obj)).isTruthy()).
                collect(Collectors.toList());
        return new ListObj(newList);
    }

    public ListObj reverse() {
        List<Obj> newList = new ArrayList<>(items);
        Collections.reverse(newList);
        return new ListObj(newList);
    }

    public ListObj add(Obj obj) {
        items.add(obj);
        return this;
    }

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

    public static final ClassObj SYNTHETIC = ClassObj.builder("List").
            withFunction("map", (interpreter, self, args) -> {
                if (args.size() != 1)
                    throw new InterpreterException("invalid number of arguments for map");
                return ((ListObj) self).map(interpreter, args.get(0));
            }).
            withFunction("filter", (interpreter, self, args) -> {
                if (args.size() != 1)
                    throw new InterpreterException("invalid number of arguments for filter");
                return ((ListObj) self).filter(interpreter, args.get(0));
            }).
            withFunction("reverse", (interpreter, self, args) -> {
                if (args.size() != 0)
                    throw new InterpreterException("too many arguments for reverse");
                return ((ListObj) self).reverse();
            }).
            withFunction("add", (interpreter, self, args) -> {
                if (args.size() != 1)
                    throw new InterpreterException("invalid number of arguments for reverse");
                return ((ListObj) self).add(args.get(0));
            }).build();
}
