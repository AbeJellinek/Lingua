package me.abje.zero.parser.parselet;

import me.abje.zero.lexer.Token;
import me.abje.zero.parser.ParseException;
import me.abje.zero.parser.Parser;
import me.abje.zero.parser.Precedence;
import me.abje.zero.parser.expr.Expr;
import me.abje.zero.parser.expr.FunctionExpr;
import me.abje.zero.parser.expr.NameExpr;

import java.util.Arrays;

public class MiniFunctionParselet implements InfixParselet {
    @Override
    public Expr parse(Parser parser, Expr left, Token token) {
        if (left instanceof NameExpr) {
            return new FunctionExpr("<anon>", Arrays.asList(((NameExpr) left).getValue()), parser.next());
        } else {
            throw new ParseException("left side of function must be an argument name");
        }
    }

    @Override
    public int getPrecedence() {
        return Precedence.COMPARISON;
    }
}
