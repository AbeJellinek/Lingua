package me.abje.zero.interpreter.obj;

import me.abje.zero.interpreter.Environment;
import me.abje.zero.interpreter.Interpreter;
import me.abje.zero.interpreter.InterpreterException;
import me.abje.zero.parser.expr.Expr;

import java.util.List;

/**
 * A Zero function. Functions can be called, have a body and take arguments.
 */
public class FunctionObj extends Obj {
    /**
     * This function's name.
     */
    private String name;

    /**
     * This function's formal argument list.
     */
    private List<String> argNames;

    /**
     * This function's body expression.
     */
    private Expr body;

    /**
     * The "self" implicit argument passed to this function.
     */
    private Obj self;

    /**
     * Creates a new function.
     *
     * @param name     The function's name.
     * @param argNames The function's formal argument list.
     * @param body     The function's body expression.
     */
    public FunctionObj(String name, List<String> argNames, Expr body) {
        this(name, argNames, body, null);
    }

    /**
     * Creates a new function.
     *
     * @param name     The function's name.
     * @param argNames The function's formal argument list.
     * @param body     The function's body expression.
     * @param self     The function's "self" implicit argument.
     */
    public FunctionObj(String name, List<String> argNames, Expr body, Obj self) {
        super(SYNTHETIC);
        this.name = name;
        this.argNames = argNames;
        this.body = body;
        this.self = self;
    }

    /**
     * Returns this function's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns this function's argument names.
     */
    public List<String> getArgNames() {
        return argNames;
    }

    /**
     * Returns this function's body.
     */
    public Expr getBody() {
        return body;
    }

    @Override
    public Obj call(Interpreter interpreter, List<Obj> args) {
        if (args.size() != argNames.size())
            throw new InterpreterException("invalid number of arguments for function " + name);

        Environment env = interpreter.getEnv();
        env.pushFrame();
        if (self != null)
            env.define("self", self);
        for (int i = 0; i < args.size(); i++) {
            env.define(argNames.get(i), args.get(i));
        }
        Obj obj = body.evaluate(interpreter);
        env.popFrame();
        return obj;
    }

    @Override
    public String toString() {
        return "<function " + name + ">";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FunctionObj that = (FunctionObj) o;

        return argNames.equals(that.argNames) && body.equals(that.body) && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + argNames.hashCode();
        result = 31 * result + body.hashCode();
        return result;
    }

    /**
     * Sets this function's "self" implicit argument.
     * @param self The new value.
     */
    public void setSelf(Obj self) {
        this.self = self;
    }

    /**
     * Returns this function's "self" implicit argument.
     */
    public Obj getSelf() {
        return self;
    }

    /**
     * Returns a copy of this function with an updated {@link #self}.
     * @param self The new value of <code>self</code>.
     */
    public FunctionObj withSelf(Obj self) {
        return new FunctionObj(name, argNames, body, self);
    }

    public static final ClassObj SYNTHETIC = ClassObj.builder("Function").build();
}
