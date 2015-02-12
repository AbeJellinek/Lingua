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

import me.abje.lingua.Phase;
import me.abje.lingua.lexer.Token;
import me.abje.lingua.parser.expr.Expr;
import me.abje.lingua.parser.parselet.*;

import java.util.HashMap;
import java.util.Map;

import static me.abje.lingua.lexer.Token.Type.*;

/**
 * A phase which produces expressions from tokens. Usually the second phase in the pipeline.
 */
public class Parser implements Phase<Token, Expr> {
    /**
     * The map of token types to prefix parselets.
     */
    private Map<Token.Type, PrefixParselet> prefixParselets = new HashMap<>();

    /**
     * The map of token types to infix parselets.
     */
    private Map<Token.Type, InfixParselet> infixParselets = new HashMap<>();

    /**
     * The lexer that provides input to the parser.
     */
    private Phase<Void, Token> lexer;

    /**
     * The token that was previously peeked, if any.
     */
    private Token peeked;

    /**
     * Creates a new Parser and registers the default parselets.
     *
     * @param lexer The lexer to use as input.
     */
    public Parser(Phase<Void, Token> lexer) {
        this.lexer = lexer;

        registerPrefix(NAME, new NameParselet());
        registerPrefix(NUMBER, new NumberParselet());
        registerPrefix(OPEN_PAREN, new ParenthesesParselet());
        registerPrefix(OPEN_BRACE, new BlockParselet());
        registerPrefix(TRUE, new BooleanParselet());
        registerPrefix(FALSE, new BooleanParselet());
        registerPrefix(STRING, new StringParselet());
        registerPrefix(IF, new IfParselet());
        registerPrefix(WHILE, new WhileParselet(false));
        registerPrefix(DO, new WhileParselet(true));
        registerPrefix(NULL, new NullParselet());
        registerPrefix(OPEN_BRACKET, new ListParselet());
        registerPrefix(CLASS, new ClassParselet());
        registerPrefix(ANNOTATION, new AnnotationParselet());
        registerPrefix(IMPORT, new ImportParselet());
        registerPrefix(TRY, new TryCatchParselet());
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
        infix(IS, Precedence.EQUALITY);
        registerInfix(OPEN_PAREN, new CallParselet());
        registerInfix(EQ, new AssignmentParselet());
        registerInfix(OPEN_BRACKET, new IndexParselet());
        registerInfix(DOT, new MemberAccessParselet());
        registerInfix(ARROW, new MiniFunctionParselet());
        registerInfix(PLUS_EQ, new BinaryMutatorParselet(Token.Type.PLUS));
        registerInfix(MINUS_EQ, new BinaryMutatorParselet(Token.Type.MINUS));
        registerInfix(TIMES_EQ, new BinaryMutatorParselet(Token.Type.TIMES));
        registerInfix(DIVIDE_EQ, new BinaryMutatorParselet(Token.Type.DIVIDE));
        registerInfix(COLON, new TypePatternParselet());
        registerInfix(MATCH, new MatchParselet());
        postfix(BANG);
    }

    /**
     * Parses one expression from the input and returns it.
     *
     * @param token      The token input to parse.
     * @param precedence The starting precedence.
     * @return An expression, or null if the end of the stream is reached.
     */
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

    /**
     * Parses one expression from the input and returns it.
     *
     * @param token The token input to parse.
     * @return An expression, or null if the end of the stream is reached.
     */
    @Override
    public Expr next(Token token) {
        return next(token, 0);
    }

    /**
     * Parses one expression from the input and returns it.
     *
     * @param precedence The starting precedence.
     * @return An expression, or null if the end of the stream is reached.
     */
    public Expr next(int precedence) {
        return next(read(), precedence);
    }

    /**
     * Parses one expression from the input and returns it.
     *
     * @return An expression, or null if the end of the stream is reached.
     */
    public Expr next() {
        return next(read());
    }

    /**
     * Returns the precedence of the current token.
     */
    private int getPrecedence() {
        Token peeked = peek();
        if (peeked == null)
            return 0;

        InfixParselet parser = infixParselets.get(peeked.getType());
        if (parser != null) return parser.getPrecedence();

        return 0;
    }

    /**
     * Peeks at the next token in the stream and returns it. Does not consume any tokens.
     */
    public Token peek() {
        if (peeked != null)
            return peeked;
        return peeked = read();
    }

    /**
     * Registers a prefix parselet for a token type.
     *
     * @param token    The token type.
     * @param parselet The parselet.
     */
    public void registerPrefix(Token.Type token, PrefixParselet parselet) {
        prefixParselets.put(token, parselet);
    }

    /**
     * Registers a prefix operator parselet for a token type.
     *
     * @param token The token type.
     */
    public void prefix(Token.Type token) {
        registerPrefix(token, new PrefixOperatorParselet());
    }

    /**
     * Registers a prefix operator parselet for some token types.
     *
     * @param tokens The token types.
     */
    public void prefix(Token.Type... tokens) {
        for (Token.Type token : tokens) {
            prefix(token);
        }
    }

    /**
     * Registers an infix parselet for a token type.
     *
     * @param token    The token type.
     * @param parselet The parselet.
     */
    public void registerInfix(Token.Type token, InfixParselet parselet) {
        infixParselets.put(token, parselet);
    }

    /**
     * Registers an infix operator parselet for a token type.
     *
     * @param token      The token type.
     * @param precedence The precedence of the operator.
     */
    public void infix(Token.Type token, int precedence) {
        registerInfix(token, new BinaryOperatorParselet(precedence));
    }

    /**
     * Registers a postfix operator parselet for a token type.
     *
     * @param token The token type.
     */
    public void postfix(Token.Type token) {
        registerInfix(token, new PostfixOperatorParselet());
    }

    /**
     * Registers a postfix operator parselet for some token types.
     *
     * @param tokens The token types.
     */
    public void postfix(Token.Type... tokens) {
        for (Token.Type token : tokens) {
            postfix(token);
        }
    }

    /**
     * Reads a single token from the input (or, if a token was peeked, that token) and returns it.
     *
     * @return The read token, or null if the end of the input has been reached.
     */
    public Token read() {
        if (peeked != null) {
            Token peeked = this.peeked;
            this.peeked = null;
            return peeked;
        } else {
            return lexer.next(null);
        }
    }

    /**
     * Reads a single token. If its type is not equal to the given type, throws an exception.
     *
     * @param type The type to compare.
     * @throws me.abje.lingua.parser.ParseException If the type of the read token is different from the given type.
     */
    public void expect(Token.Type type) {
        if (peek() == null || !read().is(type)) {
            throw new ParseException("expected " + type);
        }
    }

    /**
     * Skips over as many {@link me.abje.lingua.lexer.Token.Type#LINE} tokens as possible.
     */
    public void eatLines() {
        while (peek() != null && peek().is(LINE)) {
            read();
        }
    }

    /**
     * Peeks a single token. If its type is equal to the given type, consumes it and returns true.
     * Otherwise, returns false.
     *
     * @param type The type to compare.
     * @return True if the peeked token has type <code>type</code>.
     */
    public boolean match(Token.Type type) {
        if (peek() != null && peek().is(type)) {
            read();
            return true;
        } else {
            return false;
        }
    }
}
