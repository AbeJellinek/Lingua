package me.abje.zero.interpreter.obj;

import me.abje.zero.interpreter.Interpreter;
import me.abje.zero.interpreter.InterpreterException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The base class for Zero objects. Objects have types and members.
 */
public class Obj {
    /**
     * This object's type. Can be null if this object is a {@link me.abje.zero.interpreter.obj.ClassObj}.
     */
    private ClassObj type;

    /**
     * This object's members.
     * Keys are names of members, and values are the members' values.
     */
    private Map<String, Obj> members = new HashMap<>();

    /**
     * Creates a new object with the given type.
     *
     * @param type The type. Can be null if this object is a type itself.
     */
    public Obj(ClassObj type) {
        this.type = type;
    }

    /**
     * Invokes this object with the given arguments. Unless overridden, simply throws an exception.
     *
     * @param interpreter The Interpreter that it is being invoked in.
     * @param args        The arguments passed by the caller.
     * @return The result of invoking this object.
     */
    public Obj call(Interpreter interpreter, List<Obj> args) {
        throw new InterpreterException("object not callable");
    }

    /**
     * Objects are by default truthy.
     *
     * @return True.
     */
    public boolean isTruthy() {
        return true;
    }

    /**
     * Returns the object at the given index inside this object.
     *
     * @param index The index.
     * @return The object at that index.
     * @throws me.abje.zero.interpreter.InterpreterException If this object is not indexable.
     */
    public Obj getAtIndex(Obj index) {
        throw new InterpreterException("object not indexable");
    }

    /**
     * Sets the object at the given index inside this object.
     *
     * @param index The index.
     * @param value The object to set at that index.
     * @throws me.abje.zero.interpreter.InterpreterException If this object is not indexable.
     */
    public void setAtIndex(Obj index, Obj value) {
        throw new InterpreterException("object not indexable");
    }

    /**
     * Gets a member of this object by name.
     *
     * @param name The member's name.
     * @return The member, never null.
     * @throws me.abje.zero.interpreter.InterpreterException If a member by that name could not be found.
     */
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

    /**
     * Sets a member of this object by name.
     *
     * @param name  The member's name.
     * @param value The new value of the member.
     * @throws me.abje.zero.interpreter.InterpreterException If a member by that name could not be found.
     */
    public void setMember(String name, Obj value) {
        if (type != null && type.getFieldMap().containsKey(name)) {
            members.put(name, value);
        } else {
            throw new InterpreterException("unknown field: " + name);
        }
    }

    /**
     * Gets this object's type.
     */
    public ClassObj getType() {
        return type;
    }

    /**
     * Sets this object's type.
     *
     * @param type The new type of this object.
     */
    protected void setType(ClassObj type) {
        this.type = type;
    }
}
