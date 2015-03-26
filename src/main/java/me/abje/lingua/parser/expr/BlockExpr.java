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

import com.google.common.base.Joiner;
import me.abje.lingua.interpreter.Interpreter;
import me.abje.lingua.interpreter.obj.Obj;
import me.abje.lingua.lexer.Token;

import java.util.List;

/**
 * An expression of a block of code.
 */
public class BlockExpr extends Expr {
    /**
     * The list of expressions within this block.
     */
    private List<Expr> exprs;

    /**
     * Creates a new block expression.
     *
     * @param exprs The list of expressions within the block.
     */
    public BlockExpr(Token token, List<Expr> exprs) {
        super(token);
        this.exprs = exprs;
    }

    @Override
    public Obj evaluate(Interpreter interpreter) {
        interpreter.getEnv().pushFrame("<anon>");
        Obj result = null;
        for (Expr expr : exprs) {
            result = interpreter.next(expr);
        }
        interpreter.getEnv().popFrame();
        return result;
    }

    /**
     * Returns the list of expressions within this block.
     */
    public List<Expr> getExprs() {
        return exprs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlockExpr blockExpr = (BlockExpr) o;

        return exprs.equals(blockExpr.exprs);
    }

    @Override
    public int hashCode() {
        return exprs.hashCode();
    }

    @Override
    public String toString() {
        return "{\n" + Joiner.on('\n').join(exprs) + "\n}";
    }
}
