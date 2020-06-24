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

package me.abje.lingua.parser.parselet;

import me.abje.lingua.lexer.Token;
import me.abje.lingua.parser.ParseException;
import me.abje.lingua.parser.Parser;
import me.abje.lingua.parser.expr.BlockExpr;
import me.abje.lingua.parser.expr.Expr;
import me.abje.lingua.parser.expr.WhileExpr;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses a while loop expression, such as <code>while (x) { y }</code>
 */
public class WhileParselet implements PrefixParselet {
    /**
     * If true, this loop is a do-while loop of the format:
     * <pre>
     *     do {
     *         xyz()
     *     } while (abc)
     * </pre>
     */
    private final boolean doWhile;

    /**
     * Creates a new while loop expression parselet.
     *
     * @param doWhile Whether this loop is a do-while loop.
     */
    public WhileParselet(boolean doWhile) {
        this.doWhile = doWhile;
    }

    @Override
    public Expr parse(Parser parser, Token token) {
        Expr condition;
        Expr body;
        if (doWhile) {
            List<Expr> exprs = new ArrayList<>();
            while (!parser.match(Token.Type.WHILE)) {
                exprs.add(parser.next());
            }
            parser.expect(Token.Type.WHILE);
            condition = parser.next();
            body = new BlockExpr(token, exprs);
        } else {
            condition = parser.next();
            body = parser.next();
        }

        if (condition == null)
            throw new ParseException("expected a condition", token);
        if (body == null)
            throw new ParseException("expected a body", token);

        return new WhileExpr(token, condition, body, doWhile);
    }
}
