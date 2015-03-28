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

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * Filters events below the threshold level.
 * <p>
 * Events with a level below the specified
 * level will be denied, while events with a level
 * equal or above the specified level will trigger a
 * FilterReply.NEUTRAL result, to allow the rest of the
 * filter chain process the event.
 * <p>
 * For more information about filters, please refer to the online manual at
 * http://logback.qos.ch/manual/filters.html#thresholdFilter
 *
 * @author S&eacute;bastien Pennec
 * @author Abe Jellinek
 */
public class ThresholdFilter extends Filter<ILoggingEvent> {

    private Level level;
    private boolean reverse;

    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (!isStarted()) {
            return FilterReply.NEUTRAL;
        }

        if (event.getLevel().isGreaterOrEqual(level)) {
            return reverse ? FilterReply.NEUTRAL : FilterReply.ACCEPT;
        } else {
            return reverse ? FilterReply.ACCEPT : FilterReply.NEUTRAL;
        }
    }

    public void setLevel(String level) {
        this.level = Level.toLevel(level);
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    public void start() {
        if (this.level != null) {
            super.start();
        }
    }
}
