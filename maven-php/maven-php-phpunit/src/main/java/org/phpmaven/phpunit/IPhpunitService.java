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

import org.apache.maven.execution.MavenSession;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.phpmaven.core.IService;

/**
 * Service for phpunit support.
 * 
 * <p>
 * To support additional services create a plexus service implementation.
 * </p>
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public interface IPhpunitService extends IService {
    
    /**
     * Creates a default phpunit support (no specific version).
     * @param session maven session to be used for configuration.
     * @return default phpunit support or {@code null} if this service is not able to create a phpunit support.
     * @throws PlexusConfigurationException thrown if configuration fails.
     * @throws ComponentLookupException thrown if configuration fails.
     */
    IPhpunitSupport createDefault(MavenSession session)
        throws PlexusConfigurationException, ComponentLookupException;
    
    /**
     * Creates a phpunit support for given version.
     * @param version version to be used.
     * @param session maven session to be used for configuration.
     * @return phpunit support or {@code null} if this service is not able to create a phpunit support.
     * @throws PlexusConfigurationException thrown if configuration fails.
     * @throws ComponentLookupException thrown if configuration fails.
     */
    IPhpunitSupport createForPhpunitVersion(String version, MavenSession session)
        throws PlexusConfigurationException, ComponentLookupException;

}
