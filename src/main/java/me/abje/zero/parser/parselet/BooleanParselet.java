package me.abje.zero.parser.parselet;

import me.abje.zero.lexer.Token;
import me.abje.zero.parser.Parser;
import me.abje.zero.parser.expr.BooleanExpr;
import me.abje.zero.parser.expr.Expr;

public class BooleanParselet implements PrefixParselet {
    @Override
    public Expr parse(Parser parser, Token token) {
        return new BooleanExpr(token.is(Token.Type.TRUE));
    }
}
