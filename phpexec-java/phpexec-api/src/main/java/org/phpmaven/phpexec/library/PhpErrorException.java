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

import java.io.File;


/**
 * Symbolizes an error, printed out by a PHP execution.
 *
 * @author Martin Eisengardt <Martin.Eisengardtgooglemail.com>
 * @since 0.1.0
 */
public class PhpErrorException extends PhpExecutionException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception for the occured error.
     *
     * @param phpFile the PHP file which was involved in the exception
     * @param phpErrorMessage the error message
     */
    public PhpErrorException(File phpFile, String phpErrorMessage) {
        super(phpFile, phpErrorMessage);
    }
}
