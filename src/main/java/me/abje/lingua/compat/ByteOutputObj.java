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

import com.google.common.io.ByteStreams;
import me.abje.lingua.interpreter.Bridge;
import me.abje.lingua.interpreter.InterpreterException;
import me.abje.lingua.interpreter.obj.*;

import java.io.*;

public class ByteOutputObj extends Obj {
    public static ClassObj SYNTHETIC = bridgeClass(ByteOutputObj.class);
    private final OutputStream out;

    public ByteOutputObj(OutputStream out) {
        super(SYNTHETIC);
        this.out = out;
    }

    @Bridge
    public static ByteOutputObj init(Obj o) {
        if (o instanceof FileObj) {
            try {
                return new ByteOutputObj(new FileOutputStream(((FileObj) o).getFile()));
            } catch (FileNotFoundException e) {
                throw new InterpreterException("IOException", e.getMessage());
            }
        } else {
            throw new InterpreterException("CallException", "invalid argument for function init");
        }
    }

    @Bridge
    public Obj write(NumberObj num) {
        try {
            out.write((int) num.getValue());
        } catch (IOException e) {
            throw new InterpreterException("IOException", e.getMessage());
        }
        return NullObj.NULL;
    }

    @Bridge
    public NumberObj writeAll(ListObj all) {
        try {
            return NumberObj.of(ByteStreams.copy(new ByteArrayInputStream(fromListObj(all)), out));
        } catch (IOException e) {
            throw new InterpreterException("IOException", e.getMessage());
        }
    }

    @Bridge
    public Obj flush() {
        try {
            out.flush();
            return NullObj.NULL;
        } catch (IOException e) {
            throw new InterpreterException("IOException", e.getMessage());
        }
    }

    @Bridge
    public Obj close() {
        try {
            out.close();
            return NullObj.NULL;
        } catch (IOException e) {
            throw new InterpreterException("IOException", e.getMessage());
        }
    }

    private byte[] fromListObj(ListObj list) {
        try {
            byte[] bytes = new byte[list.size()];
            for (int i = 0; i < list.size(); i++) {
                bytes[i] = (byte) ((NumberObj) list.get(i)).getValue();
            }
            return bytes;
        } catch (ClassCastException e) {
            throw new InterpreterException("IOException", "non-byte value passed to writeAll");
        }
    }
}
