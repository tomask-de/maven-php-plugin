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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;

/**
 * A helper interface for accessing apache services.
 * 
 * <p>
 * This configuration is used to declare a apache controller that PHP-Maven will use.
 * You should ensure that the httpd.exe windows) or apachectl (unix) is found on path.
 * </p>
 * 
 * <p>
 * Create an instance via {@link IComponentFactory}.
 * </p>
 * 
 * <p>
 * Configuration of the apache executable can be done via either the goal you are executing
 * or via plugin configuration. Example of a configuration via build plugin:<br />
 * <pre>
 * &lt;build><br />
 * &nbsp;&nbsp;...<br />
 * &nbsp;&nbsp;&lt;plugins><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;...<br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;plugin><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;groupId>org.phpmaven&lt;/groupId><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;artifactId>maven-httpd-control-api&lt;/artifactId><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;configuration><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;executable>/path/to/my/httpd.exe&lt;/executable><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/configuration><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/plugin><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;...<br />
 * &nbsp;&nbsp;&lt;/plugins><br />
 * &nbsp;&nbsp;...<br />
 * &lt/build><br />
 * </pre>
 * This example will use an alternative httpd executable.
 * </p>
 * 
 * <p>
 * Another (and better) way to configure the plugin is either setting the parameter at command line
 * (for example "mvn -Dapache.executable=/path/to/httpd.exe") or via settings.xml (property apache.executable).
 * </p>
 * 
 * <p>
 * Available options:
 * </p>
 * 
 * <table border="1">
 * <tr><th>Name</th><th>Command line option</th><th>Property</th><th>Default</th><th>Description</th></tr>
 * <tr>
 *   <td>executable</td>
 *   <td>-Dapache.executable=</td>
 *   <td>apache.executable</td>
 *   <td>"httpd" for windows or "apache2ctl" for unix (found on the PATH)</td>
 *   <td>The apache executable that will be used</td>
 * </tr>
 * <tr>
 *   <td>configFile</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>${project.build.directory}/apache/httpd.conf</td>
 *   <td>The name of the httpd configuration file</td>
 * </tr>
 * <tr>
 *   <td>serverDir</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>${project.build.directory}/apache</td>
 *   <td>The name of the server directory (runtime path, this is not the directory root)</td>
 * </tr>
 * <tr>
 *   <td>defaultConfigFile</td>
 *   <td>apache.defaultConfig</td>
 *   <td>apache.defaultConfig</td>
 *   <td>The file specified at SERVER_CONFIG_FILE at "executable -V" command line</td>
 *   <td>The path to the apache2 configuration to be used with apache.</td>
 * </tr>
 * <tr>
 *   <td>pidFile</td>
 *   <td>apache.pidFile</td>
 *   <td>apache.pidFile</td>
 *   <td>${project.build.directory}/apache/apache2.pid</td>
 *   <td>The pid file to be used by apache.</td>
 * </tr>
 * </table>
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.1
 */
public interface IApacheService {
        
    /**
     * The apache version.
     */
    enum APACHE_VERSION {
        /** apache version 2.0.x. */
        VERSION_2_0,
        /** apache version 2.2.x. */
        VERSION_2_2,
        /** apache version 2.4.x. */
        VERSION_2_4
    }
    
    /**
     * Returns the apache controller that fits the default configuration.
     * @param log the logger
     * @return apache controller.
     * @throws MojoExecutionException 
     * @throws PlexusConfigurationException 
     * @throws ComponentLookupException 
     */
    IApacheController getController(Log log)
        throws MojoExecutionException, ComponentLookupException, PlexusConfigurationException;
    
    /**
     * Returns the apache controller for given version.
     * @param log the logger
     * @param version the apache version
     * @return apache controller.
     * @throws MojoExecutionException 
     * @throws PlexusConfigurationException 
     * @throws ComponentLookupException 
     */
    IApacheController getController(Log log, APACHE_VERSION version)
        throws MojoExecutionException, ComponentLookupException, PlexusConfigurationException;
    
    /**
     * Returns the apache config tool that fits the default configuration.
     * @param log the logger
     * @return apache config tool.
     * @throws MojoExecutionException 
     * @throws PlexusConfigurationException 
     * @throws ComponentLookupException 
     */
    IApacheConfig getConfigTool(Log log)
        throws MojoExecutionException, ComponentLookupException, PlexusConfigurationException;
    
    /**
     * Returns the apache config tool for given version.
     * @param log the logger
     * @param version the apache version
     * @return apache config tool.
     * @throws MojoExecutionException 
     * @throws PlexusConfigurationException 
     * @throws ComponentLookupException 
     */
    IApacheConfig getConfigTool(Log log, APACHE_VERSION version)
        throws MojoExecutionException, ComponentLookupException, PlexusConfigurationException;

}
