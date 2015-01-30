package me.abje.zero.interpreter;

import me.abje.zero.Phase;
import me.abje.zero.interpreter.obj.Obj;
import me.abje.zero.lexer.Lexer;
import me.abje.zero.lexer.Morpher;
import me.abje.zero.parser.ParseException;
import me.abje.zero.parser.Parser;
import me.abje.zero.parser.expr.Expr;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class Interpreter implements Phase<Expr, Obj> {
    private Environment env = new Environment();

    public Obj next(Expr expr) {
        return expr.evaluate(this);
    }

    public static void main(String[] args) throws FileNotFoundException {
        try {
            Parser parser = new Parser(new Morpher(new Lexer(new FileReader("test.txt"))));
            Interpreter interpreter = new Interpreter();
            Expr expr;
            while ((expr = parser.next()) != null) {
                interpreter.next(expr);
            }
        } catch (ParseException | InterpreterException e) {
            e.printStackTrace();
        }
    }

    public Environment getEnv() {
        return env;
    }
}
