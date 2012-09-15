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

package org.phpmaven.pear;

import java.io.File;

import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.exec.IPhpExecutableConfiguration;

/**
 * Pear configurator.
 * 
 * <p>
 * This tooling helps to manage PEAR intallations and can be used to read and install PEAR
 * packages from remote channels.
 * </p>
 * 
 * <p>
 * Create an instance via {@link IComponentFactory}.
 * </p>
 * 
 * <p>
 * Configuration of the pear tooling can be done via either the goal you are executing
 * or via plugin configuration. Example of a configuration via build plugin:<br />
 * <pre>
 * &lt;build><br />
 * &nbsp;&nbsp;...<br />
 * &nbsp;&nbsp;&lt;plugins><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;...<br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;plugin><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;groupId>org.phpmaven&lt;/groupId><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;artifactId>maven-php-pear&lt;/artifactId><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;configuration><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;installDir>/dome/dir&lt;/installDir><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/configuration><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/plugin><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;...<br />
 * &nbsp;&nbsp;&lt;/plugins><br />
 * &nbsp;&nbsp;...<br />
 * &lt/build><br />
 * </pre>
 * This example will use an alternative installation directory for all pear actions.
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
 *       pear related actions. See {@link IPhpExecutableConfiguration} for details.
 *   </td>
 * </tr>
 * <tr>
 *   <td>pearUtility</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>Name of the pear utility to be used. Defaults to "PHP_EXE".
 *   </td>
 * </tr>
 * <tr>
 *   <td>installDir</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>Alternative pear installation directory. Defaults to "${project.build.directory}/pear".
 *   </td>
 * </tr>
 * </table>
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public interface IPearConfiguration {
    
    /**
     * Returns the name of the pear utility.
     * @return Pear utility.
     */
    String getPearUtility();
    
    /**
     * Sets the name of the pear utility.
     * @param utility pear utility name.
     */
    void setPearUtility(String utility);
    
    /**
     * Create a pear utility.
     * 
     * @param logger the logger.
     * 
     * @return pear utility.
     * 
     * @throws PlexusConfigurationException thrown if there is a configuration problem.
     * @throws ComponentLookupException thrown if there is a configuration problem.
     */
    IPearUtility getUtility(Log logger) throws PlexusConfigurationException, ComponentLookupException;
    
    /**
     * Sets the installation directory to be used for pear.
     * 
     * @param installDir installation directory.
     */
    void setInstallDir(File installDir);
    
    /**
     * Returns the installation directory to be used for pear.
     * 
     * @return installDir.
     */
    File getInstallDir();

}
