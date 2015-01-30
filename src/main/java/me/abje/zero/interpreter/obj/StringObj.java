package me.abje.zero.interpreter.obj;

import java.util.stream.Collectors;

/**
 * A Zero character string.
 */
public class StringObj extends Obj {
    /**
     * This String's internal value.
     */
    private String value;

    /**
     * Creates a new String with the given value.
     *
     * @param value The value.
     */
    public StringObj(String value) {
        super(SYNTHETIC);
        this.value = value;
    }

    /**
     * Returns this String's value.
     */
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
            withFunction("init", (interpreter, self, args) ->
                    new StringObj(args.stream().map(Object::toString).collect(Collectors.joining("")))).
            build();
}
