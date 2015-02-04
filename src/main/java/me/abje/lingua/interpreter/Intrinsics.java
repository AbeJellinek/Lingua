/*
 * Copyright (c) 2015 Abe Jellinek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package me.abje.lingua.interpreter;

import me.abje.lingua.interpreter.obj.*;
import me.abje.lingua.lexer.Lexer;
import me.abje.lingua.lexer.Morpher;
import me.abje.lingua.parser.Parser;
import me.abje.lingua.parser.expr.Expr;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Registers intrinsic (built-in) functions with the interpreter.
 */
public class Intrinsics {
    /**
     * The map of names to intrinsic (synthetic) classes to register.
     */
    private static Map<String, ClassObj> classes = new HashMap<>();
    /**
     * The environment to register in.
     */
    private Environment.Frame env;

    /**
     * Creates a new Intrinsics.
     *
     * @param env The environment to register in.
     */
    public Intrinsics(Environment.Frame env) {
        this.env = env;
    }

    /**
     * Registers a class.
     *
     * @param clazz The class.
     */
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

    /**
     * Register the intrinsics.
     */
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

    /**
     * Registers a function.
     *
     * @param name The function's name.
     * @param func The function's body.
     */
    private void addFunction(String name, BiFunction<Interpreter, List<Obj>, Obj> func) {
        env.define(name, new Obj(FunctionObj.SYNTHETIC) {
            @Override
            public Obj call(Interpreter interpreter, List<Obj> args) {
                return func.apply(interpreter, args);
            }
        });
    }
}
