package me.abje.zero.parser.parselet;

import me.abje.zero.lexer.Token;
import me.abje.zero.parser.Parser;
import me.abje.zero.parser.Precedence;
import me.abje.zero.parser.expr.Expr;
import me.abje.zero.parser.expr.PostfixExpr;

public class PostfixOperatorParselet implements InfixParselet {
    public Expr parse(Parser parser, Expr left, Token token) {
        return new PostfixExpr(left, token.getType());
    }

    @Override
    public int getPrecedence() {
        return Precedence.PREFIX;
    }
}
