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

package me.abje.lingua.parser.expr;

import me.abje.lingua.interpreter.Environment;
import me.abje.lingua.interpreter.Interpreter;
import me.abje.lingua.interpreter.InterpreterException;
import me.abje.lingua.interpreter.obj.Obj;
import me.abje.lingua.lexer.Token;

import java.util.Deque;
import java.util.Map;

public class TryCatchExpr extends Expr {
    private final Expr body;
    private final Map<Expr, Expr> clauses;

    public TryCatchExpr(Token token, Expr body, Map<Expr, Expr> clauses) {
        super(token);
        this.body = body;
        this.clauses = clauses;
    }

    @Override
    public Obj evaluate(Interpreter interpreter) {
        try {
            return body.evaluate(interpreter);
        } catch (InterpreterException e) {
            e.initialize(interpreter);
            for (Map.Entry<Expr, Expr> clause : clauses.entrySet()) {
                Deque<Environment.Frame> stack = interpreter.getEnv().getStack();
                interpreter.getEnv().setOldStack(stack);
                interpreter.getEnv().pushFrame("<catch>");
                Obj result = null;
                if (clause.getKey().match(interpreter, interpreter.getEnv().getStack().peek(), e.getExceptionObj(), true) != null) {
                    result = clause.getValue().evaluate(interpreter);
                }
                interpreter.getEnv().popFrame();

                if (result != null)
                    return result;
            }

            throw e;
        }
    }

    @Override
    public String toString() {
        return "try {...} catch {...}";
    }
}
