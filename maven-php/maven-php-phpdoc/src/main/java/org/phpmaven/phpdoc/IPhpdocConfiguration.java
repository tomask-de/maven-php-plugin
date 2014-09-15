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

package org.phpmaven.phpdoc;

import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.exec.IPhpExecutableConfiguration;

/**
 * Phpdoc configuration.
 * 
 * <p>
 * This tooling helps to manage PHPDOC intallations and can be used to generate
 * PHPDOC reports.
 * </p>
 * 
 * <p>
 * Create an instance via {@link IComponentFactory}.
 * </p>
 * 
 * <p>
 * Configuration of the phpdoc tooling can be done via either the goal you are executing
 * or via phpdoc configuration. Example of a configuration via build plugin:<br />
 * <pre>
 * &lt;build><br />
 * &nbsp;&nbsp;...<br />
 * &nbsp;&nbsp;&lt;plugins><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;...<br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;plugin><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;groupId>org.phpmaven&lt;/groupId><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;artifactId>maven-php-phpdoc&lt;/artifactId><br />
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
 *       phpdoc generation. See {@link IPhpExecutableConfiguration} for details.
 *   </td>
 * </tr>
 * <tr>
 *   <td>phpdocService</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>Name of the phpdoc service. Defaults to "PHP_EXE".
 *   </td>
 * </tr>
 * <tr>
 *   <td>phpdocVersion</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>Overwrite the phpdoc version that is assumed to be used. Defaults to the version
 *   that is added as project dependency.
 *   </td>
 * </tr>
 * <tr>
 *   <td>installPhpdoc</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>true to installs the phpdoc locally. Can be used if phpdoc is not added as project dependency.
 *   Defaults to true if phpdoc is not found as project dependency.
 *   </td>
 * </tr>
 * <tr>
 *   <td>installFolder</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>Folder to install phpdoc to.
 *   </td>
 * </tr>
 * <tr>
 *   <td>arguments</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>Additional arguments passed to the phpdoc command line.
 *   </td>
 * </tr>
 * </table>
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public interface IPhpdocConfiguration {
    
    /**
     * Returns the service name for phpdoc.
     * @return service name for phpdoc.
     */
    String getPhpdocService();
    
    /**
     * Sets the service name for phpdoc.
     * @param service service name.
     */
    void setPhpdocService(String service);
    
    /**
     * Creates phpdoc support for default version.
     * @return phpdoc support.
     * @throws PlexusConfigurationException thrown on configuration errors.
     * @throws ComponentLookupException thrown on configuration errors.
     */
    IPhpdocSupport getPhpdocSupport()
        throws PlexusConfigurationException, ComponentLookupException;
    
    /**
     * Creates phpdoc support for given version and current project.
     * @param phpdocVersion the phpdocVersion to be used.
     * @return phpdoc support.
     * @throws PlexusConfigurationException thrown on configuration errors.
     * @throws ComponentLookupException thrown on configuration errors.
     */
    IPhpdocSupport getPhpdocSupport(String phpdocVersion)
        throws PlexusConfigurationException, ComponentLookupException;
    
    /**
     * Creates phpdoc support for given project.
     * @param project the project to be used.
     * @return phpdoc support.
     * @throws PlexusConfigurationException thrown on configuration errors.
     * @throws ComponentLookupException thrown on configuration errors.
     */
    IPhpdocSupport getPhpdocSupport(MavenProject project)
        throws PlexusConfigurationException, ComponentLookupException;

}
