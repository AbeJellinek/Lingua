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

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import me.abje.lingua.interpreter.obj.Obj;
import me.abje.lingua.lexer.Lexer;
import me.abje.lingua.lexer.Morpher;
import me.abje.lingua.parser.ParseException;
import me.abje.lingua.parser.Parser;
import me.abje.lingua.parser.expr.*;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.jline.utils.InfoCmp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 * The phase which produces objects from expressions. Usually the last phase in the pipeline.
 */
public class Interpreter {
    public static Logger log = LoggerFactory.getLogger(Interpreter.class);
    private static final Terminal terminal;
    private static final LineReader in;
    private static final PrintWriter out;

    static {
        try {
            terminal = TerminalBuilder.terminal();
            in = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .option(LineReader.Option.INSERT_BRACKET, true)
                    .build();
            out = terminal.writer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * The environment used for interpretation.
     */
    private final Environment env = new Environment();
    private final List<String> imported = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws IOException {
        OptionParser optParser = new OptionParser() {
            {
                accepts("clear", "Clear the screen before running");
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

        new Intrinsics(interpreter.env).register(files.isEmpty() ? Collections.emptyList() :
                (List<String>) files.subList(1, files.size()));
        if (!options.has("no-core"))
            interpreter.addImport("lingua.core");
        if (options.has("clear"))
            terminal.puts(InfoCmp.Capability.clear_screen);

        if (!files.isEmpty()) {
            try {
                interpreter.addImport((String) files.get(0));
            } catch (ParseException e) {
                log.error("Parse error:\n{}", e.getMessage());
            } catch (InterpreterException e) {
                handleInterpreterException(e, interpreter);
            }
        } else {
            out.println("Welcome to Lingua REPL version 1.0 (Java " +
                    System.getProperty("java.version") + ").");
            out.println("Type in expressions to evaluate them.");
            out.println();

            int num = 0;
            while (true) {
                try {
                    String prompt = new AttributedStringBuilder()
                            .style(AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN))
                            .append("lingua> ")
                            .style(AttributedStyle.DEFAULT)
                            .toAnsi();
                    StringBuilder line = new StringBuilder(in.readLine(prompt));
                    if (line.toString().equals(":exit") || line.toString().equals(":quit"))
                        break;

                    while (line.toString().trim().endsWith("\\")) {
                        String continuationPrompt = new AttributedStringBuilder()
                                .style(AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW))
                                .append(" | ")
                                .style(AttributedStyle.DEFAULT)
                                .toAnsi();

                        line.append(in.readLine(continuationPrompt));
                    }

                    Parser parser = new Parser(new Morpher(new Lexer(new StringReader(line.toString()), "<user>")));
                    List<Expr> exprs = new ArrayList<>();
                    Expr expr;
                    while ((expr = parser.next()) != null) {
                        exprs.add(expr);
                    }

                    for (Expr x : exprs) {
                        Obj value = interpreter.next(x);
                        if (x instanceof AssignmentExpr || x instanceof FunctionExpr ||
                                x instanceof IndexSetExpr || x instanceof MemberSetExpr) {
                            out.println(x.toString());
                        } else {
                            do {
                                num++;
                            } while (interpreter.env.has("res" + num));
                            String varName = "res" + num;
                            interpreter.env.define(varName, value);
                            out.println(varName + " = " + value);
                        }
                    }
                } catch (ParseException e) {
                    log.error("Parse error:\n{}", e.getMessage());
                } catch (InterpreterException e) {
                    handleInterpreterException(e, interpreter);
                } catch (Exception e) {
                    handleInterpreterException(new InterpreterException("Exception", e.getMessage()), interpreter);
                }

/*
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
*/
            }
        }
    }

    private static void handleInterpreterException(InterpreterException e, Interpreter interpreter) {
        e.initialize(interpreter);

        Deque<Environment.Frame> stack = interpreter.getEnv().getStack();
        ArrayDeque<Environment.Frame> oldStack = new ArrayDeque<>(stack);
        interpreter.getEnv().setOldStack(oldStack);
        e.getExceptionObj().getMember(interpreter, "printError").call(interpreter, Collections.emptyList());
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
            } else {
                log.warn("Ignoring already-imported module {}.", fullName);
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
