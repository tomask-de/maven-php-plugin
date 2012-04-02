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

import org.apache.maven.execution.MavenSession;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.phpmaven.core.IService;

/**
 * Phpdoc services.
 * 
 * <p>
 * To support additional services create a plexus service implementation.
 * </p>
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public interface IPhpdocService extends IService {
    
    /**
     * Creates a default phpdoc support (no specific version).
     * @param session maven session to be used for configuration.
     * @return default phpdoc support or {@code null} if this service is not able to create a phpdoc support.
     * @throws PlexusConfigurationException thrown if configuration fails.
     * @throws ComponentLookupException thrown if configuration fails.
     */
    IPhpdocSupport createDefault(MavenSession session)
        throws PlexusConfigurationException, ComponentLookupException;
    
    /**
     * Creates a phpdoc support for given version.
     * @param version version to be used.
     * @param session maven session to be used for configuration.
     * @return phpdoc support or {@code null} if this service is not able to create a phpdoc support.
     * @throws PlexusConfigurationException thrown if configuration fails.
     * @throws ComponentLookupException thrown if configuration fails.
     */
    IPhpdocSupport createForPhpdocVersion(String version, MavenSession session)
        throws PlexusConfigurationException, ComponentLookupException;

}
