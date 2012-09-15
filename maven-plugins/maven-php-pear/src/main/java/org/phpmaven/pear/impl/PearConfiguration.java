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

package org.phpmaven.pear.impl;

import java.io.File;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Configuration;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.phpmaven.core.BuildPluginConfiguration;
import org.phpmaven.core.ConfigurationParameter;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.pear.IPearConfiguration;
import org.phpmaven.pear.IPearUtility;

/**
 * The pear configuration implementation.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
@Component(role = IPearConfiguration.class, instantiationStrategy = "per-lookup")
@BuildPluginConfiguration(groupId = "org.phpmaven", artifactId = "maven-php-pear")
public class PearConfiguration implements IPearConfiguration {
    
    /**
     * The pear utility implementation to be used.
     */
    @Configuration(name = "pearUtility", value = "PHP_EXE")
    private String pearUtility;
    
    /**
     * The pear installation directory.
     */
    @ConfigurationParameter(name = "installDir", expression = "${project.build.directory}/pear")
    private File installDir;
    
    /**
     * The maven session.
     */
    @ConfigurationParameter(name = "session", expression = "${session}")
    private MavenSession session;
    
    /**
     * The executable config (dummy to let the configurator not complain).
     */
    @SuppressWarnings("unused")
    private Xpp3Dom executableConfig;
    
    /**
     * The configuration factory.
     */
    @Requirement
    private IComponentFactory factory;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getPearUtility() {
        return this.pearUtility;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPearUtility(String utility) {
        this.pearUtility = utility;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPearUtility getUtility(Log logger) throws PlexusConfigurationException,
            ComponentLookupException {
        final IPearUtility result = this.factory.lookup(
                        IPearUtility.class,
                        this.pearUtility,
                        IComponentFactory.EMPTY_CONFIG,
                        this.session);
        result.configure(this.installDir, logger);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInstallDir(File installDir) {
        this.installDir = installDir;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getInstallDir() {
        return this.installDir;
    }

}
