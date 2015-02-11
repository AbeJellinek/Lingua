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
import me.abje.lingua.interpreter.obj.StringObj;

import java.util.Arrays;

/**
 * An exception thrown at runtime by the interpreter.
 */
public class InterpreterException extends RuntimeException {
    private String exceptionClass;
    private Obj exceptionObj;

    public InterpreterException(String exceptionClass, String message) {
        this(new StringObj(message));
        this.exceptionClass = exceptionClass;
    }

    public InterpreterException(String exceptionClass, String message, Interpreter interpreter) {
        this(interpreter.getEnv().get(exceptionClass).call(interpreter, Arrays.asList(new StringObj(message))));
    }

    public InterpreterException(Obj exceptionObj) {
        super(exceptionObj.toString());
        this.exceptionObj = exceptionObj;
    }

    public Obj getExceptionObj() {
        return exceptionObj;
    }

    public void setExceptionObj(Obj exceptionObj) {
        this.exceptionObj = exceptionObj;
    }

    public String getExceptionClass() {
        return exceptionClass;
    }
}
