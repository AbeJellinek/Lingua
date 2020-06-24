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

import me.abje.lingua.interpreter.Bridge;
import me.abje.lingua.interpreter.obj.*;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

public class FileObj extends Obj {
    public static final ClassObj SYNTHETIC = bridgeClass(FileObj.class);
    private final File file;

    public FileObj(File file) {
        super(SYNTHETIC);
        this.file = file;
    }

    @Bridge
    public static FileObj init(StringObj path) {
        return new FileObj(new File(path.getValue()));
    }

    @Bridge
    public static FileObj init(FileObj parent, StringObj path) {
        return new FileObj(new File(parent.file, path.getValue()));
    }

    @Bridge
    public FileObj parent() {
        return new FileObj(file.getParentFile());
    }

    @Bridge
    public FileObj child(StringObj path) {
        return new FileObj(new File(file, path.getValue()));
    }

    @Bridge
    public StringObj name() {
        return new StringObj(file.getName());
    }

    @Bridge
    public StringObj nameWithoutExtension() {
        return new StringObj(file.getName().replaceFirst("\\.[^.]*$", ""));
    }

    @Bridge
    public StringObj extension() {
        return new StringObj(file.getName().replaceFirst(".*\\.([^.]*)$", "$1"));
    }

    @Bridge
    public StringObj path() {
        return new StringObj(file.getPath());
    }

    @Bridge
    public StringObj absolutePath() {
        return new StringObj(file.getAbsolutePath());
    }

    @Bridge
    public BooleanObj exists() {
        return BooleanObj.of(file.exists());
    }

    @Bridge
    public Obj children() {
        File[] list = file.listFiles();
        if (list != null) {
            return new ListObj(Arrays.stream(list).map(FileObj::new).collect(Collectors.toList()));
        } else {
            return NullObj.get();
        }
    }

    @Bridge
    public BooleanObj isDirectory() {
        return BooleanObj.of(file.isDirectory());
    }

    @Override
    public String toString() {
        return file.toString();
    }

    public File getFile() {
        return file;
    }
}
