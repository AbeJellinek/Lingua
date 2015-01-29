package me.abje.zero.interpreter.obj;

import me.abje.zero.interpreter.Interpreter;
import me.abje.zero.interpreter.InterpreterException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Obj {
    private Map<String, Obj> members = new HashMap<>();

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

    public Obj getMember(String name) {
        return members.getOrDefault(name, NullObj.get());
    }

    public void setMember(String name, Obj value) {
        members.put(name, value);
    }
}
