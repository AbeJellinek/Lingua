package me.abje.zero.interpreter.obj;

import me.abje.zero.parser.expr.AssignmentExpr;
import me.abje.zero.parser.expr.Expr;

/**
 * Represents a field in a class.
 */
public class Field {
    /**
     * The field's type. Currently unused.
     */
    private final ClassObj type;

    /**
     * The field's name.
     */
    private final String name;

    /**
     * The field's default value, computed when an instance is created.
     */
    private final Expr defaultValue;

    /**
     * Creates a new field.
     *
     * @param type         The field's type.
     * @param name         The field's name.
     * @param defaultValue The field's uncomputed default value.
     */
    public Field(ClassObj type, String name, Expr defaultValue) {
        this.type = type;
        this.name = name;
        this.defaultValue = defaultValue;
    }

    /**
     * Creates a new field based on an assignment expression.
     *
     * @param expr The expression.
     */
    public Field(AssignmentExpr expr) {
        this(null, expr.getName(), expr.getValue());
    }

    /**
     * Returns this field's type.
     */
    public ClassObj getType() {
        return type;
    }

    /**
     * Returns this field's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns this field's default value.
     */
    public Expr getDefaultValue() {
        return defaultValue;
    }
}
