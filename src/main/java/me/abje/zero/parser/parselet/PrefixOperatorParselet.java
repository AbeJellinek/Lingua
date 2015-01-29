package me.abje.zero.parser.parselet;

import me.abje.zero.lexer.Token;
import me.abje.zero.parser.Parser;
import me.abje.zero.parser.Precedence;
import me.abje.zero.parser.expr.Expr;
import me.abje.zero.parser.expr.PrefixExpr;

public class PrefixOperatorParselet implements PrefixParselet {
    public Expr parse(Parser parser, Token token) {
        Expr operand = parser.next(Precedence.PREFIX);
        return new PrefixExpr(token.getType(), operand);
    }
}
