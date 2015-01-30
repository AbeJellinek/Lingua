package me.abje.zero.interpreter.obj;

import me.abje.zero.interpreter.InterpreterException;

import java.util.List;

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

    public static final ClassObj SYNTHETIC = ClassObj.builder("List").build();
}
