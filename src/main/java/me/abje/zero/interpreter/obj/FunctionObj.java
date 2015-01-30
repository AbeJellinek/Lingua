package me.abje.zero.interpreter.obj;

import me.abje.zero.interpreter.Environment;
import me.abje.zero.interpreter.Interpreter;
import me.abje.zero.interpreter.InterpreterException;
import me.abje.zero.interpreter.Intrinsics;
import me.abje.zero.parser.expr.Expr;

import java.util.List;

public class FunctionObj extends Obj {
    private String name;
    private List<String> argNames;
    private Expr body;
    private Obj self;

    public FunctionObj(String name, List<String> argNames, Expr body) {
        this(name, argNames, body, null);
    }

    public FunctionObj(String name, List<String> argNames, Expr body, Obj self) {
        super(SYNTHETIC);
        this.name = name;
        this.argNames = argNames;
        this.body = body;
        this.self = self;
    }

    public String getName() {
        return name;
    }

    public List<String> getArgNames() {
        return argNames;
    }

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

    public void setSelf(Obj self) {
        this.self = self;
    }

    public Obj getSelf() {
        return self;
    }

    public FunctionObj withSelf(Obj self) {
        return new FunctionObj(name, argNames, body, self);
    }

    public static final ClassObj SYNTHETIC = ClassObj.builder("Function").build();
    static {
        Intrinsics.registerClass(SYNTHETIC);
    }
}
