package me.abje.zero.interpreter.obj;

public class SyntheticFunctionObj extends Obj {
    private Obj self;

    public SyntheticFunctionObj() {
        super(FunctionObj.SYNTHETIC);
    }

    public Obj getSelf() {
        return self;
    }

    public void setSelf(Obj self) {
        this.self = self;
    }
}
