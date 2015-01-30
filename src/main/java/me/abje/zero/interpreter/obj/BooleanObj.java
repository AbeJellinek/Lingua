package me.abje.zero.interpreter.obj;

/**
 * An object with two possible values: true and false.
 */
public class BooleanObj extends Obj {
    /**
     * The value of this object.
     */
    private boolean value;

    /**
     * Creates a new boolean object with the given value.
     *
     * @param value The value.
     */
    public BooleanObj(boolean value) {
        super(SYNTHETIC);
        this.value = value;
    }

    /**
     * Returns this boolean's value.
     */
    public boolean getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BooleanObj that = (BooleanObj) o;

        return value == that.value;
    }

    @Override
    public int hashCode() {
        return (value ? 1 : 0);
    }

    /**
     * Booleans are truthy depending on their value.
     *
     * @return Whether this boolean is `true`.
     */
    @Override
    public boolean isTruthy() {
        return value;
    }

    public static final ClassObj SYNTHETIC = ClassObj.builder("Boolean").build();
}
