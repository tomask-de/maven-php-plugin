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

package org.phpmaven.phpunit;

import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.exec.IPhpExecutableConfiguration;

/**
 * Phpunit configurator.
 * 
 * <p>
 * This tooling helps to manage PHPUNIT intallations and can be used to perform
 * PHPUNIT tests.
 * </p>
 * 
 * <p>
 * Create an instance via {@link IComponentFactory}.
 * </p>
 * 
 * <p>
 * Configuration of the phpunit tooling can be done via either the goal you are executing
 * or via plugin configuration. Example of a configuration via build plugin:<br />
 * <pre>
 * &lt;build><br />
 * &nbsp;&nbsp;...<br />
 * &nbsp;&nbsp;&lt;plugins><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;...<br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;plugin><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;groupId>org.phpmaven&lt;/groupId><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;artifactId>maven-php-phpunit&lt;/artifactId><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;configuration><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;arguments>--verbose&lt;/arguments><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/configuration><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/plugin><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;...<br />
 * &nbsp;&nbsp;&lt;/plugins><br />
 * &nbsp;&nbsp;...<br />
 * &lt/build><br />
 * </pre>
 * This example will output verbose information on phpunit.
 * </p>
 * 
 * <p>
 * Available options:
 * </p>
 * 
 * <table border="1">
 * <tr><th>Name</th><th>Command line option</th><th>Property</th><th>Default</th><th>Description</th></tr>
 * <tr>
 *   <td>executableConfig</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>Alternative configuration used every time the php executable is invoked for
 *       phpunit test cases. See {@link IPhpExecutableConfiguration} for details.
 *   </td>
 * </tr>
 * <tr>
 *   <td>phpunitService</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>Name of the phpunit service. Defaults to "PHP_EXE".
 *   </td>
 * </tr>
 * <tr>
 *   <td>phpunitVersion</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>Overwrite the phpunit version that is assumed to be used. Defaults to the version
 *   that is added as project dependency.
 *   </td>
 * </tr>
 * <tr>
 *   <td>arguments</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>Additional arguments passed to the phpunit command line.
 *   </td>
 * </tr>
 * <tr>
 *   <td>singleInvocation</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>True to start all tests within the same php exe invocation; false to invoke php exe
 *   several times for multiple tests. Defaults to true.
 *   </td>
 * </tr>
 * </table>
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public interface IPhpunitConfiguration {
    
    /**
     * Returns the name of the phpunit service.
     * @return phpunit service name.
     */
    String getPhpunitService();
    
    /**
     * Sets the name of the phpunit service.
     * @param service phpunit service name.
     */
    void setPhpunitService(String service);
    
    /**
     * Returns phpunit support for the current project/ session.
     * @return phpunit support.
     * @throws PlexusConfigurationException thrown on configuration errors.
     * @throws ComponentLookupException thrown on configuration errors.
     */
    IPhpunitSupport getPhpunitSupport() throws PlexusConfigurationException, ComponentLookupException;
    
    /**
     * Returns phpunit support for given phpunit version.
     * @param phpunitVersion the phpunit version to be used.
     * @return phpunit support.
     * @throws PlexusConfigurationException thrown on configuration errors.
     * @throws ComponentLookupException thrown on configuration errors.
     */
    IPhpunitSupport getPhpunitSupport(String phpunitVersion)
        throws PlexusConfigurationException, ComponentLookupException;
    
    /**
     * Returns phpunit support for given project.
     * @param project the project the phpunit support will be created for.
     * @return phpunit support.
     * @throws PlexusConfigurationException thrown on configuration errors.
     * @throws ComponentLookupException thrown on configuration errors.
     */
    IPhpunitSupport getPhpunitSupport(MavenProject project)
        throws PlexusConfigurationException, ComponentLookupException;

}
