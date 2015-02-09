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

import me.abje.lingua.Phase;
import me.abje.lingua.parser.ParseException;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;

import static me.abje.lingua.lexer.Token.Type.*;

/**
 * The phase which produces tokens from text. Usually the first phase in the pipeline.
 */
public class Lexer implements Phase<Void, Token> {
    /**
     * The reader that code is read from.
     */
    private PushbackReader reader;

    /**
     * The temporary buffer that holds characters as they are read.
     */
    private StringBuilder builder;

    /**
     * The current line, as of the start of the current token.
     */
    private int line = 1;

    /**
     * The current column, as of the start of the current token.
     */
    private int column = 1;

    /**
     * The current line.
     */
    private int realLine = 1;

    /**
     * The current column.
     */
    private int realColumn = 1;

    /**
     * The column before the last read operation.
     */
    private int oldColumn;

    /**
     * Creates a new Lexer with the given input Reader.
     *
     * @param reader The Reader.
     */
    public Lexer(Reader reader) {
        this.reader = new PushbackReader(reader, 1);
        this.builder = new StringBuilder();
    }

    /**
     * Reads a token from the input and returns it.
     *
     * @param unused Null.
     * @return The token that was read, or null if the end of the input was reached.
     */
    public Token next(Void unused) {
        realLine = line;
        realColumn = column;

        try {
            char read = read();
            switch (read) {
                case ';':
                case '\r':
                case '\n':
                    return make(LINE);
                case '\\':
                    return make(LINE_CONTINUATION);
                case '.':
                    return make(DOT);
                case '+':
                    if (isNext('='))
                        return make(PLUS_EQ);
                    else if (isNext('+'))
                        return make(PLUSPLUS);
                    else
                        return make(PLUS);
                case '-':
                    if (isNext('>'))
                        return make(ARROW);
                    else if (isNext('='))
                        return make(MINUS_EQ);
                    else if (isNext('-'))
                        return make(MINUSMINUS);
                    else
                        return make(MINUS);
                case '*':
                    if (isNext('='))
                        return make(TIMES_EQ);
                    else
                        return make(TIMES);
                case '/':
                    if (isNext('/')) {
                        try {
                            do {
                                read = read();
                            } while (read != '\n' && read != '\r');
                        } catch (EOSException ignored) {
                        }
                        return make(LINE);
                    } else if (isNext('*')) {
                        try {
                            do {
                                read = read();
                            } while (read != '*' || read() != '/');
                        } catch (EOSException ignored) {
                        }
                        return make(WHITESPACE);
                    } else if (isNext('=')) {
                        return make(DIVIDE_EQ);
                    } else {
                        return make(DIVIDE);
                    }
                case '^':
                    return make(POW);
                case '~':
                    return make(TILDE);
                case '!':
                    if (isNext('='))
                        return make(NEQ);
                    else
                        return make(BANG);
                case '(':
                    return make(OPEN_PAREN);
                case ')':
                    return make(CLOSE_PAREN);
                case ',':
                    return make(COMMA);
                case '=':
                    if (isNext('='))
                        return make(EQEQ);
                    else
                        return make(EQ);
                case '<':
                    if (isNext('='))
                        return make(LTE);
                    else
                        return make(LT);
                case '>':
                    if (isNext('='))
                        return make(GTE);
                    else
                        return make(GT);
                case '&':
                    if (isNext('&'))
                        return make(ANDAND);
                    else
                        return make(AND);
                case '|':
                    if (isNext('|'))
                        return make(OROR);
                    else
                        return make(OR);
                case '"':
                    return readString();
                case '{':
                    return make(OPEN_BRACE);
                case '}':
                    return make(CLOSE_BRACE);
                case '[':
                    return make(OPEN_BRACKET);
                case ']':
                    return make(CLOSE_BRACKET);
                case '@':
                    builder.setLength(0);
                    Token token = next(null);
                    if (token.is(NAME)) {
                        return new Token(ANNOTATION, token.getValue(), token.getLine(), token.getColumn());
                    } else {
                        throw new ParseException("invalid annotation");
                    }
                default:
                    if (Character.isWhitespace(read)) {
                        return make(WHITESPACE);
                    } else if (Character.isLetter(read)) {
                        boolean isEOS = false;

                        do {
                            try {
                                read = read();
                            } catch (EOSException e) {
                                isEOS = true;
                                break;
                            }
                        } while (Character.isLetterOrDigit(read));

                        if (!isEOS)
                            unread(read);
                        return makeName();
                    } else if (Character.isDigit(read)) {
                        boolean isEOS = false;

                        do {
                            try {
                                read = read();
                            } catch (EOSException e) {
                                isEOS = true;
                                break;
                            }
                        } while (read == '.' || Character.isDigit(read));

                        if (!isEOS)
                            unread(read);
                        return make(NUMBER);
                    } else {
                        throw new ParseException("unexpected character: " + read);
                    }
            }
        } catch (EOSException e) {
            return null;
        }
    }

    /**
     * Reads a quote-delimited string from the input.
     *
     * @return The Token that was read.
     */
    private Token readString() {
        StringBuilder stringBuilder = new StringBuilder();
        boolean escape = false;
        boolean cont = true;
        while (cont) {
            char c = read();
            if (escape) {
                switch (c) {
                    case '\'':
                    case '"':
                    case '\\':
                        stringBuilder.append(c);
                        break;
                    case 'n':
                        stringBuilder.append('\n');
                        break;
                    case 'r':
                        stringBuilder.append('\r');
                        break;
                    case 'f':
                        stringBuilder.append('\f');
                        break;
                    case 't':
                        stringBuilder.append('\t');
                        break;
                    case '0':
                        stringBuilder.append('\0');
                        break;
                    default:
                        throw new ParseException("invalid escape code in literal");
                }
                escape = false;
            } else {
                if (c == '\\') {
                    escape = true;
                } else if (c == '"') {
                    cont = false;
                } else {
                    stringBuilder.append(c);
                }
            }
        }
        builder.setLength(0);
        builder.append(stringBuilder);
        return make(STRING);
    }

    /**
     * Reads one character.
     * If it is the given character, returns true.
     * Otherwise, unreads it and returns false.
     *
     * @param c The character.
     * @return True if the next input character equals <code>c</code>, false otherwise.
     */
    private boolean isNext(char c) {
        try {
            char read = read();
            if (read == c) {
                return true;
            } else {
                unread(read);
                return false;
            }
        } catch (EOSException e) {
            return false;
        }
    }

    /**
     * Creates a token from the current buffer.
     * If the buffer is a reserved keyword, makes that keyword's token.
     * Otherwise, makes a {@link me.abje.lingua.lexer.Token.Type#NAME} token.
     *
     * @return The Token.
     */
    private Token makeName() {
        switch (builder.toString()) {
            case "true":
                return make(TRUE);
            case "false":
                return make(FALSE);
            case "if":
                return make(IF);
            case "else":
                return make(ELSE);
            case "while":
                return make(WHILE);
            case "do":
                return make(DO);
            case "null":
                return make(NULL);
            case "class":
                return make(CLASS);
            case "is":
                return make(IS);
            default:
                return make(NAME);
        }
    }

    /**
     * Reads a single character from the input, appends it to the buffer, and returns it.
     *
     * @return The character.
     */
    private char read() {
        try {
            int read = reader.read();
            if (read == -1)
                throw new EOSException();
            char readChar = (char) read;
            oldColumn = column;
            if (readChar == '\n' || readChar == '\r') {
                column = 1;
                line++;
            } else {
                column++;
            }
            builder.append(readChar);
            return readChar;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Removes a single character from the end of the buffer and adds it to the end of the input.
     *
     * @param read The character to unread.
     */
    private void unread(char read) {
        try {
            if (read == '\n' || read == '\r') {
                column = oldColumn;
                line--;
            } else {
                column--;
            }

            reader.unread(read);
            builder.setLength(builder.length() - 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a Token with the given type, and with the current buffer as its value.
     *
     * @param type The Token's type.
     * @return The new Token.
     */
    private Token make(Token.Type type) {
        Token token = new Token(type, builder.toString(), realLine, realColumn);
        builder.setLength(0);
        return token;
    }

    public Token next() {
        return next(null);
    }
}
