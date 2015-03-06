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
import me.abje.lingua.interpreter.obj.Obj;
import me.abje.lingua.interpreter.obj.TupleObj;
import me.abje.lingua.lexer.Token;

import java.util.List;
import java.util.stream.Collectors;

public class TupleExpr extends Expr {
    private final List<Expr> exprs;

    public TupleExpr(Token token, List<Expr> exprs) {
        super(token);
        this.exprs = exprs;
    }

    @Override
    public Obj evaluate(Interpreter interpreter) {
        return new TupleObj(exprs.stream().map(interpreter::next).collect(Collectors.toList()));
    }

    @Override
    public Obj match(Interpreter interpreter, Environment.Frame frame, Obj obj) {
        if (obj instanceof TupleObj) {
            TupleObj tuple = (TupleObj) obj;
            for (int i = 0; i < tuple.size(); i++) {
                if (exprs.get(i).match(interpreter, frame, tuple.get(i)) == null) {
                    return null;
                }
            }
            return obj;
        } else {
            return null;
        }
    }

    public List<Expr> getItems() {
        return exprs;
    }
}
