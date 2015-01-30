package me.abje.zero.interpreter.obj;

/**
 * A synthetic "function." Basically just an object with a self field.
 */
public class SyntheticFunctionObj extends Obj {
    /**
     * The "self" argument to this function.
     */
    private Obj self;

    /**
     * Creates a new synthetic function.
     */
    public SyntheticFunctionObj() {
        super(FunctionObj.SYNTHETIC);
    }

    /**
     * Returns the "self" argument.
     */
    public Obj getSelf() {
        return self;
    }

    /**
     * Sets the "self" argument.
     *
     * @param self The new "self" argument.
     */
    public void setSelf(Obj self) {
        this.self = self;
    }
}
