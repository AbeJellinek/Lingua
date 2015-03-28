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
package me.abje.lingua.util;

import ch.qos.logback.core.OutputStreamAppender;
import ch.qos.logback.core.joran.spi.ConsoleTarget;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.WarnStatus;
import ch.qos.logback.core.util.EnvUtil;
import ch.qos.logback.core.util.OptionHelper;

import java.io.OutputStream;
import java.util.Arrays;

/**
 * ConsoleAppender appends log events to <code>System.out</code> or
 * <code>System.err</code> using a layout specified by the user. The default
 * target is <code>System.out</code>.
 * <p>
 * For more information about this appender, please refer to the online manual
 * at http://logback.qos.ch/manual/appenders.html#ConsoleAppender
 *
 * @author Ceki G&uuml;lc&uuml;
 * @author Tom SH Liu
 * @author Ruediger Dohna
 * @author Abe Jellinek
 */
public class AnsiFixConsoleAppender<E> extends OutputStreamAppender<E> {

    protected ConsoleTarget target = ConsoleTarget.SystemOut;
    protected boolean withJansi = false;

    private final static String WindowsAnsiOutputStream_CLASS_NAME = "org.fusesource.jansi.WindowsAnsiOutputStream";

    /**
     * Sets the value of the <b>Target</b> option. Recognized values are
     * "System.out" and "System.err". Any other value will be ignored.
     */
    public void setTarget(String value) {
        ConsoleTarget t = ConsoleTarget.findByName(value.trim());
        if (t == null) {
            targetWarn(value);
        } else {
            target = t;
        }
    }

    /**
     * Returns the current value of the <b>target</b> property. The default value
     * of the option is "System.out".
     * <p>
     * See also {@link #setTarget}.
     */
    public String getTarget() {
        return target.getName();
    }

    private void targetWarn(String val) {
        Status status = new WarnStatus("[" + val + "] should be one of "
                + Arrays.toString(ConsoleTarget.values()), this);
        status.add(new WarnStatus(
                "Using previously set target, System.out by default.", this));
        addStatus(status);
    }

    @Override
    public void start() {
        OutputStream targetStream = target.getStream();
        // enable jansi only on Windows and only if withJansi set to true
        if (EnvUtil.isWindows() && !isIntelliJ() && withJansi) {
            targetStream = getTargetStreamForWindows(targetStream);
        }
        setOutputStream(targetStream);
        super.start();
    }

    private OutputStream getTargetStreamForWindows(OutputStream targetStream) {
        try {
            addInfo("Enabling JANSI WindowsAnsiOutputStream for the console.");
            Object windowsAnsiOutputStream = OptionHelper.instantiateByClassNameAndParameter(WindowsAnsiOutputStream_CLASS_NAME, Object.class, context,
                    OutputStream.class, targetStream);
            return (OutputStream) windowsAnsiOutputStream;
        } catch (Exception e) {
            addWarn("Failed to create WindowsAnsiOutputStream. Falling back on the default stream.", e);
        }
        return targetStream;
    }

    /**
     * @return
     */
    public boolean isWithJansi() {
        return withJansi;
    }

    /**
     * If true, this appender will output to a stream which
     *
     * @param withJansi Whether to use Jansi.
     * @since 1.0.5
     */
    public void setWithJansi(boolean withJansi) {
        this.withJansi = withJansi;
    }

    private boolean isIntelliJ() {
        try {
            Class.forName("com.intellij.rt.execution.application.AppMain");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
