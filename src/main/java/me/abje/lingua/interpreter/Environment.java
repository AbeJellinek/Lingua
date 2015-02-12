/*
 * Copyright (c) 2015 Abe Jellinek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package me.abje.lingua.interpreter;

import me.abje.lingua.interpreter.obj.Obj;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * A stack of {@link me.abje.lingua.interpreter.Environment.Frame Frames} that store a map of variable names to values.
 */
public class Environment {
    /**
     * This Environment's global frame. Holds variables accessible from anywhere.
     * If you pollute the global scope, you're a bad person.
     */
    private Frame globals = new Frame("<main>");

    /**
     * The stack that holds this Environment's frames.
     */
    private Deque<Frame> stack = new ArrayDeque<>();
    private Deque<Frame> oldStack;

    /**
     * Creates a new, empty Environment.
     */
    public Environment() {
        stack.push(globals);
    }

    /**
     * Defines a new variable in the top frame.
     *
     * @param name  The variable's name.
     * @param value The variable's value.
     * @return The variable's value.
     * @throws me.abje.lingua.interpreter.InterpreterException If a variable with that name already exists in the top frame.
     */
    public Obj define(String name, Obj value) {
        return stack.peek().define(name, value);
    }

    /**
     * Updates a variable in the top frame.
     *
     * @param name  The variable's name.
     * @param value The variable's new value.
     * @return The variable's new value.
     * @throws me.abje.lingua.interpreter.InterpreterException If a variable with that name doesn't exist in the top frame.
     */
    public Obj update(String name, Obj value) {
        return stack.peek().update(name, value);
    }

    /**
     * Returns whether a variable with the given name exists in the stack.
     *
     * @param name The variable's name.
     * @return Whether such a variable exists.
     */
    public boolean has(String name) {
        for (Frame frame : stack) {
            if (frame.has(name)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets the value of the variable with the given name in the stack.
     * Searches top-down -- narrower scopes are searched first.
     *
     * @param name The variable's name.
     * @return The variable's value.
     * @throws me.abje.lingua.interpreter.InterpreterException If a variable with that name doesn't exist in the stack.
     */
    public Obj get(String name) {
        for (Frame frame : stack) {
            if (frame.has(name)) {
                return frame.get(name);
            }
        }

        throw new InterpreterException("UndefinedException", "variable '" + name + "' is not defined in this context");
    }

    /**
     * Updates or creates a variable with the given name, and sets its value to the given value.
     * <p>
     * First searches the stack for a variable with the given name, top-down -- narrower scopes are searched first.
     * If a variable is found, updates its value.
     * If a variable is not found, creates a new variable in the narrowest scope with the given name and value.
     *
     * @param name  The variable's name.
     * @param value The variable's value.
     */
    public void put(String name, Obj value) {
        for (Frame frame : stack) {
            if (frame.has(name)) {
                frame.update(name, value);
                return;
            }
        }

        stack.peek().define(name, value);
    }

    /**
     * Pushes a new frame onto the top of the stack.
     */
    public void pushFrame(String name) {
        Frame frame = new Frame(name);
        Frame top = stack.peek();
        frame.fileName = top.fileName;
        frame.line = top.line;
        stack.push(frame);
    }

    /**
     * Pops the topmost frame from the stack.
     */
    public void popFrame() {
        if (stack.size() > 1) {
            stack.pop();
        }
    }

    /**
     * Returns this Environment's global frame.
     */
    public Frame getGlobals() {
        return globals;
    }

    public Deque<Frame> getStack() {
        return stack;
    }

    public void setStack(Deque<Frame> stack) {
        this.stack = stack;
    }

    public void setOldStack(Deque<Frame> oldStack) {
        this.oldStack = oldStack;
    }

    public Deque<Frame> getOldStack() {
        return oldStack;
    }

    /**
     * A stack frame. Holds local variables in a map.
     */
    public static class Frame {
        /**
         * This Frame's name.
         */
        private String name;

        /**
         * This Frame's local variables.
         */
        private Map<String, Obj> locals = new HashMap<>();

        /**
         * This Frame's current line.
         */
        private int line;

        /**
         * This Frame's current file name.
         */
        private String fileName;

        public Frame(String name) {
            this.name = name;
        }

        /**
         * Defines a new variable in this Frame.
         *
         * @param name  The new variable's name.
         * @param value The new variable's value.
         * @return The new variable's value.
         * @throws me.abje.lingua.interpreter.InterpreterException If a variable with the given name already exists.
         */
        public Obj define(String name, Obj value) {
            if (locals.containsKey(name)) {
                throw new InterpreterException("InvalidOperationException", "variable '" + name + "' is already defined in this context");
            } else {
                locals.put(name, value);
                return value;
            }
        }

        /**
         * Updates the value of a variable in this Frame.
         *
         * @param name  The variable's name.
         * @param value The variable's new value.
         * @return The variable's new value.
         * @throws me.abje.lingua.interpreter.InterpreterException If a variable with the given name does not exist.
         */
        public Obj update(String name, Obj value) {
            if (!locals.containsKey(name)) {
                throw new InterpreterException("UndefinedException", "variable '" + name + "' is not defined in this context");
            } else {
                locals.put(name, value);
                return value;
            }
        }

        /**
         * Returns whether a variable with the given name exists in this Frame.
         *
         * @param name The name to find.
         */
        public boolean has(String name) {
            return locals.containsKey(name);
        }

        /**
         * Returns the value of the variable with the given name in this Frame.
         *
         * @param name The variable's name.
         * @return The variable's value.
         * @throws me.abje.lingua.interpreter.InterpreterException If a variable with the given name does not exist.
         */
        public Obj get(String name) {
            if (!locals.containsKey(name)) {
                throw new InterpreterException("UndefinedException", "variable '" + name + "' is not defined in this context");
            } else {
                return locals.get(name);
            }
        }

        public String getName() {
            return name;
        }

        public int getLine() {
            return line;
        }

        public void setLine(int line) {
            this.line = line;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }
    }
}
