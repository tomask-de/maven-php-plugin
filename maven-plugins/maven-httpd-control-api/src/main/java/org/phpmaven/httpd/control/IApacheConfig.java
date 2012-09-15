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
 * A helper interface to manipulate and write apache configuration files.
 * 
 * Receive a new instance through IApacheService.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.1
 */
public interface IApacheConfig extends IApacheConfigCommon {
    
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
     * Returns the default configuration file.
     * @param log the logger
     * @throws CommandLineException thrown on execution errors.
     */
    void loadDefaultConfig(Log log) throws CommandLineException;
    
    /**
     * Returns the configuration file.
     * @param log the logger
     * @throws CommandLineException thrown on execution errors.
     */
    void loadConfigFile(Log log) throws CommandLineException;
    
    /**
     * Returns the apache version.
     * @return apache version.
     * @throws CommandLineException thrown on execution errors.
     */
    APACHE_VERSION getVersion() throws CommandLineException;
    
    /**
     * Writes the content to the config file.
     */
    void write();
    
    /**
     * Returns the server root.
     * @return server root
     */
    String getServerRoot();
    
    /**
     * Sets the server root; not needed because IApacheController will declare this at command line.
     * @param root new server root.
     */
    void setServerRoot(String root);
    
    /**
     * Returns the pid file.
     * @return pid file.
     */
    String getPidFile();
    
    /**
     * Sets the pid file; not needed because IApacheController will declare this at command line.
     * @param file new pid file location.
     */
    void setPidFile(String file);
    
    /**
     * Returns the user to be used for running apache.
     * @return user to be run with apache.
     */
    String getUser();
    
    /**
     * Sets the user to be run with apache.
     * @param user user to be run.
     */
    void setUser(String user);
    
    /**
     * Returns the group to be used for running apache.
     * @return group to be run with  apached.
     */
    String getGroup();
    
    /**
     * Sets the group to be run with apache.
     * @param group group to be run.
     */
    void setGroup(String group);
    
    /**
     * Returns the apache virtual hosts.
     * @return apache virtual hosts.
     */
    Iterable<IApacheConfigVHost> getVirtualHosts();
    
    /**
     * Declares a new virtual host.
     * @param name name of the virtual host (NameVirtualHost directive)
     * @return the new virtual host
     */
    IApacheConfigVHost declareVirtualHost(String name);
    
    /**
     * Returns a new virtual host.
     * @param name name of the virtual host (NameVirtualHost directive)
     * @return the virtual host or null if it was not found
     */
    IApacheConfigVHost getVirtualHost(String name);
    
    /**
     * Removes a virtual host.
     * @param vhost virtual host to be removed.
     */
    void removeVirtualHost(IApacheConfigVHost vhost);
    
    /**
     * Returns the listeners to be used for apache.
     * @return apache listeners.
     */
    Iterable<IApacheConfigPort> getListeners();
    
    /**
     * Declares a new listener.
     * @param port port to listen
     * @return port number.
     */
    IApacheConfigPort declareListener(int port);
    
    /**
     * Removes a listener.
     * @param listener listener to be removed
     */
    void removeListener(IApacheConfigPort listener);

}
