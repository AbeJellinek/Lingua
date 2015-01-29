package me.abje.zero.parser.parselet;

import me.abje.zero.lexer.Token;
import me.abje.zero.parser.Parser;
import me.abje.zero.parser.expr.Expr;

public class ParenthesesParselet implements PrefixParselet {
    @Override
    public Expr parse(Parser parser, Token token) {
        Expr expr = parser.next();
        parser.expect(Token.Type.CLOSE_PAREN);
        return expr;
    }
}
