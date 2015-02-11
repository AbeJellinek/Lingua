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

import me.abje.lingua.interpreter.Interpreter;
import me.abje.lingua.interpreter.obj.ListObj;
import me.abje.lingua.interpreter.obj.Obj;
import me.abje.lingua.lexer.Token;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A list expression, in the form of <code>[item1, item2, item3, ..., itemN]</code>.
 */
public class ListExpr extends Expr {
    /**
     * This list's items.
     */
    private List<Expr> items;

    /**
     * Creates a new list expression.
     *
     * @param items The list's items.
     */
    public ListExpr(Token token, List<Expr> items) {
        super(token);
        this.items = items;
    }

    /**
     * Returns this list's items.
     */
    public List<Expr> getItems() {
        return items;
    }

    @Override
    public Obj evaluate(Interpreter interpreter) {
        List<Obj> itemObjs = items.stream().map(interpreter::next).collect(Collectors.toList());
        return new ListObj(itemObjs);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ListExpr listExpr = (ListExpr) o;

        return items.equals(listExpr.items);
    }

    @Override
    public int hashCode() {
        return items.hashCode();
    }
}
