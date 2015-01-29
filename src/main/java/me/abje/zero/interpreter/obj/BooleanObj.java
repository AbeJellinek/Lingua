package me.abje.zero.interpreter.obj;

public class BooleanObj extends Obj {
    private boolean value;

    public BooleanObj(boolean value) {
        this.value = value;
    }

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

    @Override
    public boolean isTruthy() {
        return value;
    }
}
