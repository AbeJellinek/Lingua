package me.abje.zero.parser.parselet;

import me.abje.zero.lexer.Token;
import me.abje.zero.parser.Parser;
import me.abje.zero.parser.expr.BlockExpr;
import me.abje.zero.parser.expr.Expr;

import java.util.ArrayList;
import java.util.List;

public class BlockParselet implements PrefixParselet {
    @Override
    public Expr parse(Parser parser, Token token) {
        List<Expr> exprs = new ArrayList<>();
        while (!parser.peek().is(Token.Type.CLOSE_BRACE)) {
            exprs.add(parser.next());
            parser.eatLines();
        }
        parser.read();
        return new BlockExpr(exprs);
    }
}
