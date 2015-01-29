package me.abje.zero.interpreter.obj;

import me.abje.zero.interpreter.Interpreter;
import me.abje.zero.interpreter.InterpreterException;

import java.util.List;

public class Obj {
    public Obj call(Interpreter interpreter, List<Obj> args) {
        throw new InterpreterException("object not callable");
    }

    public boolean isTruthy() {
        return true;
    }

    public Obj getAtIndex(Obj index) {
        throw new InterpreterException("object not indexable");
    }

    public void setAtIndex(Obj index, Obj value) {
        throw new InterpreterException("object not indexable");
    }
}
