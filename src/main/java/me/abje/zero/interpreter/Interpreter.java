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

package me.abje.zero.interpreter;

import me.abje.zero.Phase;
import me.abje.zero.interpreter.obj.Obj;
import me.abje.zero.lexer.Lexer;
import me.abje.zero.lexer.Morpher;
import me.abje.zero.parser.ParseException;
import me.abje.zero.parser.Parser;
import me.abje.zero.parser.expr.Expr;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * The phase which produces objects from expressions. Usually the last phase in the pipeline.
 */
public class Interpreter implements Phase<Expr, Obj> {
    /**
     * The environment used for interpretation.
     */
    private Environment env = new Environment();

    /**
     * Interprets the given expression.
     *
     * @param expr The expression to interpret.
     * @return The result of interpreting the expression.
     */
    public Obj next(Expr expr) {
        return expr.evaluate(this);
    }

    public static void main(String[] args) throws FileNotFoundException {
        try {
            Interpreter interpreter = new Interpreter();
            new Intrinsics(interpreter.env.getGlobals()).register();
            interpreter.interpret(new InputStreamReader(Interpreter.class.getResourceAsStream("/core.zero")));
            interpreter.interpret(new FileReader("test.txt"));
        } catch (ParseException | InterpreterException e) {
            e.printStackTrace();
        }
    }

    /**
     * Interprets each expression in the given input.
     * @param reader The input.
     */
    public void interpret(Reader reader) {
        Parser parser = new Parser(new Morpher(new Lexer(reader)));
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
