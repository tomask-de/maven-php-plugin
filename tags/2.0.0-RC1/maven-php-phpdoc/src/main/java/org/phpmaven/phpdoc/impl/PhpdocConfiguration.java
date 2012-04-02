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

package org.phpmaven.phpdoc.impl;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Configuration;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.phpmaven.core.BuildPluginConfiguration;
import org.phpmaven.core.ConfigurationParameter;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.phpdoc.IPhpdocConfiguration;
import org.phpmaven.phpdoc.IPhpdocService;
import org.phpmaven.phpdoc.IPhpdocSupport;

/**
 * Configuration impl.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
@Component(role = IPhpdocConfiguration.class, instantiationStrategy = "per-lookup")
@BuildPluginConfiguration(groupId = "org.phpmaven", artifactId = "maven-php-phpdoc", filter = {
        "executableConfig", "phpdocVersion", "installPhpdoc", "installFolder", "arguments"
        })
public class PhpdocConfiguration implements IPhpdocConfiguration {
    
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
     * The service name.
     */
    @Configuration(name = "phpdocService", value = "PHP_EXE")
    private String phpdocService;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPhpdocService() {
        return this.phpdocService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPhpdocService(String service) {
        this.phpdocService = service;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPhpdocSupport getPhpdocSupport()
        throws PlexusConfigurationException, ComponentLookupException {
        for (final IPhpdocService service : this.factory.getServiceImplementations(IPhpdocService.class, session)) {
            if (this.phpdocService.equals(service.getServiceName())) {
                return service.createDefault(this.session);
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPhpdocSupport getPhpdocSupport(String phpdocVersion)
        throws PlexusConfigurationException, ComponentLookupException {
        for (final IPhpdocService service : this.factory.getServiceImplementations(IPhpdocService.class, session)) {
            if (this.phpdocService.equals(service.getServiceName())) {
                return service.createForPhpdocVersion(phpdocVersion, session);
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPhpdocSupport getPhpdocSupport(MavenProject project)
        throws PlexusConfigurationException, ComponentLookupException {
        final MavenSession inherited = this.session.clone();
        inherited.setCurrentProject(project);
        for (final IPhpdocService service : this.factory.getServiceImplementations(IPhpdocService.class, inherited)) {
            if (this.phpdocService.equals(service.getServiceName())) {
                return service.createDefault(inherited);
            }
        }
        return null;
    }

}
