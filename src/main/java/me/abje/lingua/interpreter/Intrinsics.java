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
import me.abje.lingua.interpreter.obj.bridge.ObjectBridge;
import me.abje.lingua.lexer.Lexer;
import me.abje.lingua.lexer.Morpher;
import me.abje.lingua.parser.Parser;
import me.abje.lingua.parser.expr.Expr;
import me.abje.lingua.util.TriFunction;

import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Registers intrinsic (built-in) functions with the interpreter.
 */
public class Intrinsics {
    /**
     * The environment to register in.
     */
    private Environment env;

    /**
     * Creates a new Intrinsics.
     *
     * @param env The environment to register in.
     */
    public Intrinsics(Environment env) {
        this.env = env;
    }

    @Bridge(anyLength = true)
    public void print(Obj... args) {
        System.out.println(Arrays.asList(args).stream().map(Object::toString).collect(Collectors.joining("")));
    }

    @Bridge(anyLength = true)
    public void error(Obj... args) {
        System.err.println(Arrays.asList(args).stream().map(Object::toString).collect(Collectors.joining("")));
    }

    @Bridge
    public ClassObj classOf(Obj obj) {
        return obj.getType();
    }

    @Bridge
    public Obj eval(StringObj code, Interpreter interpreter) {
        Parser parser = new Parser(new Morpher(new Lexer(new StringReader(code.getValue()), "<eval>")));
        Expr expr;
        Obj result = NullObj.get();
        while ((expr = parser.next()) != null) {
            result = interpreter.next(expr);
        }
        return result;
    }

    @Bridge
    public NumberObj sqrt(NumberObj num) {
        return NumberObj.of((float) Math.sqrt(num.getValue()));
    }

    @Bridge
    public StringObj read() {
        Scanner scanner = new Scanner(System.in);
        if (scanner.hasNextLine()) {
            return new StringObj(scanner.nextLine());
        } else {
            return null;
        }
    }

    @Bridge
    public StringObj read(StringObj prompt) {
        Scanner scanner = new Scanner(System.in);
        System.out.print(prompt);
        if (scanner.hasNextLine()) {
            return new StringObj(scanner.nextLine());
        } else {
            return null;
        }
    }

    @Bridge
    public ListObj dumpStack() {
        return new ListObj((env.getOldStack() != null ? env.getOldStack() : env.getStack()).stream().map(frame -> {
            if (frame.getFileName() == null || frame.getFileName().equals("<native>")) {
                return new StringObj(frame.getName() + "(native)");
            } else {
                return new StringObj(frame.getName() + "(" + frame.getFileName() + ":" + frame.getLine() + ")");
            }
        }).collect(Collectors.toList()));
    }

    @Bridge("throw")
    public void doThrow(Obj obj) {
        throw new InterpreterException(obj);
    }

    @Bridge("native")
    public ClassObj doNative(StringObj name) {
        try {
            Class<?> clazz = Class.forName(name.getValue());
            return (ClassObj) clazz.getField("SYNTHETIC").get(null);
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Register the intrinsics.
     */
    public void register() {
        ObjectBridge.createMethodMap(Intrinsics.class, this).
                forEach((name, map) -> addFunction(name, ObjectBridge.createFunctionBridge(name, map)));
    }

    /**
     * Registers a function.
     *
     * @param name The function's name.
     * @param func The function's body.
     */
    private void addFunction(String name, TriFunction<Interpreter, Obj, List<Obj>, Obj> func) {
        env.getGlobals().define(name, new SyntheticFunctionObj(FunctionObj.SYNTHETIC) {
            @Override
            public Obj call(Interpreter interpreter, List<Obj> args) {
                return func.apply(interpreter, getSelf(), args);
            }
        });
    }
}
