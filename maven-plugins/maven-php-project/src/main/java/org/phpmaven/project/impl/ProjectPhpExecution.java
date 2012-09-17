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

package org.phpmaven.project.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.repository.RepositorySystem;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.phpmaven.core.ConfigurationParameter;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.dependency.IAction;
import org.phpmaven.dependency.IAction.ActionType;
import org.phpmaven.dependency.IActionExtractAndInclude;
import org.phpmaven.dependency.IActionInclude;
import org.phpmaven.dependency.IDependency;
import org.phpmaven.dependency.IDependencyConfiguration;
import org.phpmaven.exec.IPhpExecutableConfiguration;
import org.phpmaven.project.IProjectPhpExecution;

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
     * The maven project builder.
     */
    @Requirement
    private ProjectBuilder mavenProjectBuilder;
    
    /**
     * the repository system.
     */
    @Requirement
    private RepositorySystem reposSystem;

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
        final IDependencyConfiguration depConfig = this.componentFactory.lookup(
                IDependencyConfiguration.class,
                IComponentFactory.EMPTY_CONFIG,
                mavenSession);
        try {
            this.addIncludes(execConfig, buildConfig, mojoConfig, project, mavenSession, depConfig);
        } catch (ExpressionEvaluationException ex) {
            throw new PlexusConfigurationException("Problems evaluating the includes", ex);
        } catch (MojoExecutionException ex) {
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
        final IDependencyConfiguration depConfig = this.componentFactory.lookup(
                IDependencyConfiguration.class,
                IComponentFactory.EMPTY_CONFIG,
                mavenSession);
        try {
            this.addIncludes(execConfig, buildConfig, mojoConfig, project, mavenSession, depConfig);
            this.addTestIncludes(execConfig, buildConfig, mojoConfig, project, mavenSession, depConfig);
        } catch (ExpressionEvaluationException ex) {
            throw new PlexusConfigurationException("Problems evaluating the includes", ex);
        } catch (MojoExecutionException ex) {
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
            MavenSession mavenSession,
            IDependencyConfiguration depConfig)
        throws ExpressionEvaluationException, MojoExecutionException {
        execConfig.getIncludePath().add(project.getBuild().getOutputDirectory());
        File depsDir;
        if (buildConfig == null || buildConfig.getChild("dependenciesDir") == null) {
            depsDir = this.componentFactory.filterString(
                    mavenSession,
                    "${project.build.directory}/php-deps",
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
        
        addFromDepConfig(execConfig, project, Artifact.SCOPE_COMPILE, depConfig, depsDir);
        
        // add the project dependencies of multi-project-poms
        addProjectDependencies(execConfig, project, Artifact.SCOPE_COMPILE, depConfig);
    }

    /**
     * Adds additional include paths from dependency config.
     * @param execConfig
     * @param project
     * @param targetScope
     * @param depConfig
     * @param depsDir
     * @throws ExpressionEvaluationException 
     */
    private void addFromDepConfig(
        IPhpExecutableConfiguration execConfig,
        MavenProject project,
        String targetScope,
        IDependencyConfiguration depConfig,
        File depsDir) throws ExpressionEvaluationException, MojoExecutionException {
        final Set<Artifact> deps = project.getArtifacts();
        
        for (final Artifact depObject : deps) {
            if (!targetScope.equals(depObject.getScope())) {
                continue;
            }
            
            MavenProject depProject;
            try {
                depProject = this.getProjectFromArtifact(project, depObject);
            } catch (ProjectBuildingException ex) {
                throw new ExpressionEvaluationException("Problems creating maven project from dependency", ex);
            }
            
            if (depProject.getFile() != null) {
                // Reference to a local project; should only happen in IDEs or multi-project poms
                
                for (final IAction action : DependencyHelper.getDependencyActions(depObject, depConfig, reposSystem, session, mavenProjectBuilder, componentFactory)) {
                    if (action.getType() == ActionType.ACTION_INCLUDE) {
                        final String includePath =
                            getClassesDirFromProject(depProject) +
                            "/" +
                            ((IActionInclude) action).getPharPath();
                        execConfig.getIncludePath().add(new File(includePath).getAbsolutePath());
                    } else if (action.getType() == ActionType.ACTION_EXTRACT_INCLUDE) {
                        final String includePath =
                            getClassesDirFromProject(depProject) +
                            "/" +
                            ((IActionExtractAndInclude) action).getPharPath() +
                            "/" +
                            ((IActionExtractAndInclude) action).getIncludePath();
                        execConfig.getIncludePath().add(new File(includePath).getAbsolutePath());
                    } else if (action.getType() == ActionType.ACTION_CLASSIC) {
                        final String includePath =
                            getClassesDirFromProject(depProject);
                        execConfig.getIncludePath().add(new File(includePath).getAbsolutePath());
                    }
                }
            }
            
            if (depObject.getFile() != null) {
                // Reference to a local repository
                for (final IAction action : DependencyHelper.getDependencyActions(depObject, depConfig, reposSystem, session, mavenProjectBuilder, componentFactory)) {
                    if (action.getType() == ActionType.ACTION_INCLUDE) {
                        final String includePath = ((IActionInclude) action).getPharPath();
                        execConfig.getIncludePath().add(
                            "phar://" +
                            depObject.getFile().getAbsolutePath().replace("\\", "/") +
                            "/" +
                            (includePath.startsWith("/") ? includePath.substring(1) : includePath));
                    } else if (action.getType() == ActionType.ACTION_EXTRACT_INCLUDE) {
                        final String includePath = ((IActionExtractAndInclude) action).getIncludePath();
                        final String pharPath = ((IActionExtractAndInclude) action).getPharPath();
                        execConfig.getIncludePath().add(
                            "phar://" +
                            depObject.getFile().getAbsolutePath().replace("\\", "/") +
                            "/" +
                            (pharPath.startsWith("/") ? pharPath.substring(1) : pharPath) +
                            (pharPath.endsWith("/") || pharPath.length() == 0 ? "" : "/") +
                            (includePath.startsWith("/") ? includePath.substring(1) : includePath));
                    }
                }
            }
        }
    }

    /**
     * Adds project dependencies (/target/classes) for given scope (needed for IDE/multi-pom).
     * @param execConfig executable config.
     * @param project project.
     * @param targetScope target scope.
     * @throws ExpressionEvaluationException thrown on errors.
     * @since 2.0.1
     */
    private void addProjectDependencies(
        IPhpExecutableConfiguration execConfig,
        MavenProject project,
        final String targetScope,
        IDependencyConfiguration depConfig)
        throws ExpressionEvaluationException {
        final Set<Artifact> deps = project.getArtifacts();
        for (final Artifact dep : deps) {
            if (!targetScope.equals(dep.getScope())) {
                continue;
            }
            boolean foundDepConfig = false;
            for (final IDependency depC : depConfig.getDependencies()) {
                if (depC.getGroupId().equals(dep.getGroupId()) && depC.getArtifactId().equals(dep.getArtifactId())) {
                    foundDepConfig = true;
                }
            }
            if (foundDepConfig) {
                // was already be performed by addFromDepConfig()
                continue;
            }
            try {
                final MavenProject depProject = this.getProjectFromArtifact(project, dep);
                if (depProject.getFile() != null) {
                    // Reference to a local project; should only happen in IDEs or multi-project poms
                    final String depTargetDir = getClassesDirFromProject(depProject);
                    execConfig.getIncludePath().add(depTargetDir);
                }
            } catch (ProjectBuildingException ex) {
                throw new ExpressionEvaluationException("Problems creating maven project from dependency", ex);
            }
        }
    }

    private String getClassesDirFromProject(final MavenProject depProject) throws ExpressionEvaluationException {
        final MavenSession depSession = session.clone();
        depSession.setCurrentProject(depProject);
        final String depTargetDir = this.componentFactory.filterString(
                depSession,
                "${project.build.directory}/classes",
                File.class).getAbsolutePath();
        return depTargetDir;
    }
    
    /**
     * Returns the maven project from given artifact.
     * @param project the maven project
     * @param a artifact
     * @return maven project
     * @throws ProjectBuildingException thrown if there are problems creating the project
     * @since 2.0.1
     */
    protected MavenProject getProjectFromArtifact(final MavenProject project, final Artifact a)
        throws ProjectBuildingException {
        final ProjectBuildingRequest request = session.getProjectBuildingRequest();
        request.setLocalRepository(session.getLocalRepository());
        request.setRemoteRepositories(project.getRemoteArtifactRepositories());
        request.setResolveDependencies(false);
        request.setProcessPlugins(false);
        return this.mavenProjectBuilder.build(a, request).getProject();
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
            MavenSession mavenSession,
            IDependencyConfiguration depConfig)
        throws ExpressionEvaluationException, MojoExecutionException {
        execConfig.getIncludePath().add(project.getBuild().getTestOutputDirectory());
        File depsDir;
        if (buildConfig == null || buildConfig.getChild("testDependenciesDir") == null) {
            depsDir = this.componentFactory.filterString(
                    mavenSession,
                    "${project.build.directory}/php-test-deps",
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
        
        addFromDepConfig(execConfig, project, Artifact.SCOPE_TEST, depConfig, depsDir);
        
        // add the project dependencies of multi-project-poms
        addProjectDependencies(execConfig, project, Artifact.SCOPE_TEST, depConfig);
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
                    "${project.build.directory}/php-deps",
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
                    "${project.build.directory}/php-test-deps",
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
