package me.abje.zero.parser.parselet;

import me.abje.zero.lexer.Token;
import me.abje.zero.parser.Parser;
import me.abje.zero.parser.expr.Expr;
import me.abje.zero.parser.expr.OperatorExpr;

public class BinaryOperatorParselet implements InfixParselet {
    private int precedence;

    public BinaryOperatorParselet(int precedence) {
        this.precedence = precedence;
    }

    public Expr parse(Parser parser, Expr left, Token token) {
        Expr right = parser.next(precedence);
        return new OperatorExpr(left, token.getType(), right);
    }

    @Override
    public int getPrecedence() {
        return precedence;
    }
}
