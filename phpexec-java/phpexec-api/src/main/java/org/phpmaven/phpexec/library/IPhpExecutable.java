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
import java.io.OutputStream;

import org.codehaus.plexus.util.cli.StreamConsumer;

/**
 * An interface representing a php executable.
 * 
 * <p>
 * This interface can be used to invoke php scripts respecting a php
 * configuration.
 * </p>
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 0.1.4
 */
public interface IPhpExecutable {
	
	/**
	 * Sets the silent flag (controls logging).
	 * @param silent true to be silent; useful if passwords are passed to the php executable
	 * @since 0.1.5
	 */
	void setSilent(boolean silent);
    
    /**
     * Executes PHP with the given arguments and returns its output.
     *
     * @param arguments string of arguments for PHP (including the file-path and filename)
     * @param file a hint which file will be processed
     * @return the output string
     * @throws PhpException if the execution failed
     */
    String execute(String arguments, File file) throws PhpException;
    
    /**
     * Executes PHP with the given arguments and returns its output.
     *
     * @param file the php file to be executed
     * @return the output string
     * @throws PhpException if the execution failed
     */
    String execute(File file) throws PhpException;
    
    /**
     * Executes PHP code snippet with the given arguments and returns its output.
     *
     * @param arguments string of arguments for PHP
     * @param code the php code to be executed
     * @return the output string
     * @throws PhpException if the execution failed
     */
    String executeCode(String arguments, String code) throws PhpException;
    
    /**
     * Executes PHP code snippet with the given arguments and returns its output.
     *
     * @param arguments string of arguments for PHP
     * @param code the php code to be executed
     * @param codeArguments Arguments (cli) for the script
     * @return the output string
     * @throws PhpException if the execution failed
     */
    String executeCode(String arguments, String code, String codeArguments) throws PhpException;
    
    /**
     * Executes PHP with the given arguments and throws an IllegalStateException if the
     * execution fails.
     *
     * @param arguments string of arguments for PHP (including the file-path and filename)
     * @param file a hint which file will be processed
     * @param stdout handler for stdout lines
     * @return the returncode of PHP
     * @throws PhpException if the execution failed
     */
    int execute(String arguments, File file, final OutputStream stdout) throws PhpException;
    
    /**
     * Executes PHP with the given arguments.
     *
     * @param arguments string of arguments for PHP (including the file-path and filename)
     * @param stdout handler for stdout lines
     * @param stderr handler for stderr lines
     * @return the return code of PHP
     * @throws PhpException if the executions fails
     */
    int execute(String arguments, OutputStream stdout, OutputStream stderr) throws PhpException;
    
    /**
     * Executes PHP with the given arguments and throws an IllegalStateException if the
     * execution fails.
     *
     * @param arguments string of arguments for PHP (including the file-path and filename)
     * @param file a hint which file will be processed
     * @param stdout handler for stdout lines
     * @return the returncode of PHP
     * @throws PhpException if the execution failed
     * @since 0.1.8
     */
    int execute(String arguments, File file, final StreamConsumer stdout) throws PhpException;
    
    /**
     * Executes PHP with the given arguments.
     *
     * @param arguments string of arguments for PHP (including the file-path and filename)
     * @param stdout handler for stdout lines
     * @param stderr handler for stderr lines
     * @return the return code of PHP
     * @throws PhpException if the executions fails
     * @since 0.1.8
     */
    int execute(String arguments, StreamConsumer stdout, StreamConsumer stderr) throws PhpException;
    
    /**
     * Returns the version of this php executable.
     * @return php executable version.
     * @throws PhpException if the execution fails
     */
    PhpVersion getVersion() throws PhpException;
    
    /**
     * Returns the version string.
     * @return PHP version string.
     * @throws PhpException if the execution fails
     */
    String getStrVersion() throws PhpException;

}
