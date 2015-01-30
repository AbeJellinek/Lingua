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

package me.abje.zero.parser.parselet;

import me.abje.zero.lexer.Token;
import me.abje.zero.parser.ParseException;
import me.abje.zero.parser.Parser;
import me.abje.zero.parser.Precedence;
import me.abje.zero.parser.expr.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses an assignment, index assignment, or method definition expression.
 */
public class AssignmentParselet implements InfixParselet {
    @Override
    public Expr parse(Parser parser, Expr left, Token token) {
        if (left instanceof CallExpr) {
            CallExpr call = (CallExpr) left;
            List<String> argNames = new ArrayList<>();
            for (Expr arg : call.getArgs()) {
                if (arg instanceof NameExpr) {
                    argNames.add(((NameExpr) arg).getValue());
                } else {
                    throw new ParseException("function definition arguments must be names");
                }
            }

            if (call.getFunc() instanceof NameExpr) {
                Expr value = parser.next(Precedence.ASSIGNMENT - 1);
                return new FunctionExpr(((NameExpr) call.getFunc()).getValue(), argNames, value);
            } else {
                throw new ParseException("function name must actually be a name");
            }
        } else if (left instanceof IndexExpr) {
            IndexExpr index = (IndexExpr) left;
            Expr value = parser.next(Precedence.ASSIGNMENT - 1);
            return new IndexSetExpr(index.getTarget(), index.getIndex(), value);
        } else if (left instanceof MemberAccessExpr) {
            MemberAccessExpr member = (MemberAccessExpr) left;
            Expr value = parser.next(Precedence.ASSIGNMENT - 1);
            return new MemberSetExpr(member.getLeft(), member.getName(), value);
        } else if (left instanceof NameExpr) {
            Expr value = parser.next(Precedence.ASSIGNMENT);
            return new AssignmentExpr(((NameExpr) left).getValue(), value);
        } else {
            throw new ParseException("assignments must have a function call, index expression, or variable as a target");
        }
    }

    @Override
    public int getPrecedence() {
        return Precedence.ASSIGNMENT;
    }
}
