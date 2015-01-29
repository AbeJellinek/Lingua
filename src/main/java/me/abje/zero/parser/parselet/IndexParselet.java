package me.abje.zero.parser.parselet;

import me.abje.zero.lexer.Token;
import me.abje.zero.parser.Parser;
import me.abje.zero.parser.Precedence;
import me.abje.zero.parser.expr.Expr;
import me.abje.zero.parser.expr.IndexExpr;

public class IndexParselet implements InfixParselet {
    @Override
    public Expr parse(Parser parser, Expr left, Token token) {
        Expr index = parser.next();
        parser.expect(Token.Type.CLOSE_BRACKET);
        return new IndexExpr(left, index);
    }

    @Override
    public int getPrecedence() {
        return Precedence.CALL;
    }
}
