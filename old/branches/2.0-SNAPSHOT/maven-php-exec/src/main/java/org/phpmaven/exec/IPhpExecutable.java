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

import java.io.File;

import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.cli.StreamConsumer;

/**
 * An interface representing a php executable.
 * 
 * <p>
 * This interface can be used to invoke php scripts respecting a php
 * configuration. You can receive an implementation of this class by creating
 * a php executable configuration and invoking the method {@link PhpExecutableConfiguration#getPhpExecutable()}
 * </p>
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public interface IPhpExecutable {
    
    /**
     * Configures this executable.
     * 
     * <p>
     * Implementations should fail if this method is called twice. Implementations should assume that
     * this method is called before any other method is called.
     * </p>
     * 
     * @param config configuration
     * @param log the logger
     */
    void configure(IPhpExecutableConfiguration config, Log log);
    
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
    int execute(String arguments, File file, final StreamConsumer stdout) throws PhpException;
    
    /**
     * Executes PHP with the given arguments.
     *
     * @param arguments string of arguments for PHP (including the file-path and filename)
     * @param stdout handler for stdout lines
     * @param stderr handler for stderr lines
     * @return the return code of PHP
     * @throws PhpException if the executions fails
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
