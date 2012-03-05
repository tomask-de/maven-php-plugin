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

/**
 * TODO Docu
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
