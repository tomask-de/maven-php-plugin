package org.phpmaven.eclipse.core.internal.mvn;

import java.text.DateFormat;
import java.util.Date;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

/**
 * Logging appender to fetch the maven output.
 * 
 * @author Martin Eisengardt
 */
public final class MvnLoggingAppender extends AppenderBase<ILoggingEvent> {
    
    /**
     * The thread local state for fetching logging events
     */
    private static ThreadLocal<FetchHelper> TLS = new ThreadLocal<FetchHelper>();
    
    @Override
    protected void append(ILoggingEvent e) {
        final FetchHelper helper = TLS.get();
        if (helper != null) {
            helper.append(e);
        }
    }
    
    /**
     * Starts the log fetching in this thread
     */
    public static void startFetching() {
        TLS.set(new FetchHelper());
    }
    
    /**
     * Stops the log fetching and returns the results
     * @return fetching results
     */
    public static String stopFetching() {
        final FetchHelper helper = TLS.get();
        TLS.remove();
        if (helper == null) {
            return ""; //$NON-NLS-1$
        }
        return helper.getContents();
    }
    
    /**
     * Helper class to fetch the logging output.
     * 
     * @author Martin Eisengardt
     */
    private static final class FetchHelper {
        
        /**
         * The date format to be used
         */
        private static final DateFormat DATE_FORMAT = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
        
        /**
         * The string buffer holding the logging events.
         */
        private StringBuffer buffer = new StringBuffer();
        
        /**
         * Appends given logging event.
         * @param e event
         */
        public void append(ILoggingEvent e) {
            this.buffer.append("["); //$NON-NLS-1$
            this.buffer.append(e.getLevel().toString());
            this.buffer.append(":"); //$NON-NLS-1$
            this.buffer.append(DATE_FORMAT.format(new Date(e.getTimeStamp())));
            this.buffer.append("] "); //$NON-NLS-1$
            this.buffer.append(e.getFormattedMessage());
            this.buffer.append("\n"); //$NON-NLS-1$
        }
        
        /**
         * Returns the buffer contents.
         * @return Buffer contents
         */
        public String getContents() {
            return this.buffer.toString();
        }
        
    }
    
}