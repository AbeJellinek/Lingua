package me.abje.zero.interpreter.obj;

import me.abje.zero.interpreter.Intrinsics;

public class NullObj extends Obj {
    private static final NullObj self = new NullObj();

    private NullObj() {
        super(SYNTHETIC);
    }

    public static NullObj get() {
        return self;
    }

    @Override
    public String toString() {
        return "null";
    }

    @Override
    public boolean isTruthy() {
        return false;
    }

    public static final ClassObj SYNTHETIC = ClassObj.builder("Null").build();
    static {
        Intrinsics.registerClass(SYNTHETIC);
    }
}
