package me.abje.zero.parser.parselet;

import me.abje.zero.lexer.Token;
import me.abje.zero.parser.expr.Expr;
import me.abje.zero.parser.Parser;

public interface PrefixParselet {
    public Expr parse(Parser parser, Token token);
}
