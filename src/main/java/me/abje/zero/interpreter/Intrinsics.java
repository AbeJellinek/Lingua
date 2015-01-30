package me.abje.zero.interpreter;

import me.abje.zero.interpreter.obj.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class Intrinsics {
    private Environment.Frame env;
    private static Map<String, ClassObj> classes = new HashMap<>();

    public Intrinsics(Environment.Frame env) {
        this.env = env;
    }

    public void register() {
        classes.forEach(env::define);

        addFunction("print", (interpreter, args) -> {
            System.out.println(args.stream().map(Object::toString).collect(Collectors.joining("")));
            return NullObj.get();
        });

        addFunction("classOf", (interpreter, args) -> {
            if (args.size() == 1) {
                return args.get(0).getType();
            } else {
                throw new InterpreterException("wrong number of arguments for classOf");
            }
        });
    }

    private void addFunction(String name, BiFunction<Interpreter, List<Obj>, Obj> func) {
        env.define(name, new Obj(FunctionObj.SYNTHETIC) {
            @Override
            public Obj call(Interpreter interpreter, List<Obj> args) {
                return func.apply(interpreter, args);
            }
        });
    }

    public static void registerClass(ClassObj clazz) {
        classes.put(clazz.getName(), clazz);
    }

    static {
        registerClass(BooleanObj.SYNTHETIC);
        registerClass(FunctionObj.SYNTHETIC);
        registerClass(ListObj.SYNTHETIC);
        registerClass(NullObj.SYNTHETIC);
        registerClass(NumberObj.SYNTHETIC);
        registerClass(StringObj.SYNTHETIC);
    }
}
