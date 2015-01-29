package me.abje.zero.parser.parselet;

import me.abje.zero.lexer.Token;
import me.abje.zero.parser.Parser;
import me.abje.zero.parser.expr.Expr;

public interface InfixParselet {
    public Expr parse(Parser parser, Expr left, Token token);

    public int getPrecedence();
}
