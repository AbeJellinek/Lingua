package me.abje.zero.parser.parselet;

import me.abje.zero.lexer.Token;
import me.abje.zero.parser.ParseException;
import me.abje.zero.parser.Parser;
import me.abje.zero.parser.Precedence;
import me.abje.zero.parser.expr.*;

import java.util.ArrayList;
import java.util.List;

public class AssignmentParselet implements InfixParselet {
    @Override
    public Expr parse(Parser parser, Expr left, Token token) {
        if (left instanceof CallExpr) {
            CallExpr call = (CallExpr) left;
            List<String> argNames = new ArrayList<>();
            for (Expr arg : call.getArgs()) {
                if (arg instanceof NameExpr) {
                    argNames.add(((NameExpr) arg).getValue());
                } else {
                    throw new ParseException("function definition arguments must be names");
                }
            }

            if (call.getFunc() instanceof NameExpr) {
                Expr value = parser.next(Precedence.ASSIGNMENT - 1);
                return new FunctionExpr(((NameExpr) call.getFunc()).getValue(), argNames, value);
            } else {
                throw new ParseException("function name must actually be a name");
            }
        } else if (left instanceof IndexExpr) {
            IndexExpr index = (IndexExpr) left;
            Expr value = parser.next(Precedence.ASSIGNMENT - 1);
            return new IndexSetExpr(index.getTarget(), index.getIndex(), value);
        } else if (left instanceof MemberAccessExpr) {
            MemberAccessExpr member = (MemberAccessExpr) left;
            Expr value = parser.next(Precedence.ASSIGNMENT - 1);
            return new MemberSetExpr(member.getLeft(), member.getName(), value);
        } else if (left instanceof NameExpr) {
            Expr value = parser.next(Precedence.ASSIGNMENT);
            return new AssignmentExpr(((NameExpr) left).getValue(), value);
        } else {
            throw new ParseException("assignments must have a function call or name as a target");
        }
    }

    @Override
    public int getPrecedence() {
        return Precedence.ASSIGNMENT;
    }
}
