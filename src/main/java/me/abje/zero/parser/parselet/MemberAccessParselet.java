package me.abje.zero.parser.parselet;

import me.abje.zero.lexer.Token;
import me.abje.zero.parser.ParseException;
import me.abje.zero.parser.Parser;
import me.abje.zero.parser.Precedence;
import me.abje.zero.parser.expr.Expr;
import me.abje.zero.parser.expr.MemberAccessExpr;

public class MemberAccessParselet implements InfixParselet {
    @Override
    public Expr parse(Parser parser, Expr left, Token token) {
        Token name = parser.read();
        if (name.is(Token.Type.NAME)) {
            return new MemberAccessExpr(left, name.getValue());
        } else {
            throw new ParseException("member not a name");
        }
    }

    @Override
    public int getPrecedence() {
        return Precedence.CALL;
    }
}
