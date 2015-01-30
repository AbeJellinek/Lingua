package me.abje.zero.lexer;

import me.abje.zero.Phase;
import me.abje.zero.parser.ParseException;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;

import static me.abje.zero.lexer.Token.Type.*;

public class Lexer implements Phase<Void, Token> {
    private PushbackReader reader;
    private StringBuilder builder;
    private int line = 1, column = 1;
    private int realLine = 1, realColumn = 1;
    private int oldColumn;

    public Lexer(Reader reader) {
        this.reader = new PushbackReader(reader, 1);
        this.builder = new StringBuilder();
    }

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
                    return make(PLUS);
                case '-':
                    if (isNext('>'))
                        return make(ARROW);
                    else
                        return make(MINUS);
                case '*':
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
                        return null;
                    }
            }
        } catch (EOSException e) {
            return null;
        }
    }

    private Token readString() {
        StringBuilder stringBuilder = new StringBuilder();
        boolean escape = false;
        boolean cont = true;
        while (cont) {
            char c = read();
            if (escape) {
                stringBuilder.append(c);
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
            default:
                return make(NAME);
        }
    }

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

    private Token make(Token.Type type) {
        Token token = new Token(type, builder.toString(), realLine, realColumn);
        builder.setLength(0);
        return token;
    }
}
