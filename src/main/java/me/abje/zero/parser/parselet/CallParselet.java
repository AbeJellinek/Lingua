package me.abje.zero.parser.parselet;

import me.abje.zero.lexer.Token;
import me.abje.zero.parser.Parser;
import me.abje.zero.parser.Precedence;
import me.abje.zero.parser.expr.CallExpr;
import me.abje.zero.parser.expr.Expr;

import java.util.ArrayList;
import java.util.List;

public class CallParselet implements InfixParselet {
    @Override
    public Expr parse(Parser parser, Expr left, Token token) {
        List<Expr> args = new ArrayList<>();
        while (!parser.peek().is(Token.Type.CLOSE_PAREN)) {
            args.add(parser.next());
            if (parser.peek().is(Token.Type.COMMA)) {
                parser.read();
            }
        }
        parser.read();
        return new CallExpr(left, args);
    }

    @Override
    public int getPrecedence() {
        return Precedence.CALL;
    }
}
