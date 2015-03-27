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

import jline.console.ConsoleReader;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import me.abje.lingua.interpreter.obj.Obj;
import me.abje.lingua.lexer.Lexer;
import me.abje.lingua.lexer.Morpher;
import me.abje.lingua.parser.ParseException;
import me.abje.lingua.parser.Parser;
import me.abje.lingua.parser.expr.*;

import java.io.*;
import java.util.*;

/**
 * The phase which produces objects from expressions. Usually the last phase in the pipeline.
 */
public class Interpreter {
    private static ConsoleReader console;

    static {
        try {
            console = new ConsoleReader();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * The environment used for interpretation.
     */
    private Environment env = new Environment();
    private List<String> imported = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        OptionParser optParser = new OptionParser() {
            {
                accepts("no-core", "Don't import core (advanced)");
                accepts("h", "Show help").forHelp();
            }
        };
        OptionSet options = optParser.parse(args);

        if (options.has("h")) {
            optParser.printHelpOn(System.out);
            return;
        }

        List<?> files = options.nonOptionArguments();
        Interpreter interpreter = new Interpreter();

        new Intrinsics(interpreter.env).register();
        if (!options.has("no-core"))
            interpreter.addImport("lingua.core");

        if (!files.isEmpty()) {
            try {
                for (Object file : files)
                    interpreter.addImport((String) file);
            } catch (ParseException e) {
                System.err.println(e.getMessage());
            } catch (InterpreterException e) {
                handleInterpreterException(e, interpreter);
            }
        } else {
            console.println("Welcome to Lingua REPL version 1.0 (" +
                    System.getProperty("java.vm.name") + ", Java " +
                    System.getProperty("java.version") + ").");
            console.println("Type in expressions to evaluate them.");
            console.println();

            int num = 0;
            while (true) {
                try {
                    String line = console.readLine("lingua> ");
                    if (line == null || line.equals(":exit") || line.equals(":quit"))
                        break;

                    while (line.trim().endsWith("\\")) {
                        line += console.readLine("| ");
                    }

                    Parser parser = new Parser(new Morpher(new Lexer(new StringReader(line), "<user>")));
                    List<Expr> exprs = new ArrayList<>();
                    Expr expr;
                    while ((expr = parser.next()) != null) {
                        exprs.add(expr);
                    }

                    for (Expr x : exprs) {
                        Obj value = interpreter.next(x);
                        if (x instanceof AssignmentExpr || x instanceof FunctionExpr ||
                                x instanceof IndexSetExpr || x instanceof MemberSetExpr) {
                            System.out.println(x);
                        } else {
                            do {
                                num++;
                            } while (interpreter.env.has("res" + num));
                            String varName = "res" + num;
                            interpreter.env.define(varName, value);
                            console.println(varName + " = " + value);
                        }
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
            }
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
            if (!imported.contains(name)) {
                InputStream classpathStream = Interpreter.class.getResourceAsStream("/" + name);
                String[] parts = name.split("/");
                imported.add(name);
                if (classpathStream != null) {
                    interpret(new InputStreamReader(classpathStream), parts[parts.length - 1]);
                } else {
                    File file = new File(name);
                    interpret(new FileReader(file), parts[parts.length - 1]);
                }
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
