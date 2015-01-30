package me.abje.zero.interpreter.obj;

import me.abje.zero.interpreter.Interpreter;
import me.abje.zero.interpreter.InterpreterException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Obj {
    private ClassObj type;
    private Map<String, Obj> members = new HashMap<>();

    public Obj(ClassObj type) {
        this.type = type;
    }

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
        if (type != null && type.getFunctionMap().containsKey(name)) {
            Obj function = type.getFunctionMap().get(name);
            if (function instanceof FunctionObj) {
                return ((FunctionObj) function).withSelf(this);
            } else if (function instanceof SyntheticFunctionObj) {
                ((SyntheticFunctionObj) function).setSelf(this);
                return function;
            } else {
                return function;
            }
        } else if (type != null && type.getFieldMap().containsKey(name)) {
            return members.getOrDefault(name, NullObj.get());
        } else {
            throw new InterpreterException("unknown field: " + name);
        }
    }

    public void setMember(String name, Obj value) {
        if (type != null && type.getFieldMap().containsKey(name)) {
            members.put(name, value);
        } else {
            throw new InterpreterException("unknown field: " + name);
        }
    }

    public ClassObj getType() {
        return type;
    }

    protected void setType(ClassObj type) {
        this.type = type;
    }
}
