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

package me.abje.lingua.lexer;

/**
 * Converts a raw token sequence to a stream of just the meaningful ones.
 * Strips out comments and whitespace, and handles eliding newlines where
 * appropriate.
 * <p>
 * (Regarding the name: the Lexer generates "lexemes". This generates
 * "morphemes", the tokens that have semantic meaning.)
 *
 * @author Bob Nystrom
 * @author Abe Jellinek
 */
public class Morpher {

    private final Lexer mTokens;
    private boolean mEatLines;

    public Morpher(Lexer tokens) {
        mTokens = tokens;

        // Consume any leading newlines.
        mEatLines = true;
    }

    public Token next() {
        while (true) {
            Token token = mTokens.next();

            if (token == null) {
                return null;
            }

            switch (token.getType()) {
                case WHITESPACE:
                    // Ignore non-semantic tokens.
                    continue;

                    // Ignore lines after tokens that can't end an expression.
                case DOT:
                case PLUS:
                case MINUS:
                case TIMES:
                case DIVIDE:
                case POW:
                case TILDE:
                case OPEN_PAREN:
                    mEatLines = true;
                    break;

                case LINE_CONTINUATION:
                    mEatLines = true;
                    continue;

                case LINE:
                    if (mEatLines) continue;

                    // Collapse multiple lines into one.
                    mEatLines = true;
                    break;

                default:
                    // A line after any other token is significant.
                    mEatLines = false;
                    break;
            }

            return token;
        }
    }
}
