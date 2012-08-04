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

package org.phpmaven.phpunit.impl;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Configuration;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.phpmaven.core.ConfigurationParameter;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.phpunit.IPhpunitConfiguration;
import org.phpmaven.phpunit.IPhpunitService;
import org.phpmaven.phpunit.IPhpunitSupport;

/**
 * Configuration impl.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
@Component(role = IPhpunitConfiguration.class, instantiationStrategy = "per-lookup")
public class PhpunitConfiguration implements IPhpunitConfiguration {
    
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
    @Configuration(name = "phpunitService", value = "PHP_EXE")
    private String phpunitService;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPhpunitService() {
        return this.phpunitService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPhpunitService(String service) {
        this.phpunitService = service;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPhpunitSupport getPhpunitSupport()
        throws PlexusConfigurationException, ComponentLookupException {
        for (final IPhpunitService service : this.factory.getServiceImplementations(IPhpunitService.class, session)) {
            if (this.phpunitService.equals(service.getServiceName())) {
                return service.createDefault(this.session);
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPhpunitSupport getPhpunitSupport(String phpunitVersion)
        throws PlexusConfigurationException, ComponentLookupException {
        for (final IPhpunitService service : this.factory.getServiceImplementations(IPhpunitService.class, session)) {
            if (this.phpunitService.equals(service.getServiceName())) {
                return service.createForPhpunitVersion(phpunitVersion, session);
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPhpunitSupport getPhpunitSupport(MavenProject project)
        throws PlexusConfigurationException, ComponentLookupException {
        final MavenSession inherited = this.session.clone();
        inherited.setCurrentProject(project);
        for (final IPhpunitService service : this.factory.getServiceImplementations(IPhpunitService.class, inherited)) {
            if (this.phpunitService.equals(service.getServiceName())) {
                return service.createDefault(inherited);
            }
        }
        return null;
    }

}
