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

package org.phpmaven.project;

import java.io.File;

import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.exec.IPhpExecutableConfiguration;

/**
 * Helper to generate execution configurations for php projects.
 * 
 * <p>
 * This helper is used to generate php execution configurations for PHP-Maven. It set the
 * proper include path and respects the configuration on the mojo.
 * </p>
 * 
 * <p>
 * Create an instance via {@link IComponentFactory}.
 * </p>
 * 
 * <p>
 * Configuration of the php projects executable can be done via either the goal you are executing
 * or via plugin configuration. Example of a configuration via build plugin:<br />
 * <pre>
 * &lt;build><br />
 * &nbsp;&nbsp;...<br />
 * &nbsp;&nbsp;&lt;plugins><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;...<br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;plugin><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;groupId>org.phpmaven&lt;/groupId><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;artifactId>maven-php-project&lt;/artifactId><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;configuration><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &lt;dependenciesDir>${project.build.directory}/php-dependencies&lt;/dependenciesDir><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/configuration><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/plugin><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;...<br />
 * &nbsp;&nbsp;&lt;/plugins><br />
 * &nbsp;&nbsp;...<br />
 * &lt/build><br />
 * </pre>
 * This example will use an alternative folder to extract the php dependencies to.
 * </p>
 * 
 * <p>
 * Available options:
 * </p>
 * 
 * <table border="1">
 * <tr><th>Name</th><th>Command line option</th><th>Property</th><th>Default</th><th>Description</th></tr>
 * <tr>
 *   <td>dependenciesDir</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>${project.build.directory}/php-deps</td>
 *   <td>The folder for the php compile/runtime dependencies to be used</td>
 * </tr>
 * <tr>
 *   <td>testDependenciesDir</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>${project.build.directory}/php-test-deps</td>
 *   <td>The folder for the php test dependencies to be used</td>
 * </tr>
 * <tr>
 *   <td>executableConfig</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>Alternative configuration used every time the php executable is invoked for
 *       project scripts or test scripts. See {@link IPhpExecutableConfiguration} for details.
 *   </td>
 * </tr>
 * </table>
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public interface IProjectPhpExecution {
    
    /**
     * Returns the execution configuration used to invoke scripts for the active project.
     * 
     * @return execution configuration
     * 
     * @throws PlexusConfigurationException thrown on errors while creating the executable
     * @throws ComponentLookupException  thrown on errors while creating the executable
     */
    IPhpExecutableConfiguration getExecutionConfiguration()
        throws PlexusConfigurationException, ComponentLookupException;
    
    /**
     * Returns the execution configuration used to invoke test scripts for the active project.
     * 
     * @return test execution configuration
     * 
     * @throws PlexusConfigurationException thrown on errors while creating the executable
     * @throws ComponentLookupException  thrown on errors while creating the executable
     */
    IPhpExecutableConfiguration getTestExecutionConfiguration()
        throws PlexusConfigurationException, ComponentLookupException;
    
    /**
     * Returns the execution configuration used to invoke scripts for the given project.
     * 
     * @param project the maven project.
     * 
     * @return execution configuration
     * 
     * @throws PlexusConfigurationException thrown on errors while creating the executable
     * @throws ComponentLookupException  thrown on errors while creating the executable
     */
    IPhpExecutableConfiguration getExecutionConfiguration(final MavenProject project)
        throws PlexusConfigurationException, ComponentLookupException;
    
    /**
     * Returns the execution configuration used to invoke test scripts for the given project.
     * 
     * @param project the maven project.
     * 
     * @return test execution configuration
     * 
     * @throws PlexusConfigurationException thrown on errors while creating the executable
     * @throws ComponentLookupException  thrown on errors while creating the executable
     */
    IPhpExecutableConfiguration getTestExecutionConfiguration(final MavenProject project)
        throws PlexusConfigurationException, ComponentLookupException;
    
    /**
     * Returns the execution configuration used to invoke scripts for the active project.
     * 
     * @param mojoConfig The configuration from mojo.
     * 
     * @return execution configuration
     * 
     * @throws PlexusConfigurationException thrown on errors while creating the executable
     * @throws ComponentLookupException  thrown on errors while creating the executable
     */
    IPhpExecutableConfiguration getExecutionConfiguration(final Xpp3Dom mojoConfig)
        throws PlexusConfigurationException, ComponentLookupException;
    
    /**
     * Returns the execution configuration used to invoke test scripts for the active project.
     * 
     * @param mojoConfig The configuration from mojo.
     * 
     * @return test execution configuration
     * 
     * @throws PlexusConfigurationException thrown on errors while creating the executable
     * @throws ComponentLookupException  thrown on errors while creating the executable
     */
    IPhpExecutableConfiguration getTestExecutionConfiguration(final Xpp3Dom mojoConfig)
        throws PlexusConfigurationException, ComponentLookupException;
    
    /**
     * Returns the execution configuration used to invoke scripts for the given project.
     * 
     * @param mojoConfig The configuration from mojo.
     * @param project the maven project.
     * 
     * @return execution configuration
     * 
     * @throws PlexusConfigurationException thrown on errors while creating the executable
     * @throws ComponentLookupException  thrown on errors while creating the executable
     */
    IPhpExecutableConfiguration getExecutionConfiguration(final Xpp3Dom mojoConfig, final MavenProject project)
        throws PlexusConfigurationException, ComponentLookupException;
    
    /**
     * Returns the execution configuration used to invoke test scripts for the given project.
     * 
     * @param mojoConfig The configuration from mojo.
     * @param project the maven project.
     * 
     * @return test execution configuration
     * 
     * @throws PlexusConfigurationException thrown on errors while creating the executable
     * @throws ComponentLookupException  thrown on errors while creating the executable
     */
    IPhpExecutableConfiguration getTestExecutionConfiguration(final Xpp3Dom mojoConfig, final MavenProject project)
        throws PlexusConfigurationException, ComponentLookupException;
    
    /**
     * Returns the current php deps directory.
     * @return php deps directory.
     * @throws ExpressionEvaluationException thrown on configuration errors
     */
    File getDepsDir() throws ExpressionEvaluationException;
    
    /**
     * Returns the php deps directory for current project and given mojo configuration.
     * @param mojoConfig the mojo configuration
     * @return php deps directory.
     * @throws ExpressionEvaluationException thrown on configuration errors
     */
    File getDepsDir(final Xpp3Dom mojoConfig) throws ExpressionEvaluationException;
    
    /**
     * Returns the php deps directory for given project.
     * @param project the project
     * @return php deps directory.
     * @throws ExpressionEvaluationException thrown on configuration errors
     */
    File getDepsDir(final MavenProject project) throws ExpressionEvaluationException;
    
    /**
     * Returns the php deps directory by using the given mojo configuration.
     * @param mojoConfig the mojo configuration
     * @param project the project
     * @return php deps directory.
     * @throws ExpressionEvaluationException thrown on configuration errors
     */
    File getDepsDir(final Xpp3Dom mojoConfig, final MavenProject project) throws ExpressionEvaluationException;
    
    /**
     * Returns the current php test deps directory.
     * @return php deps directory.
     * @throws ExpressionEvaluationException thrown on configuration errors
     */
    File getTestDepsDir() throws ExpressionEvaluationException;
    
    /**
     * Returns the php test deps directory for current project and given mojo configuration.
     * @param mojoConfig the mojo configuration
     * @return php deps directory.
     * @throws ExpressionEvaluationException thrown on configuration errors
     */
    File getTestDepsDir(final Xpp3Dom mojoConfig) throws ExpressionEvaluationException;
    
    /**
     * Returns the php test deps directory for given project.
     * @param project the project
     * @return php deps directory.
     * @throws ExpressionEvaluationException thrown on configuration errors
     */
    File getTestDepsDir(final MavenProject project) throws ExpressionEvaluationException;
    
    /**
     * Returns the php test deps directory by using the given mojo configuration.
     * @param mojoConfig the mojo configuration
     * @param project the project
     * @return php deps directory.
     * @throws ExpressionEvaluationException thrown on configuration errors
     */
    File getTestDepsDir(final Xpp3Dom mojoConfig, final MavenProject project) throws ExpressionEvaluationException;
    
}
