package me.abje.zero.interpreter;

import me.abje.zero.interpreter.obj.NullObj;
import me.abje.zero.interpreter.obj.Obj;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class Intrinsics {
    private Environment.Frame env;

    public Intrinsics(Environment.Frame env) {
        this.env = env;
    }

    public void register() {
        addFunction("print", (interpreter, args) -> {
            System.out.println(args.stream().map(Object::toString).collect(Collectors.joining("")));
            return NullObj.get();
        });
    }

    private void addFunction(String name, BiFunction<Interpreter, List<Obj>, Obj> func) {
        env.define(name, new Obj() {
            @Override
            public Obj call(Interpreter interpreter, List<Obj> args) {
                return func.apply(interpreter, args);
            }
        });
    }
}
