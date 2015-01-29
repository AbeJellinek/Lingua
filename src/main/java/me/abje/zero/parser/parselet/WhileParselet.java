package me.abje.zero.parser.parselet;

import me.abje.zero.lexer.Token;
import me.abje.zero.parser.Parser;
import me.abje.zero.parser.expr.BlockExpr;
import me.abje.zero.parser.expr.Expr;
import me.abje.zero.parser.expr.WhileExpr;

import java.util.ArrayList;
import java.util.List;

public class WhileParselet implements PrefixParselet {
    private boolean doWhile;

    public WhileParselet(boolean doWhile) {
        this.doWhile = doWhile;
    }

    @Override
    public Expr parse(Parser parser, Token token) {
        Expr condition;
        Expr body;
        if (doWhile) {
            List<Expr> exprs = new ArrayList<>();
            while (parser.peek() != null && !parser.peek().is(Token.Type.WHILE)) {
                exprs.add(parser.next());
            }
            parser.expect(Token.Type.WHILE);
            condition = parser.next();
            body = new BlockExpr(exprs);
        } else {
            condition = parser.next();
            body = parser.next();
        }
        return new WhileExpr(condition, body);
    }
}
