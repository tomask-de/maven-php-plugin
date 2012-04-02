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

package org.phpmaven.phar;

import org.apache.maven.execution.MavenSession;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Configuration;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.phpmaven.core.BuildPluginConfiguration;
import org.phpmaven.core.ConfigurationParameter;
import org.phpmaven.core.IComponentFactory;

/**
 * Implementation of the phar packager configuration.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
@Component(role = IPharPackagerConfiguration.class, instantiationStrategy = "per-lookup")
@BuildPluginConfiguration(groupId = "org.phpmaven", artifactId = "php-maven-phar")
public class PharPackagerConfiguration implements IPharPackagerConfiguration {
    
    /**
     * The component factory.
     */
    @Requirement
    private IComponentFactory factory;
    
    /**
     * The maven session.
     */
    @ConfigurationParameter(name = "session", expression = "${session}")
    private MavenSession session;
    
    /**
     * The packager to be used.
     */
    @Configuration(name = "packager", value = "PHP_EXE")
    private String packager;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public IPharPackager getPharPackager() throws PlexusConfigurationException,
            ComponentLookupException {
        return this.factory.lookup(IPharPackager.class, IComponentFactory.EMPTY_CONFIG, this.session);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPackager() {
        return this.packager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPackager(String packager) {
        this.packager = packager;
    }
    
    /**
     * dummy setter.
     * @param config dummy.
     */
    public void setExecutableConfig(Xpp3Dom config) {
        // does nothing; only to let the component factory not complain
    }
    
    /**
     * dummy setter.
     * @param config dummy.
     */
    public void setPharConfig(Xpp3Dom config) {
        // does nothing; only to let the component factory not complain
    }

}
