package me.abje.zero.parser.parselet;

import me.abje.zero.lexer.Token;
import me.abje.zero.parser.Parser;
import me.abje.zero.parser.expr.Expr;
import me.abje.zero.parser.expr.IfExpr;

public class IfParselet implements PrefixParselet {
    @Override
    public Expr parse(Parser parser, Token token) {
        Expr condition = parser.next();
        Expr thenBranch = parser.next();
        parser.eatLines();
        Expr elseBranch = null;
        if (parser.match(Token.Type.ELSE))
            elseBranch = parser.next();
        return new IfExpr(condition, thenBranch, elseBranch);
    }
}
