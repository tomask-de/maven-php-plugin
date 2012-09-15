/**
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
package org.phpmaven.plugin.php;

import java.io.File;

import org.codehaus.plexus.util.cli.StreamConsumer;

/**
 * Interface to execute php scripts.
 * 
 * @author Martin Eisengardt
 */
public interface IPhpExecution {
    
    /**
     * Executes PHP with the given arguments and returns its output.
     *
     * @param arguments string of arguments for PHP
     * @param file a hint which file will be processed
     * @return the output string
     * @throws PhpException if the execution failed
     */
    String execute(String arguments, File file) throws PhpException;
    
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
     * @param arguments string of arguments for PHP
     * @param file a hint which file will be processed
     * @param stdout handler for stdout lines
     * @return the returncode of PHP
     * @throws PhpException if the execution failed
     */
    int execute(String arguments, File file, final StreamConsumer stdout) throws PhpException;
    
    /**
     * Executes PHP with the given arguments.
     *
     * @param arguments string of arguments for PHP
     * @param stdout handler for stdout lines
     * @param stderr handler for stderr lines
     * @return the return code of PHP
     * @throws PhpException if the executions fails
     */
    int execute(String arguments, StreamConsumer stdout, StreamConsumer stderr) throws PhpException;
    
    /**
     * Generated the command line option for default include path (without testing).
     * 
     * @param file Optional file that parents directory is used.
     * 
     * @return command line option for include path
     */
    String defaultIncludePath(File file);
    
    /**
     * Generated the command line option for default include path (test staging).
     * 
     * @param file Optional file that parents directory is used.
     * 
     * @return command line option for include path
     */
    String defaultTestIncludePath(File file);

}
