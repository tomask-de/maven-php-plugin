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
 * Something with the basic PHP execution went wrong.
 *
 * @author Tobias Sarnowski
 * @since 2.0.0
 */
public class PhpCoreException extends PhpException {

    /**
     * serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor to create a php exception.
     */
    public PhpCoreException() {
        super();
    }

    /**
     * Constructor to create a php exception with given message.
     * 
     * @param message the detail message.
     */
    public PhpCoreException(String message) {
        super(message);
    }

    /**
     * Constructor to create a php exception with given message and caused by given throwable.
     * 
     * @param message the detail message.
     * @param cause the throwable that caused this exception.
     */
    public PhpCoreException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor to create a php exception caused by given throawble.
     * 
     * @param cause the throwable that caused this exception.
     */
    public PhpCoreException(Throwable cause) {
        super(cause);
    }
}
