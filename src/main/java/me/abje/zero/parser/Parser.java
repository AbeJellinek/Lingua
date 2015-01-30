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

import me.abje.zero.Phase;
import me.abje.zero.lexer.Token;
import me.abje.zero.parser.expr.Expr;
import me.abje.zero.parser.parselet.*;

import java.util.HashMap;
import java.util.Map;

import static me.abje.zero.lexer.Token.Type.*;

public class Parser implements Phase<Token, Expr> {
    private Map<Token.Type, PrefixParselet> prefixParselets = new HashMap<>();
    private Map<Token.Type, InfixParselet> infixParselets = new HashMap<>();
    private Phase<Void, Token> lexer;
    private Token peeked;

    public Parser(Phase<Void, Token> lexer) {
        this.lexer = lexer;

        register(NAME, new NameParselet());
        register(NUMBER, new NumberParselet());
        register(OPEN_PAREN, new ParenthesesParselet());
        register(OPEN_BRACE, new BlockParselet());
        register(TRUE, new BooleanParselet());
        register(FALSE, new BooleanParselet());
        register(STRING, new StringParselet());
        register(IF, new IfParselet());
        register(WHILE, new WhileParselet(false));
        register(NULL, new NullParselet());
        register(OPEN_BRACKET, new ListParselet());
        register(CLASS, new ClassParselet());
        register(ANNOTATION, new AnnotationParselet());
        prefix(PLUS, MINUS, TILDE, BANG);
        infix(PLUS, Precedence.SUM);
        infix(MINUS, Precedence.SUM);
        infix(TIMES, Precedence.PRODUCT);
        infix(DIVIDE, Precedence.PRODUCT);
        infix(POW, Precedence.EXPONENT);
        infix(EQEQ, Precedence.EQUALITY);
        infix(NEQ, Precedence.EQUALITY);
        infix(LT, Precedence.COMPARISON);
        infix(LTE, Precedence.COMPARISON);
        infix(GT, Precedence.COMPARISON);
        infix(GTE, Precedence.COMPARISON);
        infix(ANDAND, Precedence.LOGICAL);
        infix(OROR, Precedence.LOGICAL);
        register(OPEN_PAREN, new CallParselet());
        register(EQ, new AssignmentParselet());
        register(OPEN_BRACKET, new IndexParselet());
        register(DOT, new MemberAccessParselet());
        register(ARROW, new MiniFunctionParselet());
        postfix(BANG);
    }

    public Expr next(Token token, int precedence) {
        while (token != null && token.is(LINE)) {
            token = read();
        }

        if (token == null)
            return null;

        PrefixParselet prefix = prefixParselets.get(token.getType());

        if (prefix == null)
            throw new ParseException("unexpected " + token.getType());

        Expr left = prefix.parse(this, token);

        while (precedence < getPrecedence()) {
            token = read();

            if (token == null || token.is(LINE))
                break;

            InfixParselet infix = infixParselets.get(token.getType());
            left = infix.parse(this, left, token);
        }

        return left;
    }

    @Override
    public Expr next(Token token) {
        return next(token, 0);
    }

    public Expr next(int precedence) {
        return next(read(), precedence);
    }

    public Expr next() {
        return next(read());
    }

    private int getPrecedence() {
        Token peeked = peek();
        if (peeked == null)
            return 0;

        InfixParselet parser = infixParselets.get(peeked.getType());
        if (parser != null) return parser.getPrecedence();

        return 0;
    }

    public Token peek() {
        if (peeked != null)
            return peeked;
        return peeked = read();
    }

    public void register(Token.Type token, PrefixParselet parselet) {
        prefixParselets.put(token, parselet);
    }

    public void prefix(Token.Type token) {
        register(token, new PrefixOperatorParselet());
    }

    public void prefix(Token.Type... tokens) {
        for (Token.Type token : tokens) {
            prefix(token);
        }
    }

    public void register(Token.Type token, InfixParselet parselet) {
        infixParselets.put(token, parselet);
    }

    public void infix(Token.Type token, int precedence) {
        register(token, new BinaryOperatorParselet(precedence));
    }

    public void postfix(Token.Type token) {
        register(token, new PostfixOperatorParselet());
    }

    public void postfix(Token.Type... tokens) {
        for (Token.Type token : tokens) {
            postfix(token);
        }
    }

    public Token read() {
        if (peeked != null) {
            Token peeked = this.peeked;
            this.peeked = null;
            return peeked;
        } else {
            return lexer.next(null);
        }
    }

    public void expect(Token.Type type) {
        if (peek() == null || !read().is(type)) {
            throw new ParseException("expected " + type);
        }
    }

    public void eatLines() {
        while (peek().is(LINE)) {
            read();
        }
    }

    public boolean match(Token.Type type) {
        return peek() != null && read().is(type);
    }
}
