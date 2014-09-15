/**
 * Copyright 2010-2012 by PHP-maven.org
 *
 * This file is part of phpexec-java.
 *
 * phpexec-java is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * phpexec-java is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with phpexec-java.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.phpmaven.phpexec.library;

/**
 * Abstract exception thrown by the php execution if something went wrong.
 * 
 * <p>
 * This is the base exception for all possible failures during php execution.
 * </p>
 *
 * @author Martin Eisengardt <Martin.Eisengardtgooglemail.com>
 * @since 0.1.0
 */
public abstract class PhpException extends Exception {

    /**
     * serial version uid.
     */
    private static final long serialVersionUID = 1L;
    
    // checkstyle wants exceptions to be immutable but we
    // can not implement that at the moment
    /*CHECKSTYLE:OFF*/
    /**
     * The php output.
     */
    private String phpOutput;
    /*CHECKSTYLE:ON*/

    /**
     * Constructor to create a php exception.
     */
    protected PhpException() {
        super();
    }

    /**
     * Constructor to create a php exception with given message.
     * 
     * @param message the detail message.
     */
    protected PhpException(String message) {
        super(message);
    }

    /**
     * Constructor to create a php exception with given message and caused by given throwable.
     * 
     * @param message the detail message.
     * @param cause the throwable that caused this exception.
     */
    protected PhpException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor to create a php exception caused by given throawble.
     * 
     * @param cause the throwable that caused this exception.
     */
    protected PhpException(Throwable cause) {
        super(cause);
    }

    /**
     * Adds php outputs to the exception.
     *
     * @param output the output string
     */
    public void appendOutput(String output) {
        this.phpOutput = output;
    }

    /**
     * Returns the given output.
     *
     * @return the output string
     */
    public String getAppendedOutput() {
        return phpOutput;
    }

    /**
     * Returns the message and appends the php output.
     * 
     * @return message including php output.
     */
    @Override
    public String getMessage() {
        String message = super.getMessage();
        if (phpOutput != null) {
            message = message + "\n\n" + phpOutput;
        }
        return message;
    }
}
