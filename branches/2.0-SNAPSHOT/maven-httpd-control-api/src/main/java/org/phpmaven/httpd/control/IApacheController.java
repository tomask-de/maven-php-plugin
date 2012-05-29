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
package org.phpmaven.httpd.control;

import java.io.File;

import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.phpmaven.httpd.control.IApacheService.APACHE_VERSION;


/**
 * A helper interface to control the apache.
 * 
 * Receive a new instance through IApacheService.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.1
 */
public interface IApacheController {
    
    /**
     * Returns the server directory to be used.
     * @return server directory.
     */
    File getServerDir();
    
    /**
     * Sets the server directory.
     * @param dir server directory.
     */
    void setServerDir(File dir);
    
    /**
     * Returns the config file to be used.
     * @return config file.
     */
    File getConfigFile();
    
    /**
     * Sets the config file.
     * @param config config file.
     */
    void setConfigFile(File config);
    
    /**
     * Returns the executable that is used to start apache.
     * @return executable.
     */
    String getExecutable();
    
    /**
     * Returns the default config file.
     * @param log the logger
     * @return default config file contents or null if it cannot be found/read.
     * @throws CommandLineException thrown on execution errors.
     */
    String getDefaultConfig(Log log) throws CommandLineException;
    
    /**
     * Returns the apache version.
     * @return apache version.
     * @throws CommandLineException thrown on execution errors.
     */
    APACHE_VERSION getVersion() throws CommandLineException;
    
    /**
     * Returns true if there is an active instance.
     * @return true if the instance is active.
     * @throws CommandLineException thrown on execution errors.
     */
    boolean isActive() throws CommandLineException;
    
    /**
     * Returns true if there is an active instance.
     * @return true if the instance is active.
     * @throws CommandLineException thrown on execution errors.
     */
    boolean isDaemonActive() throws CommandLineException;
    
    /**
     * Starts the apache and waits for the process to end. This method will never return until
     * apache is terminated by either sending SIGTERM (Ctrl+C) or by invoking stop from another
     * java thread.
     * @throws CommandLineException thrown on execution errors.
     */
    void start() throws CommandLineException;
    
    /**
     * Starts the apache as a daemon within a new process.
     * @throws CommandLineException thrown on execution errors.
     */
    void startDaemon() throws CommandLineException;
    
    /**
     * Stops the apache within the current process.
     * @throws CommandLineException thrown on execution errors.
     */
    void stop() throws CommandLineException;
    
    /**
     * Stops the apache daemon.
     * @throws CommandLineException thrown on execution errors.
     */
    void stopDaemon() throws CommandLineException;
    
    /**
     * Restarts the apache within the current process.
     * @throws CommandLineException thrown on execution errors.
     */
    void restart() throws CommandLineException;
    
    /**
     * Restarts the apache daemon.
     * @throws CommandLineException thrown on execution errors.
     */
    void restartDaemon() throws CommandLineException;

}
