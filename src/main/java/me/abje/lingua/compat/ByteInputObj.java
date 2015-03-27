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

package me.abje.lingua.compat;

import com.google.common.base.Charsets;
import me.abje.lingua.interpreter.Bridge;
import me.abje.lingua.interpreter.InterpreterException;
import me.abje.lingua.interpreter.obj.ClassObj;
import me.abje.lingua.interpreter.obj.Obj;
import me.abje.lingua.interpreter.obj.StringObj;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ByteInputObj extends Obj {
    public static ClassObj SYNTHETIC = bridgeClass(ByteInputObj.class);
    private final InputStream in;

    public ByteInputObj(InputStream in) {
        super(SYNTHETIC);
        this.in = in;
    }

    @Bridge
    public static ByteInputObj init(StringObj s) {
        return new ByteInputObj(new ByteArrayInputStream(s.getValue().getBytes(Charsets.UTF_8)));
    }

    @Bridge
    public int next() {
        try {
            return in.read();
        } catch (IOException e) {
            throw new InterpreterException("IOException", e.getMessage());
        }
    }
}
