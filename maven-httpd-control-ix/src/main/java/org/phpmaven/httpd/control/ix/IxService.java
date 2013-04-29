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
package org.phpmaven.httpd.control.ix;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Configuration;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.phpmaven.core.BuildPluginConfiguration;
import org.phpmaven.core.ConfigurationParameter;
import org.phpmaven.core.ExecutionUtils;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.httpd.control.IApacheConfig;
import org.phpmaven.httpd.control.IApacheController;
import org.phpmaven.httpd.control.IApacheService;

/**
 * A helper interface for accessing apache services.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.1
 */
@Component(role = IApacheService.class, instantiationStrategy = "per-lookup")
@BuildPluginConfiguration(groupId = "org.phpmaven", artifactId = "maven-httpd-control-api", filter = {
        "configFile", "serverDir"
        })
public class IxService implements IApacheService {
    
    /**
     * The apache executable.
     */
    @Configuration(name = "executable", value = "apachectl")
    @ConfigurationParameter(name = "executable", expression = "${apache.executable}")
    private String executable;
    
    /**
     * the component factory.
     */
    @Requirement
    private IComponentFactory factory;
    
    /**
     * The maven session.
     */
    @ConfigurationParameter(name = "session", expression = "${session}")
    private MavenSession session;

    /**
     * {@inheritDoc} 
     */
    @Override
    public IApacheController getController(Log log)
        throws MojoExecutionException, ComponentLookupException, PlexusConfigurationException {

        String result;
        try {
            final String conf = ExecutionUtils.executeCommand(log, "\"" + this.executable + "\" -V SERVER_CONFIG_FILE");
            final Pattern pattern = Pattern.compile("^\\s*-D\\s*SERVER_CONFIG_FILE=\"(.*)?\"$");
            final Matcher matcher = pattern.matcher(conf);
            result = matcher.group(1);
        } catch (CommandLineException e) {
            throw new MojoExecutionException("Error fetching apache version", e);
        }
        final Pattern pattern = Pattern.compile("^Server version:\\s*Apache/(\\S*)?\\s*.*$");
        final Matcher matcher = pattern.matcher(result);
        if (matcher.find()) {
            final String version = matcher.group(0);
            if (version.startsWith("2.4.")) {
                return this.factory.lookup(
                        IApacheController.class,
                        "V2.4",
                        IComponentFactory.EMPTY_CONFIG,
                        this.session);
            }
            if (version.startsWith("2.2.")) {
                return this.factory.lookup(
                        IApacheController.class,
                        "V2.2",
                        IComponentFactory.EMPTY_CONFIG,
                        this.session);
            }
            if (version.startsWith("2.0.")) {
                return this.factory.lookup(
                        IApacheController.class,
                        "V2.0",
                        IComponentFactory.EMPTY_CONFIG,
                        this.session);
            }
            throw new MojoExecutionException("Unknown version: " + version);
        }
        
        throw new MojoExecutionException("Unknown version/ invalid response:\n" + result);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IApacheController getController(Log log, APACHE_VERSION version)
        throws MojoExecutionException, ComponentLookupException, PlexusConfigurationException {
        if (version == null) {
            return getController(log);
        }
        
        switch (version) {
            case VERSION_2_0:
                return this.factory.lookup(
                    IApacheController.class,
                    "V2.0",
                    IComponentFactory.EMPTY_CONFIG,
                    this.session);
            case VERSION_2_2:
                return this.factory.lookup(
                    IApacheController.class,
                    "V2.2",
                    IComponentFactory.EMPTY_CONFIG,
                    this.session);
            case VERSION_2_4:
                return this.factory.lookup(
                    IApacheController.class,
                    "V2.4",
                    IComponentFactory.EMPTY_CONFIG,
                    this.session);
            default:
                throw new MojoExecutionException("Unknown version/ invalid response:\n" + version);
        }
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public IApacheConfig getConfigTool(Log log)
        throws MojoExecutionException, ComponentLookupException, PlexusConfigurationException {

        String result;
        try {
            result = ExecutionUtils.executeCommand(log, "\"" + this.executable + "\" -v");
        } catch (CommandLineException e) {
            throw new MojoExecutionException("Error fetching apache version", e);
        }
        final Pattern pattern = Pattern.compile("^Server version:\\s*Apache/(\\S*)?\\s*.*$");
        final Matcher matcher = pattern.matcher(result);
        if (matcher.find()) {
            final String version = matcher.group(0);
            if (version.startsWith("2.4.")) {
                return this.factory.lookup(
                        IApacheConfig.class,
                        "V2.4",
                        IComponentFactory.EMPTY_CONFIG,
                        this.session);
            }
            if (version.startsWith("2.2.")) {
                return this.factory.lookup(
                        IApacheConfig.class,
                        "V2.2",
                        IComponentFactory.EMPTY_CONFIG,
                        this.session);
            }
            if (version.startsWith("2.0.")) {
                return this.factory.lookup(
                        IApacheConfig.class,
                        "V2.0",
                        IComponentFactory.EMPTY_CONFIG,
                        this.session);
            }
            throw new MojoExecutionException("Unknown version: " + version);
        }
        
        throw new MojoExecutionException("Unknown version/ invalid response:\n" + result);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IApacheConfig getConfigTool(Log log, APACHE_VERSION version)
        throws MojoExecutionException, ComponentLookupException, PlexusConfigurationException {
        if (version == null) {
            return getConfigTool(log);
        }
        
        switch (version) {
            case VERSION_2_0:
                return this.factory.lookup(
                    IApacheConfig.class,
                    "V2.0",
                    IComponentFactory.EMPTY_CONFIG,
                    this.session);
            case VERSION_2_2:
                return this.factory.lookup(
                    IApacheConfig.class,
                    "V2.2",
                    IComponentFactory.EMPTY_CONFIG,
                    this.session);
            case VERSION_2_4:
                return this.factory.lookup(
                    IApacheConfig.class,
                    "V2.4",
                    IComponentFactory.EMPTY_CONFIG,
                    this.session);
            default:
                throw new MojoExecutionException("Unknown version/ invalid response:\n" + version);
        }
    }

}
