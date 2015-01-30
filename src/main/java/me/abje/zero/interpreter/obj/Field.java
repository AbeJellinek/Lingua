package me.abje.zero.interpreter.obj;

import me.abje.zero.parser.expr.AssignmentExpr;
import me.abje.zero.parser.expr.Expr;

public class Field {
    private final ClassObj type;
    private final String name;
    private final Expr defaultValue;

    public Field(ClassObj type, String name, Expr defaultValue) {
        this.type = type;
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public Field(AssignmentExpr expr) {
        this(null, expr.getName(), expr.getValue());
    }

    public ClassObj getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Expr getDefaultValue() {
        return defaultValue;
    }
}
