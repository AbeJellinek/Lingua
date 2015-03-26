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

import me.abje.lingua.Phase;
import me.abje.lingua.interpreter.obj.Obj;
import me.abje.lingua.lexer.Lexer;
import me.abje.lingua.lexer.Morpher;
import me.abje.lingua.parser.ParseException;
import me.abje.lingua.parser.Parser;
import me.abje.lingua.parser.expr.Expr;

import java.io.*;
import java.util.*;

/**
 * The phase which produces objects from expressions. Usually the last phase in the pipeline.
 */
public class Interpreter implements Phase<Expr, Obj> {
    /**
     * The environment used for interpretation.
     */
    private Environment env = new Environment();

    public static void main(String[] args) throws FileNotFoundException {
        if (args.length == 1) {
            Interpreter interpreter = new Interpreter();
            try {
                new Intrinsics(interpreter.env).register();
                interpreter.addImport("core");
                interpreter.addImport(args[0]);
            } catch (ParseException e) {
                System.err.println(e.getMessage());
            } catch (InterpreterException e) {
                handleInterpreterException(e, interpreter);
            }
        } else if (args.length == 0) {
            Scanner in = new Scanner(System.in);
            Interpreter interpreter = new Interpreter();
            new Intrinsics(interpreter.env).register();
            interpreter.addImport("core");

            System.out.println("Welcome to Lingua REPL version 1.0 (" +
                    System.getProperty("java.vm.name") + ", Java " +
                    System.getProperty("java.version") + ").");
            System.out.println("Type in expressions to evaluate them.\n");
            System.out.print("lingua> ");

            int num = 0;
            while (in.hasNextLine()) {
                try {
                    String line = in.nextLine();
                    while (line.trim().endsWith("\\")) {
                        System.out.print("| ");
                        line += in.nextLine();
                    }

                    Parser parser = new Parser(new Morpher(new Lexer(new StringReader(line), "<user>")));
                    List<Expr> exprs = new ArrayList<>();
                    Expr expr;
                    while ((expr = parser.next()) != null) {
                        exprs.add(expr);
                    }

                    for (Expr x : exprs) {
                        Obj value = interpreter.next(x);
                        do {
                            num++;
                        } while (interpreter.env.has("res" + num));
                        String varName = "res" + num;
                        interpreter.env.define(varName, value);
                        System.out.println(varName + " = " + value);
                    }
                } catch (ParseException e) {
                    System.err.println(e.getMessage());
                } catch (InterpreterException e) {
                    handleInterpreterException(e, interpreter);
                }

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.print("lingua> ");
            }
        } else {
            System.err.println("Usage: lingua [script]");
        }
    }

    private static void handleInterpreterException(InterpreterException e, Interpreter interpreter) {
        e.initialize(interpreter);

        Deque<Environment.Frame> stack = interpreter.getEnv().getStack();
        ArrayDeque<Environment.Frame> oldStack = new ArrayDeque<>(stack);
        interpreter.getEnv().setOldStack(oldStack);
        e.getExceptionObj().getMember("printError").call(interpreter, Collections.emptyList());
        interpreter.getEnv().setOldStack(null);
        interpreter.getEnv().setStack(stack);
    }

    public void addImport(String fullName) {
        try {
            String name = fullName.replace('.', '/') + ".ling";
            InputStream classpathStream = Interpreter.class.getResourceAsStream("/" + name);
            String[] parts = name.split("/");
            if (classpathStream != null) {
                interpret(new InputStreamReader(classpathStream), parts[parts.length - 1]);
            } else {
                File file = new File(name);
                interpret(new FileReader(file), parts[parts.length - 1]);
            }
        } catch (FileNotFoundException e) {
            throw new InterpreterException("UndefinedException", "module not found: " + fullName, this);
        }
    }

    /**
     * Interprets the given expression.
     *
     * @param expr The expression to interpret.
     * @return The result of interpreting the expression.
     */
    public Obj next(Expr expr) {
        env.getStack().peek().setLine(expr.getToken().getLine());
        env.getStack().peek().setFileName(expr.getToken().getFile());
        return expr.evaluate(this);
    }

    /**
     * Interprets each expression in the given input.
     *
     * @param reader   The input.
     * @param fileName The file name of the input.
     */
    public void interpret(Reader reader, String fileName) {
        Parser parser = new Parser(new Morpher(new Lexer(reader, fileName)));
        List<Expr> exprs = new ArrayList<>();
        Expr expr;
        while ((expr = parser.next()) != null) {
            exprs.add(expr);
        }
        exprs.forEach(this::next);
    }

    /**
     * Returns the environment used for interpretation.
     */
    public Environment getEnv() {
        return env;
    }
}
