package me.abje.zero.interpreter.obj;

import me.abje.zero.interpreter.Intrinsics;

import java.util.stream.Collectors;

public class StringObj extends Obj {
    private String value;

    public StringObj(String value) {
        super(SYNTHETIC);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StringObj stringObj = (StringObj) o;

        return value.equals(stringObj.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }

    public static final ClassObj SYNTHETIC = ClassObj.builder("String").
            withFunction("init", (interpreter, args) ->
                    new StringObj(args.stream().map(Object::toString).collect(Collectors.joining("")))).
            build();

    static {
        Intrinsics.registerClass(SYNTHETIC);
    }
}
