package me.abje.zero.interpreter;

import me.abje.zero.interpreter.obj.Obj;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class Environment {
    private Frame globals = new Frame();
    private Deque<Frame> stack = new ArrayDeque<>();

    public Environment() {
        new Intrinsics(globals).register();
        stack.push(globals);
    }

    public Obj define(String name, Obj value) {
        return stack.peek().define(name, value);
    }

    public Obj update(String name, Obj value) {
        return stack.peek().update(name, value);
    }

    public boolean has(String name) {
        for (Frame frame : stack) {
            if (frame.has(name)) {
                return true;
            }
        }

        return false;
    }

    public Obj get(String name) {
        for (Frame frame : stack) {
            if (frame.has(name)) {
                return frame.get(name);
            }
        }

        throw new InterpreterException("variable '" + name + "' is not defined in this context");
    }

    public void put(String name, Obj value) {
        for (Frame frame : stack) {
            if (frame.has(name)) {
                frame.update(name, value);
                return;
            }
        }

        stack.peek().define(name, value);
    }

    public void pushFrame() {
        stack.push(new Frame());
    }

    public void popFrame() {
        if (stack.size() > 1) {
            stack.pop();
        }
    }

    public static class Frame {
        private Map<String, Obj> locals = new HashMap<>();

        public Obj define(String name, Obj value) {
            if (locals.containsKey(name)) {
                throw new InterpreterException("variable '" + name + "' is already defined in this context");
            } else {
                locals.put(name, value);
                return value;
            }
        }

        public Obj update(String name, Obj value) {
            if (!locals.containsKey(name)) {
                throw new InterpreterException("variable '" + name + "' is not defined in this context");
            } else {
                locals.put(name, value);
                return value;
            }
        }

        public boolean has(String name) {
            return locals.containsKey(name);
        }

        public Obj get(String name) {
            if (!locals.containsKey(name)) {
                throw new InterpreterException("variable '" + name + "' is not defined in this context");
            } else {
                return locals.get(name);
            }
        }
    }
}
