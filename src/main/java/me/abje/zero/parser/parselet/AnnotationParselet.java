package me.abje.zero.parser.parselet;

import me.abje.zero.lexer.Token;
import me.abje.zero.parser.Parser;
import me.abje.zero.parser.expr.Expr;

public class AnnotationParselet implements PrefixParselet {
    @Override
    public Expr parse(Parser parser, Token token) {
        Expr next = parser.next();
        next.getAnnotations().add(token.getValue());
        return next;
    }
}
