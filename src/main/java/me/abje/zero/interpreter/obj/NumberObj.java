package me.abje.zero.interpreter.obj;

import me.abje.zero.interpreter.Interpreter;
import me.abje.zero.interpreter.InterpreterException;

import java.util.List;

public class NumberObj extends Obj {
    private float value;

    public NumberObj(float value) {
        this.value = value;
    }

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

    @Override
    public boolean isTruthy() {
        return value != 0;
    }
}
