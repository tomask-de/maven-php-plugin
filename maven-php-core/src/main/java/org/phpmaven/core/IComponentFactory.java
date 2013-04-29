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
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
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
     * An empty xpp3dom configuration.
     */
    Xpp3Dom[] EMPTY_CONFIG = new Xpp3Dom[0];
    
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
    
    /**
     * Lookup the component and apply the given configuration.
     * @param clazz The looked up component class (interface).
     * @param configuration The configuration array; the first element int he array will be applied first,
     *        the second element will overvrite the first one and so on.
     * @param session the current maven session.
     * @param <T> The component class
     * @return The component instance.
     * @throws ComponentLookupException thrown if the component lookup failed.
     * @throws PlexusConfigurationException thrown if the configuration failed.
     */
    <T> T lookup(Class<T> clazz, Xpp3Dom[] configuration, MavenSession session)
        throws ComponentLookupException, PlexusConfigurationException;
    
    /**
     * Lookup the component and apply the given configuration.
     * @param clazz The looked up component class (interface).
     * @param roleHint the role hint used to lookup the component.
     * @param configuration The configuration array; the first element int he array will be applied first,
     *        the second element will overvrite the first one and so on.
     * @param session the current maven session.
     * @param <T> The component class
     * @return The component instance.
     * @throws ComponentLookupException thrown if the component lookup failed.
     * @throws PlexusConfigurationException thrown if the configuration failed.
     */
    <T> T lookup(Class<T> clazz, String roleHint, Xpp3Dom[] configuration, MavenSession session)
        throws ComponentLookupException, PlexusConfigurationException;
    
    /**
     * receives a build configuration.
     * @param project the maven project.
     * @param groupid the group id
     * @param artifactId the artifact id
     * @return build configuration or null if there is no build configuration for this plugin
     */
    Xpp3Dom getBuildConfig(final MavenProject project, final String groupid, final String artifactId);
    
    /**
     * Parses the given configuration string and returns it by filtering the properties.
     * @param session session
     * @param source source string
     * @param type the class type
     * @param <T> the class type
     * @return filtered string
     * @throws ExpressionEvaluationException thrown on expression errors
     */
    <T> T filterString(final MavenSession session, final String source, Class<T> type)
        throws ExpressionEvaluationException;
    
    /**
     * Returns the service implementations for given class.
     * @param type class type
     * @param <T> class type
     * @param session the maven session.
     * @return implementations
     * @throws ComponentLookupException thrown on configuration errors.
     * @throws PlexusConfigurationException thrown on configuration errors.
     */
    <T extends IService> T[] getServiceImplementations(final Class<T> type, MavenSession session)
        throws ComponentLookupException, PlexusConfigurationException;
    
    /**
     * Returns the service implementations for given class.
     * @param type class type
     * @param <T> class type
     * @param config the configuration.
     * @param session the maven session.
     * @return implementations
     * @throws ComponentLookupException thrown on configuration errors.
     * @throws PlexusConfigurationException thrown on configuration errors.
     */
    <T extends IService> T[] getServiceImplementations(final Class<T> type, Xpp3Dom[] config, MavenSession session)
        throws ComponentLookupException, PlexusConfigurationException;

}
