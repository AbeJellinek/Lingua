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

package me.abje.zero.parser;

import me.abje.zero.interpreter.obj.Field;
import me.abje.zero.lexer.Lexer;
import me.abje.zero.lexer.Morpher;
import me.abje.zero.lexer.Token;
import me.abje.zero.parser.expr.*;
import org.junit.Test;

import java.io.StringReader;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ParserTest {
    private Parser parser;

    private void input(String input) {
        parser = new Parser(new Morpher(new Lexer(new StringReader(input))));
    }

    @Test
    public void testNext() throws Exception {
        Expr e;

        input("@ann test");
        e = parser.next();
        assertThat(e, is(instanceOf(NameExpr.class)));
        assertThat(((NameExpr) e).getValue(), is("test"));
        assertThat(e.getAnnotations(), is(Arrays.asList("ann")));

        input("x = y");
        e = parser.next();
        assertThat(e, is(new AssignmentExpr("x", new NameExpr("y"))));

        input("x += y");
        e = parser.next();
        assertThat(e, is(new AssignmentExpr("x", new OperatorExpr(new NameExpr("x"), Token.Type.PLUS, new NameExpr("y")))));

        input("x + y");
        e = parser.next();
        assertThat(e, is(new OperatorExpr(new NameExpr("x"), Token.Type.PLUS, new NameExpr("y"))));

        input("{ xyz 123 }");
        e = parser.next();
        assertThat(e, is(new BlockExpr(Arrays.asList(new NameExpr("xyz"), new NumberExpr(123)))));

        input("true false");
        e = parser.next();
        assertThat(e, is(new BooleanExpr(true)));
        e = parser.next();
        assertThat(e, is(new BooleanExpr(false)));

        input("x(y, 123)");
        e = parser.next();
        assertThat(e, is(new CallExpr(new NameExpr("x"), Arrays.asList(new NameExpr("y"), new NumberExpr(123)))));

        input("class Test {\n foo \n bar(x, y) = z \n}");
        e = parser.next();
        assertThat(e, is(new ClassExpr("Test", Arrays.asList(
                new FunctionExpr("bar", Arrays.asList("x", "y"), new NameExpr("z"))),
                Arrays.asList(new Field(null, "foo", new NullExpr())))));

        input("if x y else z; if (x) y");
        e = parser.next();
        assertThat(e, is(new IfExpr(new NameExpr("x"), new NameExpr("y"), new NameExpr("z"))));
        e = parser.next();
        assertThat(e, is(new IfExpr(new NameExpr("x"), new NameExpr("y"), null)));

        input("x[y][z]");
        e = parser.next();
        assertThat(e, is(new IndexExpr(new IndexExpr(new NameExpr("x"), new NameExpr("y")), new NameExpr("z"))));

        input("[x, y z]");
        e = parser.next();
        assertThat(e, is(new ListExpr(Arrays.asList(new NameExpr("x"), new NameExpr("y"), new NameExpr("z")))));

        input("foo.bar.baz");
        e = parser.next();
        assertThat(e, is(new MemberAccessExpr(new MemberAccessExpr(new NameExpr("foo"), "bar"), "baz")));

        input("x -> y + z");
        e = parser.next();
        assertThat(e, is(new FunctionExpr("<anon>", Arrays.asList("x"),
                new OperatorExpr(new NameExpr("y"), Token.Type.PLUS, new NameExpr("z")))));

        input("foobar");
        e = parser.next();
        assertThat(e, is(new NameExpr("foobar")));

        input("null");
        e = parser.next();
        assertThat(e, is(new NullExpr()));

        input("192.168");
        e = parser.next();
        assertThat(e, is(new NumberExpr(192.168f)));

        input("(x + y) * z");
        e = parser.next();
        assertThat(e, is(new OperatorExpr(new OperatorExpr(
                new NameExpr("x"), Token.Type.PLUS, new NameExpr("y")), Token.Type.TIMES, new NameExpr("z"))));

        input("10! + 2");
        e = parser.next();
        assertThat(e, is(new OperatorExpr(new PostfixExpr(
                new NumberExpr(10), Token.Type.BANG), Token.Type.PLUS, new NumberExpr(2))));

        input("!true");
        e = parser.next();
        assertThat(e, is(new PrefixExpr(Token.Type.BANG, new BooleanExpr(true))));

        input("\"abc def\"");
        e = parser.next();
        assertThat(e, is(new StringExpr("abc def")));

        input("while x y");
        e = parser.next();
        assertThat(e, is(new WhileExpr(new NameExpr("x"), new NameExpr("y"))));
    }
}
