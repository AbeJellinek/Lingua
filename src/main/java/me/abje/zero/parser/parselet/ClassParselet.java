package me.abje.zero.parser.parselet;

import me.abje.zero.interpreter.obj.Field;
import me.abje.zero.lexer.Token;
import me.abje.zero.parser.ParseException;
import me.abje.zero.parser.Parser;
import me.abje.zero.parser.expr.*;

import java.util.ArrayList;
import java.util.List;

public class ClassParselet implements PrefixParselet {
    @Override
    public Expr parse(Parser parser, Token token) {
        Token name = parser.read();
        if (!name.is(Token.Type.NAME))
            throw new ParseException("invalid class name");
        parser.eatLines();
        parser.expect(Token.Type.OPEN_BRACE);
        List<Field> fields = new ArrayList<>();
        List<FunctionExpr> functions = new ArrayList<>();
        while (!parser.peek().is(Token.Type.CLOSE_BRACE)) {
            Expr expr = parser.next();
            if (expr instanceof FunctionExpr) {
                functions.add((FunctionExpr) expr);
            } else if (expr instanceof AssignmentExpr) {
                fields.add(new Field((AssignmentExpr) expr));
            } else if (expr instanceof NameExpr) {
                fields.add(new Field(null, ((NameExpr) expr).getValue(), new NullExpr()));
            } else {
                throw new ParseException("invalid class member");
            }
            parser.eatLines();
        }
        parser.read();
        return new ClassExpr(name.getValue(), functions, fields);
    }
}
