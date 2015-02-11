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

package me.abje.lingua.parser;

import me.abje.lingua.interpreter.obj.Field;
import me.abje.lingua.lexer.Lexer;
import me.abje.lingua.lexer.Morpher;
import me.abje.lingua.parser.expr.*;
import org.junit.Test;

import java.io.StringReader;

import static java.util.Arrays.asList;
import static me.abje.lingua.lexer.Token.Type.*;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ParserTest {
    private Parser parser;

    private void input(String input) {
        parser = new Parser(new Morpher(new Lexer(new StringReader(input), "<test>")));
    }

    @Test
    public void testNext() throws Exception {
        Expr e;

        input("@ann test");
        e = parser.next();
        assertThat(e, is(instanceOf(NameExpr.class)));
        assertThat(((NameExpr) e).getValue(), is("test"));
        assertThat(e.getAnnotations(), is(asList("ann")));

        input("x = y");
        e = parser.next();
        assertThat(e, is(new AssignmentExpr(null, "x", new NameExpr(null, "y"))));

        input("x += y");
        e = parser.next();
        assertThat(e, is(new AssignmentExpr(null, "x", new OperatorExpr(PLUS, new NameExpr(null, "x"), new NameExpr(null, "y")))));

        input("x + y");
        e = parser.next();
        assertThat(e, is(new OperatorExpr(PLUS, new NameExpr(null, "x"), new NameExpr(null, "y"))));

        input("{ xyz 123 }");
        e = parser.next();
        assertThat(e, is(new BlockExpr(null, asList(new NameExpr(null, "xyz"), new NumberExpr(null, 123)))));

        input("true false");
        e = parser.next();
        assertThat(e, is(new BooleanExpr(null, true)));
        e = parser.next();
        assertThat(e, is(new BooleanExpr(null, false)));

        input("x(y, 123)");
        e = parser.next();
        assertThat(e, is(new CallExpr(null, new NameExpr(null, "x"), asList(new NameExpr(null, "y"), new NumberExpr(null, 123)))));

        input("class Test {\n foo \n bar(x, y) = z \n}");
        e = parser.next();
        assertThat(e, is(new ClassExpr(null, "Test", asList(
                new FunctionExpr(null, "bar", asList("x", "y"), new NameExpr(null, "z"))), asList(new Field(null, "foo", new NullExpr(null))), "Obj")));

        input("if x y else z; if (x) y");
        e = parser.next();
        assertThat(e, is(new IfExpr(null, new NameExpr(null, "x"), new NameExpr(null, "y"), new NameExpr(null, "z"))));
        e = parser.next();
        assertThat(e, is(new IfExpr(null, new NameExpr(null, "x"), new NameExpr(null, "y"), null)));

        input("x[y][z]");
        e = parser.next();
        assertThat(e, is(new IndexExpr(null, new IndexExpr(null, new NameExpr(null, "x"), new NameExpr(null, "y")), new NameExpr(null, "z"))));

        input("[x, y z]");
        e = parser.next();
        assertThat(e, is(new ListExpr(null, asList(new NameExpr(null, "x"), new NameExpr(null, "y"), new NameExpr(null, "z")))));

        input("foo.bar.baz");
        e = parser.next();
        assertThat(e, is(new MemberAccessExpr(null, new MemberAccessExpr(null, new NameExpr(null, "foo"), "bar"), "baz")));

        input("x -> y + z");
        e = parser.next();
        assertThat(e, is(new FunctionExpr(null, "<anon>", asList("x"), new OperatorExpr(PLUS, new NameExpr(null, "y"), new NameExpr(null, "z")))));

        input("foobar");
        e = parser.next();
        assertThat(e, is(new NameExpr(null, "foobar")));

        input("null");
        e = parser.next();
        assertThat(e, is(new NullExpr(null)));

        input("192.168");
        e = parser.next();
        assertThat(e, is(new NumberExpr(null, 192.168f)));

        input("(x + y) * z");
        e = parser.next();
        assertThat(e, is(new OperatorExpr(TIMES, new OperatorExpr(PLUS,
                new NameExpr(null, "x"), new NameExpr(null, "y")), new NameExpr(null, "z"))));

        input("10! + 2");
        e = parser.next();
        assertThat(e, is(new OperatorExpr(PLUS, new PostfixExpr(null,
                new NumberExpr(null, 10), BANG), new NumberExpr(null, 2))));

        input("!true");
        e = parser.next();
        assertThat(e, is(new PrefixExpr(null, BANG, new BooleanExpr(null, true))));

        input("\"abc def\"");
        e = parser.next();
        assertThat(e, is(new StringExpr(null, "abc def")));

        input("while x y; do y while x");
        e = parser.next();
        assertThat(e, is(new WhileExpr(null, new NameExpr(null, "x"), new NameExpr(null, "y"), false)));
        e = parser.next();
        assertThat(e, is(new WhileExpr(null, new NameExpr(null, "x"), new BlockExpr(null, asList(new NameExpr(null, "y"))), true)));
    }
}
