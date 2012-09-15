/**
 * Copyright 2010-2012 by PHP-maven.org
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.phpmaven.exec;

/**
 * Abstract exception thrown by the php execution if something went wrong.
 * 
 * <p>
 * This is the base exception for all possible failures during php execution.
 * </p>
 *
 * @author Tobias Sarnowski
 * @since 2.0.0
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
