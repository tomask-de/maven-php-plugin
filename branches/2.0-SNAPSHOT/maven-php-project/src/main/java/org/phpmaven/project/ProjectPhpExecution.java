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

package org.phpmaven.project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.phpmaven.core.ConfigurationParameter;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.exec.IPhpExecutableConfiguration;

/**
 * Implementation of the php project execution helper.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
@Component(role = IProjectPhpExecution.class, instantiationStrategy = "singleton")
public class ProjectPhpExecution implements IProjectPhpExecution {
    
    /**
     * The component factory.
     */
    @Requirement
    private IComponentFactory componentFactory;
    
    /**
     * The maven session.
     */
    @ConfigurationParameter(name = "session", expression = "${session}")
    private MavenSession session;

    /**
     * {@inheritDoc}
     */
    @Override
    public IPhpExecutableConfiguration getExecutionConfiguration()
        throws PlexusConfigurationException, ComponentLookupException {
        return this.getExecutionConfiguration(null, this.session.getCurrentProject(), this.session);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPhpExecutableConfiguration getTestExecutionConfiguration()
        throws PlexusConfigurationException, ComponentLookupException {
        return this.getTestExecutionConfiguration(null, this.session.getCurrentProject(), this.session);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPhpExecutableConfiguration getExecutionConfiguration(MavenProject project)
        throws PlexusConfigurationException, ComponentLookupException {
        final MavenSession s2 = this.session.clone();
        s2.setCurrentProject(project);
        return this.getExecutionConfiguration(null, project, s2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPhpExecutableConfiguration getTestExecutionConfiguration(MavenProject project)
        throws PlexusConfigurationException, ComponentLookupException {
        final MavenSession s2 = this.session.clone();
        s2.setCurrentProject(project);
        return this.getTestExecutionConfiguration(null, project, s2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPhpExecutableConfiguration getExecutionConfiguration(Xpp3Dom mojoConfig)
        throws PlexusConfigurationException, ComponentLookupException {
        return this.getExecutionConfiguration(mojoConfig, this.session.getCurrentProject(), this.session);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPhpExecutableConfiguration getTestExecutionConfiguration(Xpp3Dom mojoConfig)
        throws PlexusConfigurationException, ComponentLookupException {
        return this.getTestExecutionConfiguration(mojoConfig, this.session.getCurrentProject(), this.session);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPhpExecutableConfiguration getExecutionConfiguration(Xpp3Dom mojoConfig, MavenProject project)
        throws PlexusConfigurationException, ComponentLookupException {
        final MavenSession s2 = this.session.clone();
        s2.setCurrentProject(project);
        return this.getExecutionConfiguration(mojoConfig, project, s2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPhpExecutableConfiguration getTestExecutionConfiguration(Xpp3Dom mojoConfig, MavenProject project)
        throws PlexusConfigurationException, ComponentLookupException {
        final MavenSession s2 = this.session.clone();
        s2.setCurrentProject(project);
        return this.getTestExecutionConfiguration(mojoConfig, project, s2);
    }
    
    /**
     * Returns the execution configuration used to invoke scripts for the given project.
     * 
     * @param mojoConfig The configuration from mojo.
     * @param project the maven project.
     * @param mavenSession the maven session.
     * 
     * @return execution configuration
     * 
     * @throws PlexusConfigurationException thrown on errors while creating the executable
     * @throws ComponentLookupException  thrown on errors while creating the executable
     */
    private IPhpExecutableConfiguration getExecutionConfiguration(
            final Xpp3Dom mojoConfig, final MavenProject project, final MavenSession mavenSession)
        throws PlexusConfigurationException, ComponentLookupException {
        final List<Xpp3Dom> configs = new ArrayList<Xpp3Dom>();
        final Xpp3Dom buildConfig = this.componentFactory.getBuildConfig(project, "org.phpmaven", "maven-php-project");
        if (buildConfig != null && buildConfig.getChild("executableConfig") != null) {
            configs.add(buildConfig.getChild("executableConfig"));
        }
        if (mojoConfig != null && mojoConfig.getChild("executableConfig") != null) {
            configs.add(mojoConfig.getChild("executableConfig"));
        }
        final IPhpExecutableConfiguration execConfig = this.componentFactory.lookup(
                IPhpExecutableConfiguration.class,
                configs.toArray(new Xpp3Dom[configs.size()]),
                mavenSession);
        try {
            this.addIncludes(execConfig, buildConfig, mojoConfig, project, mavenSession);
        } catch (ExpressionEvaluationException ex) {
            throw new PlexusConfigurationException("Problems evaluating the includes", ex);
        }
        return execConfig;
    }

    /**
     * Returns the execution configuration used to invoke test scripts for the given project.
     * 
     * @param mojoConfig The configuration from mojo.
     * @param project the maven project.
     * @param mavenSession the maven session.
     * 
     * @return test execution configuration
     * 
     * @throws PlexusConfigurationException thrown on errors while creating the executable
     * @throws ComponentLookupException  thrown on errors while creating the executable
     */
    IPhpExecutableConfiguration getTestExecutionConfiguration(
            final Xpp3Dom mojoConfig, final MavenProject project, final MavenSession mavenSession)
        throws PlexusConfigurationException, ComponentLookupException {
        final List<Xpp3Dom> configs = new ArrayList<Xpp3Dom>();
        final Xpp3Dom buildConfig = this.componentFactory.getBuildConfig(project, "org.phpmaven", "maven-php-project");
        if (buildConfig != null && buildConfig.getChild("executableConfig") != null) {
            configs.add(buildConfig.getChild("executableConfig"));
        }
        if (mojoConfig != null && mojoConfig.getChild("executableConfig") != null) {
            configs.add(mojoConfig.getChild("executableConfig"));
        }
        final IPhpExecutableConfiguration execConfig = this.componentFactory.lookup(
                IPhpExecutableConfiguration.class,
                configs.toArray(new Xpp3Dom[configs.size()]),
                mavenSession);
        try {
            this.addIncludes(execConfig, buildConfig, mojoConfig, project, mavenSession);
            this.addTestIncludes(execConfig, buildConfig, mojoConfig, project, mavenSession);
        } catch (ExpressionEvaluationException ex) {
            throw new PlexusConfigurationException("Problems evaluating the includes", ex);
        }
        return execConfig;
    }

    /**
     * Adds the default includes to the executable configuration.
     * @param execConfig executable configuration.
     * @param buildConfig the build configuration.
     * @param mojoConfig the mojo configuration.
     * @param project the project.
     * @param mavenSession the maven session.
     * @throws ExpressionEvaluationException thrown on maven property errors
     */
    private void addIncludes(
            IPhpExecutableConfiguration execConfig,
            Xpp3Dom buildConfig,
            Xpp3Dom mojoConfig,
            MavenProject project,
            MavenSession mavenSession)
        throws ExpressionEvaluationException {
        execConfig.getIncludePath().add(project.getBuild().getOutputDirectory());
        File depsDir;
        if (buildConfig == null || buildConfig.getChild("dependenciesDir") == null) {
            depsDir = this.componentFactory.filterString(
                    mavenSession,
                    "${project.basedir}/target/php-deps",
                    File.class);
        } else {
            depsDir = this.componentFactory.filterString(
                    mavenSession,
                    buildConfig.getChild("dependenciesDir").getValue(),
                    File.class);
        }
        if (mojoConfig != null && mojoConfig.getChild("dependenciesDir") != null) {
            depsDir = this.componentFactory.filterString(
                    mavenSession,
                    mojoConfig.getChild("dependenciesDir").getValue(),
                    File.class);
        }
        execConfig.getIncludePath().add(depsDir.getAbsolutePath());
        // TODO: Bad hack for broken pear libraries.
        execConfig.getIncludePath().add(new File(depsDir, "pear").getAbsolutePath());
    }
    
    /**
     * Adds the test includes to the executable configuration.
     * @param execConfig executable configuration.
     * @param buildConfig 
     * @param mojoConfig 
     * @param project the project.
     * @param mavenSession the maven session.
     * @throws ExpressionEvaluationException thrown on maven property errors
     */
    private void addTestIncludes(
            IPhpExecutableConfiguration execConfig,
            Xpp3Dom buildConfig,
            Xpp3Dom mojoConfig,
            MavenProject project,
            MavenSession mavenSession)
        throws ExpressionEvaluationException {
        execConfig.getIncludePath().add(project.getBuild().getTestOutputDirectory());
        File depsDir;
        if (buildConfig == null || buildConfig.getChild("testDependenciesDir") == null) {
            depsDir = this.componentFactory.filterString(
                    mavenSession,
                    "${project.basedir}/target/php-test-deps",
                    File.class);
        } else {
            depsDir = this.componentFactory.filterString(
                    mavenSession,
                    buildConfig.getChild("testDependenciesDir").getValue(),
                    File.class);
        }
        if (mojoConfig != null && mojoConfig.getChild("testDependenciesDir") != null) {
            depsDir = this.componentFactory.filterString(
                    mavenSession,
                    mojoConfig.getChild("testDependenciesDir").getValue(),
                    File.class);
        }
        execConfig.getIncludePath().add(depsDir.getAbsolutePath());
        // TODO: Bad hack for broken pear libraries.
        execConfig.getIncludePath().add(new File(depsDir, "pear").getAbsolutePath());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getDepsDir() throws ExpressionEvaluationException {
        return this.getDepsDir(null, this.session.getCurrentProject(), this.session);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getDepsDir(Xpp3Dom mojoConfig) throws ExpressionEvaluationException {
        return this.getDepsDir(mojoConfig, this.session.getCurrentProject(), this.session);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getDepsDir(MavenProject project) throws ExpressionEvaluationException {
        final MavenSession s2 = this.session.clone();
        s2.setCurrentProject(project);
        return this.getDepsDir(null, project, s2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getDepsDir(Xpp3Dom mojoConfig, MavenProject project) throws ExpressionEvaluationException {
        final MavenSession s2 = this.session.clone();
        s2.setCurrentProject(project);
        return this.getDepsDir(mojoConfig, project, s2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getTestDepsDir() throws ExpressionEvaluationException {
        return this.getTestDepsDir(null, this.session.getCurrentProject(), this.session);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getTestDepsDir(Xpp3Dom mojoConfig) throws ExpressionEvaluationException {
        return this.getTestDepsDir(mojoConfig, this.session.getCurrentProject(), this.session);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getTestDepsDir(MavenProject project) throws ExpressionEvaluationException {
        final MavenSession s2 = this.session.clone();
        s2.setCurrentProject(project);
        return this.getTestDepsDir(null, project, s2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getTestDepsDir(Xpp3Dom mojoConfig, MavenProject project) throws ExpressionEvaluationException {
        final MavenSession s2 = this.session.clone();
        s2.setCurrentProject(project);
        return this.getTestDepsDir(mojoConfig, project, s2);
    }
    
    /**
     * Returns the php deps directory by using the given mojo configuration.
     * @param mojoConfig the mojo configuration
     * @param project the project
     * @param mavenSession the maven session
     * @return php deps directory.
     * @throws ExpressionEvaluationException thrown on configuration errors
     */
    private File getDepsDir(Xpp3Dom mojoConfig, MavenProject project, MavenSession mavenSession)
        throws ExpressionEvaluationException {
        final Xpp3Dom buildConfig = this.componentFactory.getBuildConfig(project, "org.phpmaven", "maven-php-project");
        File depsDir;
        if (buildConfig == null || buildConfig.getChild("dependenciesDir") == null) {
            depsDir = this.componentFactory.filterString(
                    mavenSession,
                    "${project.basedir}/target/php-deps",
                    File.class);
        } else {
            depsDir = this.componentFactory.filterString(
                    mavenSession,
                    buildConfig.getChild("dependenciesDir").getValue(),
                    File.class);
        }
        if (mojoConfig != null && mojoConfig.getChild("dependenciesDir") != null) {
            depsDir = this.componentFactory.filterString(
                    mavenSession,
                    mojoConfig.getChild("dependenciesDir").getValue(),
                    File.class);
        }
        return depsDir;
    }
    
    /**
     * Returns the php test deps directory by using the given mojo configuration.
     * @param mojoConfig the mojo configuration
     * @param project the project
     * @param mavenSession the maven session
     * @return php deps directory.
     * @throws ExpressionEvaluationException thrown on configuration errors
     */
    private File getTestDepsDir(Xpp3Dom mojoConfig, MavenProject project, MavenSession mavenSession)
        throws ExpressionEvaluationException {
        final Xpp3Dom buildConfig = this.componentFactory.getBuildConfig(project, "org.phpmaven", "maven-php-project");
        File depsDir;
        if (buildConfig == null || buildConfig.getChild("testDependenciesDir") == null) {
            depsDir = this.componentFactory.filterString(
                    mavenSession,
                    "${project.basedir}/target/php-test-deps",
                    File.class);
        } else {
            depsDir = this.componentFactory.filterString(
                    mavenSession,
                    buildConfig.getChild("testDependenciesDir").getValue(),
                    File.class);
        }
        if (mojoConfig != null && mojoConfig.getChild("testDependenciesDir") != null) {
            depsDir = this.componentFactory.filterString(
                    mavenSession,
                    mojoConfig.getChild("testDependenciesDir").getValue(),
                    File.class);
        }
        return depsDir;
    }
    
}
