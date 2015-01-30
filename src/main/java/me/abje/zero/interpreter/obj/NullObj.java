package me.abje.zero.interpreter.obj;

/**
 * The Zero "null" singleton. Has no fields, cannot be invoked, and is generally quite useless.
 */
public class NullObj extends Obj {
    /**
     * The singleton instance.
     */
    private static final NullObj self = new NullObj();

    /**
     * Constructs a new Null. This constructor is for private use.
     */
    private NullObj() {
        super(SYNTHETIC);
    }

    /**
     * Returns the Null instance.
     */
    public static NullObj get() {
        return self;
    }

    @Override
    public String toString() {
        return "null";
    }

    /**
     * Null is not truthy.
     *
     * @return False.
     */
    @Override
    public boolean isTruthy() {
        return false;
    }

    public static final ClassObj SYNTHETIC = ClassObj.builder("Null").build();
}
