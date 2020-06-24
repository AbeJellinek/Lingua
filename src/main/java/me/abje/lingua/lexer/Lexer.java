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

import me.abje.lingua.parser.ParseException;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;

import static me.abje.lingua.lexer.Token.Type.*;

/**
 * The phase which produces tokens from text. Usually the first phase in the pipeline.
 */
public class Lexer {
    /**
     * The reader that code is read from.
     */
    private final PushbackReader reader;

    /**
     * The temporary buffer that holds characters as they are read.
     */
    private final StringBuilder builder;

    /**
     * The current line, as of the start of the current token.
     */
    private int line = 1;

    private final String fileName;

    /**
     * Creates a new Lexer with the given input Reader.
     *
     * @param reader   The Reader.
     * @param fileName The name of the file to be read.
     */
    public Lexer(Reader reader, String fileName) {
        this.fileName = fileName;
        this.reader = new PushbackReader(reader, 8);
        this.builder = new StringBuilder();
    }

    /**
     * Reads a token from the input and returns it.
     *
     * @return The token that was read, or null if the end of the input was reached.
     */
    public Token next() {
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
                    if (isNext('.')) {
                        if (isNext('.')) {
                            return make(TRIPLE_DOT);
                        } else {
                            return make(DOUBLE_DOT);
                        }
                    } else {
                        return make(DOT);
                    }
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
                    else if (isNext('>'))
                        return make(FAT_ARROW);
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
                case '\'':
                    return readChar();
                case '{':
                    return make(OPEN_BRACE);
                case '}':
                    return make(CLOSE_BRACE);
                case '#':
                    if (isNext('{'))
                        return make(OPEN_MAP_BRACE);
                    else if (isNext('['))
                        return make(OPEN_SET_BRACE);
                    else
                        return make(HASH);
                case '[':
                    return make(OPEN_BRACKET);
                case ']':
                    return make(CLOSE_BRACKET);
                case ':':
                    return make(COLON);
                case '@':
                    builder.setLength(0);
                    Token token = next();
                    if (token != null && token.is(NAME)) {
                        return new Token(ANNOTATION, token.getValue(), token.getLine(), "<none>");
                    } else {
                        throw new ParseException("invalid annotation", fileName, line);
                    }
                case '?':
                    if (isNext('.'))
                        return make(INTERRODOT);
                    else if (isNext(':'))
                        return make(ELVIS);
                    else
                        return make(QUESTION_MARK);
                default:
                    if (Character.isWhitespace(read)) {
                        return make(WHITESPACE);
                    } else if (read == '_' || Character.isLetter(read)) {
                        boolean isEOS = false;

                        do {
                            try {
                                read = read();
                            } catch (EOSException e) {
                                isEOS = true;
                                break;
                            }
                        } while (Character.isLetterOrDigit(read) || read == '_' || read == '$');

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
                        throw new ParseException("unexpected character: " + read, fileName, line);
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
                    case '\'', '"', '\\' -> stringBuilder.append(c);
                    case 'n' -> stringBuilder.append('\n');
                    case 'r' -> stringBuilder.append('\r');
                    case 'f' -> stringBuilder.append('\f');
                    case 't' -> stringBuilder.append('\t');
                    case '0' -> stringBuilder.append('\0');
                    default -> throw new ParseException("invalid escape code in literal", fileName, line);
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
     * Reads a quote-delimited char from the input.
     *
     * @return The Token that was read.
     */
    private Token readChar() {
        char c = read();
        if (c == '\\') {
            c = read();
            switch (c) {
                case '\'':
                case '"':
                case '\\':
                    break;
                case 'n':
                    c = '\n';
                    break;
                case 'r':
                    c = '\r';
                    break;
                case 'f':
                    c = '\f';
                    break;
                case 't':
                    c = '\t';
                    break;
                case '0':
                    c = '\0';
                    break;
                default:
                    throw new ParseException("invalid escape code in literal", fileName, line);
            }
        }
        builder.setLength(0);
        builder.append(c);
        if (!isNext('\''))
            throw new ParseException("unclosed char literal", fileName, line);
        return make(CHAR);
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
        return switch (builder.toString()) {
            case "true" -> make(TRUE);
            case "false" -> make(FALSE);
            case "if" -> make(IF);
            case "else" -> make(ELSE);
            case "while" -> make(WHILE);
            case "do" -> make(DO);
            case "null" -> make(NULL);
            case "class" -> make(CLASS);
            case "is" -> make(IS);
            case "import" -> make(IMPORT);
            case "try" -> make(TRY);
            case "catch" -> make(CATCH);
            case "match" -> make(MATCH);
            case "var" -> make(VAR);
            default -> make(NAME);
        };
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
            if (readChar == '\n' || readChar == '\r') {
                line++;
                if (readChar == '\r') {
                    char next = (char) reader.read();
                    if (next != '\n')
                        reader.unread(next);
                }
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
                line--;
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
        Token token = new Token(type, builder.toString(), line, fileName);
        builder.setLength(0);
        return token;
    }
}
