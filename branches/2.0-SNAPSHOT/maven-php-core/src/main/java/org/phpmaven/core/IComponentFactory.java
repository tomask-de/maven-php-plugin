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

package org.phpmaven.core;

import org.apache.maven.execution.MavenSession;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.util.xml.Xpp3Dom;


/**
 * Interface for the php-maven component factory.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public interface IComponentFactory {
    
    /**
     * Lookup the component and apply the given configuration.
     * @param clazz The looked up component class (interface).
     * @param configuration The configuration or {@code null} without any special configuration.
     * @param session the current maven session.
     * @param <T> The component class
     * @return The component instance.
     * @throws ComponentLookupException thrown if the component lookup failed.
     * @throws PlexusConfigurationException thrown if the configuration failed.
     */
    <T> T lookup(Class<T> clazz, Xpp3Dom configuration, MavenSession session)
        throws ComponentLookupException, PlexusConfigurationException;
    
    /**
     * Lookup the component and apply the given configuration.
     * @param clazz The looked up component class (interface).
     * @param roleHint the role hint used to lookup the component.
     * @param configuration The configuration or {@code null} without any special configuration.
     * @param session the current maven session.
     * @param <T> The component class
     * @return The component instance.
     * @throws ComponentLookupException thrown if the component lookup failed.
     * @throws PlexusConfigurationException thrown if the configuration failed.
     */
    <T> T lookup(Class<T> clazz, String roleHint, Xpp3Dom configuration, MavenSession session)
        throws ComponentLookupException, PlexusConfigurationException;

}
