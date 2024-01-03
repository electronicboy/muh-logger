/**
 * Copyright (c) 2004-2011 QOS.ch
 * All rights reserved.
 *
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 *
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 *
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package pw.valaria.muhlogger.util;

import org.slf4j.spi.LocationAwareLogger;

import java.util.logging.Level;

// https://github.com/qos-ch/slf4j/blob/master/jul-to-slf4j/src/main/java/org/slf4j/bridge/SLF4JBridgeHandler.java
public class LoggerUtil {

    private static final int TRACE_LEVEL_THRESHOLD = Level.FINEST.intValue();
    private static final int DEBUG_LEVEL_THRESHOLD = Level.FINE.intValue();
    private static final int INFO_LEVEL_THRESHOLD = Level.INFO.intValue();
    private static final int WARN_LEVEL_THRESHOLD = Level.WARNING.intValue();

    public static org.slf4j.event.Level getLevel(Level level) {
        int julLevelValue = level.intValue();
        int slf4jLevel;

        if (julLevelValue <= TRACE_LEVEL_THRESHOLD) {
            slf4jLevel = LocationAwareLogger.TRACE_INT;
        } else if (julLevelValue <= DEBUG_LEVEL_THRESHOLD) {
            slf4jLevel = LocationAwareLogger.DEBUG_INT;
        } else if (julLevelValue <= INFO_LEVEL_THRESHOLD) {
            slf4jLevel = LocationAwareLogger.INFO_INT;
        } else if (julLevelValue <= WARN_LEVEL_THRESHOLD) {
            slf4jLevel = LocationAwareLogger.WARN_INT;
        } else {
            slf4jLevel = LocationAwareLogger.ERROR_INT;
        }
        return org.slf4j.event.Level.intToLevel(slf4jLevel);
    }
}
