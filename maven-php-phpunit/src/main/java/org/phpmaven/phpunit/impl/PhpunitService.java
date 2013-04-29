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
import org.apache.maven.model.Dependency;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.phpunit.IPhpunitService;
import org.phpmaven.phpunit.IPhpunitSupport;
import org.sonatype.aether.util.version.GenericVersionScheme;
import org.sonatype.aether.version.InvalidVersionSpecificationException;
import org.sonatype.aether.version.Version;

/**
 * Implementation of the default phpunit service.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
@Component(role = IPhpunitService.class, instantiationStrategy = "per-lookup")
public class PhpunitService implements IPhpunitService {
    
    /**
     * The version scheme.
     */
    private static final GenericVersionScheme SCHEME = new GenericVersionScheme();
    
    /**
     * Version 3.3.10.
     */
    private static Version v3310;
    
    /**
     * Version 3.4.0.
     */
    private static Version v340;
    
    /**
     * Version 3.6.0.
     */
    private static Version v360;
    
    /**
     * The component factory.
     */
    @Requirement
    private IComponentFactory factory;
    
    static {
        try {
            v3310 = SCHEME.parseVersion("3.3.10");
            v340 = SCHEME.parseVersion("3.4.0");
            v360 = SCHEME.parseVersion("3.6.0");
        } catch (InvalidVersionSpecificationException ex) {
            // should never happen
            ex.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getServiceName() {
        return "PHP_EXE";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPhpunitSupport createDefault(MavenSession session)
        throws PlexusConfigurationException, ComponentLookupException {
        for (final Dependency dep : session.getCurrentProject().getDependencies()) {
            if ("PHPUnit".equals(dep.getArtifactId()) && "de.phpunit".equals(dep.getGroupId())) {
                return createForPhpunitVersion(dep.getVersion(), session);
            }
            if ("phpunit5".equals(dep.getArtifactId()) && "org.phpunit".equals(dep.getGroupId())) {
                return createForPhpunitVersion(dep.getVersion(), session);
            }
        }
        // try the best matching version.
        return createForPhpunitVersion("3.6.0", session);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPhpunitSupport createForPhpunitVersion(String version,
            MavenSession session) throws PlexusConfigurationException,
            ComponentLookupException {
        try {
            final Version mavenVer = SCHEME.parseVersion(version);
            if (v3310.compareTo(mavenVer) > 0) {
                return this.factory.lookup(
                        IPhpunitSupport.class,
                        "PHP_EXE_V3.3.9",
                        IComponentFactory.EMPTY_CONFIG,
                        session);
            }
            if (v340.compareTo(mavenVer) > 0) {
                return this.factory.lookup(
                        IPhpunitSupport.class,
                        "PHP_EXE_V3.3.10",
                        IComponentFactory.EMPTY_CONFIG,
                        session);
            }
            if (v360.compareTo(mavenVer) > 0) {
                return this.factory.lookup(
                        IPhpunitSupport.class,
                        "PHP_EXE_V3.4.0",
                        IComponentFactory.EMPTY_CONFIG,
                        session);
            }
            return this.factory.lookup(
                    IPhpunitSupport.class,
                    "PHP_EXE_V3.6.0",
                    IComponentFactory.EMPTY_CONFIG,
                    session);
        } catch (InvalidVersionSpecificationException ex) {
            throw new PlexusConfigurationException("Invalid version for phpunit: " + version);
        }
    }

}
