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

package me.abje.zero.parser.expr;

import me.abje.zero.interpreter.Interpreter;
import me.abje.zero.interpreter.obj.Obj;

import java.util.List;
import java.util.stream.Collectors;

public class CallExpr extends Expr {
    private Expr func;
    private List<Expr> args;

    public CallExpr(Expr func, List<Expr> args) {
        this.func = func;
        this.args = args;
    }

    @Override
    public Obj evaluate(Interpreter interpreter) {
        List<Obj> argObjs = args.stream().map(arg -> arg.evaluate(interpreter)).collect(Collectors.toList());
        return func.evaluate(interpreter).call(interpreter, argObjs);
    }

    public Expr getFunc() {
        return func;
    }

    public List<Expr> getArgs() {
        return args;
    }
}
