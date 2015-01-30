/*
 * Copyright (c) 2015 Abe Jellinek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

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
