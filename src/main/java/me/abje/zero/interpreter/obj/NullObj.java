package me.abje.zero.interpreter.obj;

public class NullObj extends Obj {
    private static final NullObj self = new NullObj();

    private NullObj() {
    }

    public static NullObj get() {
        return self;
    }

    @Override
    public String toString() {
        return "null";
    }
}
