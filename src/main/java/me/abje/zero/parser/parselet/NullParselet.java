package me.abje.zero.parser.parselet;

import me.abje.zero.lexer.Token;
import me.abje.zero.parser.Parser;
import me.abje.zero.parser.expr.Expr;
import me.abje.zero.parser.expr.NullExpr;

public class NullParselet implements PrefixParselet {
    @Override
    public Expr parse(Parser parser, Token token) {
        return new NullExpr();
    }
}
