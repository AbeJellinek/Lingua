package me.abje.zero.lexer;

import me.abje.zero.Phase;

/**
 * Converts a raw token sequence to a stream of just the meaningful ones.
 * Strips out comments and whitespace, and handles eliding newlines where
 * appropriate.
 * <p>
 * (Regarding the name: the Lexer generates "lexemes". This generates
 * "morphemes", the tokens that have semantic meaning.)
 */
public class Morpher implements Phase<Void, Token> {

    public Morpher(Lexer tokens) {
        mTokens = tokens;

        // Consume any leading newlines.
        mEatLines = true;
    }

    public Token next(Void unused) {
        while (true) {
            Token token = mTokens.next(null);

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

    private final Lexer mTokens;
    private boolean mEatLines;
}
