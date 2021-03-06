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

import me.abje.lingua.interpreter.obj.DefaultObjField;
import me.abje.lingua.interpreter.obj.ObjField;
import me.abje.lingua.lexer.Token;
import me.abje.lingua.parser.ParseException;
import me.abje.lingua.parser.Parser;
import me.abje.lingua.parser.expr.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses a class definition expression.
 */
public class ClassParselet implements PrefixParselet {
    @Override
    public Expr parse(Parser parser, Token token) {
        Token name = parser.read();
        String superClassName = "Obj";
        if (!name.is(Token.Type.NAME))
            throw new ParseException("invalid class name", name);
        parser.eatLines();
        if (parser.match(Token.Type.COLON))
            superClassName = parser.read().getValue();
        parser.expect(Token.Type.OPEN_BRACE);
        List<ObjField> fields = new ArrayList<>();
        List<FunctionExpr> functions = new ArrayList<>();
        while (!parser.match(Token.Type.CLOSE_BRACE)) {
            Expr expr = parser.next();
            if (expr instanceof FunctionExpr) {
                functions.add((FunctionExpr) expr);
            } else if (expr instanceof AssignmentExpr) {
                AssignmentExpr assgn = (AssignmentExpr) expr;
                if (assgn.getName() instanceof NameExpr) {
                    fields.add(new DefaultObjField(((NameExpr) assgn.getName()).getValue(), false, assgn.getValue()));
                } else {
                    throw new ParseException("invalid class member (left side must be a name)", expr.getToken());
                }
            } else if (expr instanceof NameExpr) {
                fields.add(new DefaultObjField(((NameExpr) expr).getValue(), false, new NullExpr(token)));
            } else {
                throw new ParseException("invalid class member", expr.getToken());
            }
            parser.eatLines();
        }
        return new ClassExpr(token, name.getValue(), functions, fields, superClassName);
    }
}
