package me.abje.zero.interpreter.obj;

import me.abje.zero.interpreter.InterpreterException;

public class UserObj extends Obj {
    private ClassObj clazz;

    public UserObj(ClassObj clazz) {
        this.clazz = clazz;
    }

    public ClassObj getClazz() {
        return clazz;
    }

    @Override
    public Obj getMember(String name) {
        if (clazz.getFunctionMap().containsKey(name)) {
            return clazz.getFunctionMap().get(name).withSelf(this);
        } else if (clazz.getFieldMap().containsKey(name)) {
            return super.getMember(name);
        } else {
            throw new InterpreterException("unknown field: " + name);
        }
    }

    @Override
    public void setMember(String name, Obj value) {
        if (clazz.getFieldMap().containsKey(name)) {
            super.setMember(name, value);
        } else {
            throw new InterpreterException("unknown field: " + name);
        }
    }
}
