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

package me.abje.lingua.interpreter.obj;

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

    public SyntheticFunctionObj(ClassObj type) {
        super(type);
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
