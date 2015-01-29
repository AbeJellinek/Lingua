package me.abje.zero.parser.parselet;

import me.abje.zero.lexer.Token;
import me.abje.zero.parser.Parser;
import me.abje.zero.parser.expr.Expr;
import me.abje.zero.parser.expr.ListExpr;

import java.util.ArrayList;
import java.util.List;

public class ListParselet implements PrefixParselet {
    @Override
    public Expr parse(Parser parser, Token token) {
        List<Expr> items = new ArrayList<>();
        while (!parser.peek().is(Token.Type.CLOSE_BRACKET)) {
            items.add(parser.next());
            if (parser.peek().is(Token.Type.COMMA)) {
                parser.read();
            }
        }
        parser.read();
        return new ListExpr(items);
    }
}
