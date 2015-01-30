package me.abje.zero.interpreter;

/**
 * An exception thrown at runtime by the interpreter.
 */
public class InterpreterException extends RuntimeException {
    public InterpreterException(String message) {
        super(message);
    }
}
