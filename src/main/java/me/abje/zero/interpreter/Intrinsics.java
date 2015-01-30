package me.abje.zero.interpreter;

import me.abje.zero.interpreter.obj.*;
import me.abje.zero.lexer.Lexer;
import me.abje.zero.lexer.Morpher;
import me.abje.zero.parser.Parser;
import me.abje.zero.parser.expr.Expr;

import java.io.StringReader;
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

        addFunction("eval", (interpreter, args) -> {
            if (args.size() == 1) {
                Parser parser = new Parser(new Morpher(new Lexer(new StringReader(args.get(0).toString()))));
                Expr expr;
                Obj result = NullObj.get();
                while ((expr = parser.next()) != null) {
                    result = interpreter.next(expr);
                }
                return result;
            } else {
                throw new InterpreterException("wrong number of arguments for eval");
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
