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

package org.phpmaven.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.MavenArtifactRepository;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.artifact.resolver.filter.CumulativeScopeArtifactFilter;
import org.apache.maven.cli.MavenCli;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequestPopulator;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.FileUtils;
import org.apache.maven.it.util.ResourceExtractor;
import org.apache.maven.lifecycle.internal.LifecycleDependencyResolver;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.PluginParameterExpressionEvaluator;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuilder;
import org.apache.maven.settings.building.SettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuildingResult;
import org.codehaus.plexus.component.configurator.ComponentConfigurator;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.impl.internal.SimpleLocalRepositoryManager;
import org.sonatype.aether.util.DefaultRepositorySystemSession;
import org.sonatype.aether.util.graph.transformer.ChainedDependencyGraphTransformer;
import org.sonatype.aether.util.graph.transformer.ConflictMarker;
import org.sonatype.aether.util.graph.transformer.JavaDependencyContextRefiner;
import org.sonatype.aether.util.graph.transformer.JavaEffectiveScopeCalculator;
import org.sonatype.aether.util.graph.transformer.NearestVersionConflictResolver;

/**
 * Abstract base class for testing the modules.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public abstract class AbstractTestCase extends AbstractMojoTestCase {
    
    /**
     * Local repository directory.
     * @return local repos dir
     * @throws VerificationException thrown on verififcation errors.
     */
    protected File getLocalReposDir() throws VerificationException {
        final Verifier verifier = new Verifier("foo", true);
        return new File(verifier.localRepo);
    }

    /**
     * Creates a maven session with given test directory (name relative to package org/phpmaven/test/projects).
     * 
     * @param strTestDir the relative folder containing the pom.xml to be used
     * @return the maven session
     * @throws Exception thrown on errors
     */
    protected MavenSession createSimpleSession(final String strTestDir)
        throws Exception {
        final File testDir = prepareResources(strTestDir);
        final RepositorySystemSession systemSession = null;
        final MavenExecutionRequest request = new DefaultMavenExecutionRequest();
        final MavenExecutionResult result = null;
        final MavenSession session = new MavenSession(getContainer(), systemSession, request, result);
        final MavenProject project = buildProject(testDir);
        session.setCurrentProject(project);
        return session;
    }
    
    /**
     * Creates a mojo
     * @param clazz
     * @param groupId
     * @param config
     * @return mojo
     */
    protected <T extends Mojo> T createConfiguredMojo(Class<T> clazz, MavenSession session, String groupId, String artifactId, String version, String goal, Xpp3Dom config) throws Exception {
    	final PlexusConfiguration plexusConfig = new XmlPlexusConfiguration(config);
    	final T result = clazz.cast(lookupMojo(groupId, artifactId, version, goal, plexusConfig));
    	
        ExpressionEvaluator evaluator = new PluginParameterExpressionEvaluator( session, newMojoExecution(goal) );

        Xpp3Dom configuration = null;
        Plugin plugin = session.getCurrentProject().getPlugin( groupId + ":" + artifactId );
        if ( plugin != null )
        {
            configuration = (Xpp3Dom) plugin.getConfiguration();
        }
        if ( configuration == null )
        {
            configuration = new Xpp3Dom( "configuration" );
        }
        configuration = Xpp3Dom.mergeXpp3Dom( newMojoExecution(goal).getConfiguration(), configuration );

        PlexusConfiguration pluginConfiguration = new XmlPlexusConfiguration( configuration );

        getContainer().lookup( ComponentConfigurator.class, "basic" ).configureComponent( result, pluginConfiguration, evaluator, getContainer().getContainerRealm() );
        
        return result;
    }

    private MavenProject buildProject(final File projectDir) throws Exception {
        final File projectFile = new File(projectDir, "pom.xml");
        final ProjectBuildingRequest buildingRequest = new DefaultProjectBuildingRequest();
        buildingRequest.setProcessPlugins(false);
        buildingRequest.setResolveDependencies(false);
        final MavenProject project = lookup(ProjectBuilder.class).build(projectFile, buildingRequest).getProject();
        return project;
    }
    
    /**
     * Creates a maven session with an empty project pom.
     * @return the maven session
     * @throws Exception thrown on errors
     */
    protected MavenSession createSimpleEmptySession()
        throws Exception {
        return createSimpleSession("empty");
    }
    
    /**
     * Creates a maven session with given test directory (name relative to this class package).
     * 
     * @param strTestDir the relative folder containing the pom.xml to be used
     * @return the maven session
     * @throws Exception thrown on errors
     */
    protected MavenSession createSessionForPhpMaven(final String strTestDir)
        throws Exception {
        return createSessionForPhpMaven(strTestDir, false);
    }
    
    /**
     * Creates a maven session with given test directory (name relative to this class package).
     * 
     * @param strTestDir the relative folder containing the pom.xml to be used
     * @return the maven session
     * @throws Exception thrown on errors
     */
    protected MavenSession createSessionForPhpMaven(final String strTestDir, boolean resolveDependencies)
        throws Exception {
        return createSessionForPhpMaven(strTestDir, resolveDependencies, false);
    }
    
    protected static final class MavenData {
    	ProjectBuildingRequest projectBuildingRequest;
    	MavenExecutionRequest executionRequest;
    }
    
    protected MavenData createProjectBuildingRequest() throws Exception {
        final File localReposFile = this.getLocalReposDir();
        
        final SimpleLocalRepositoryManager localRepositoryManager = new SimpleLocalRepositoryManager(
                localReposFile);
        
        final DefaultRepositorySystemSession repositorySession = new DefaultRepositorySystemSession();
        for (final Map.Entry<Object, Object> entry : System.getProperties().entrySet()) {
            repositorySession.getSystemProperties().put(entry.getKey().toString(), entry.getValue().toString());
        }
        repositorySession.getSystemProperties().put("java.version", System.getProperty("java.version"));
        repositorySession.setDependencyGraphTransformer(new ChainedDependencyGraphTransformer( new ConflictMarker(), new JavaEffectiveScopeCalculator(),
                new NearestVersionConflictResolver(),
                new JavaDependencyContextRefiner() ));
        final MavenExecutionRequest request = new DefaultMavenExecutionRequest();
        final MavenExecutionRequestPopulator populator = lookup(MavenExecutionRequestPopulator.class);
        populator.populateDefaults(request);
        
        final SettingsBuildingRequest settingsRequest = new DefaultSettingsBuildingRequest();
        settingsRequest.setGlobalSettingsFile(MavenCli.DEFAULT_GLOBAL_SETTINGS_FILE);
        settingsRequest.setUserSettingsFile(MavenCli.DEFAULT_USER_SETTINGS_FILE);
        settingsRequest.setSystemProperties(request.getSystemProperties());
        settingsRequest.setUserProperties(request.getUserProperties());
        final SettingsBuilder settingsBuilder = lookup(SettingsBuilder.class);
        final SettingsBuildingResult settingsResult = settingsBuilder.build(settingsRequest);
        final MavenExecutionRequestPopulator executionRequestPopulator = lookup(MavenExecutionRequestPopulator.class);
        executionRequestPopulator.populateFromSettings(request, settingsResult.getEffectiveSettings());
        
        final ArtifactRepositoryLayout layout = lookup(ArtifactRepositoryLayout.class);
        final ArtifactRepositoryPolicy policy = new ArtifactRepositoryPolicy();
        final MavenArtifactRepository repos = new MavenArtifactRepository(
                "local",
                localReposFile.toURI().toURL().toString(),
                layout,
                policy,
                policy);
        
        request.setLocalRepository(repos);
        request.setSystemProperties(new Properties(System.getProperties()));
        request.getSystemProperties().put("java.version", System.getProperty("java.version"));
        repositorySession.setLocalRepositoryManager(localRepositoryManager);

        final ProjectBuildingRequest buildingRequest = new DefaultProjectBuildingRequest();
        buildingRequest.setLocalRepository(request.getLocalRepository());
        buildingRequest.setRepositorySession(repositorySession);
        buildingRequest.setSystemProperties(request.getSystemProperties());
        buildingRequest.getRemoteRepositories().addAll(request.getRemoteRepositories());
        buildingRequest.setProfiles(request.getProfiles());
        buildingRequest.setActiveProfileIds(request.getActiveProfiles());
        buildingRequest.setProcessPlugins(false);
        buildingRequest.setResolveDependencies(false);
        
        final MavenData data = new MavenData();
        data.executionRequest = request;
        data.projectBuildingRequest = buildingRequest;
        
        return data;
    }
    
    /**
     * Creates a maven session with given test directory (name relative to this class package).
     * 
     * @param strTestDir the relative folder containing the pom.xml to be used
     * @return the maven session
     * @throws Exception thrown on errors
     */
    protected MavenSession createSessionForPhpMaven(
            final String strTestDir,
            boolean resolveDependencies,
            boolean processPlugins)
        throws Exception {
        final File testDir = preparePhpMavenLocalRepos(strTestDir);
        final MavenExecutionResult result = null;
        final MavenData data = this.createProjectBuildingRequest();
        final ProjectBuildingRequest buildingRequest = data.projectBuildingRequest;
        final MavenExecutionRequest request = data.executionRequest;

        final File projectFile = new File(testDir, "pom.xml");
        final MavenProject project = lookup(ProjectBuilder.class).build(projectFile, buildingRequest).getProject();
        
        final MavenSession session = new MavenSession(getContainer(), buildingRequest.getRepositorySession(), request, result);
        session.setCurrentProject(project);
        
        return session;
    }

    /**
     * Prepares a local repository and installs the php-maven projects.
     * @param strTestDir The local test directory for the project to be tested
     * @return the file path to the local test installation
     * @throws Exception
     */
    private File preparePhpMavenLocalRepos(final String strTestDir)
        throws Exception {
        final File testDir = prepareResources(strTestDir);
        return testDir;
    }

    private File prepareResources(final String strTestDir) throws IOException {
        
        final String tempDirPath = System.getProperty(
               "maven.test.tmpdir", System.getProperty("java.io.tmpdir"));
        final File testDir = new File(tempDirPath, "org/phpmaven/test/projects/" + strTestDir);
        // try to delete it 5 times (sometimes it silently failes but succeeds the second incovation)
        for (int i = 0; i < 5; i++) {
            try {
                FileUtils.deleteDirectory(testDir);
            } catch (IOException ex) {
                if (i == 5) {
                    throw ex;
                }
            }
        }
        ResourceExtractor.extractResourcePath(
                getClass(),
                "/org/phpmaven/test/projects/" + strTestDir,
                new File(tempDirPath),
                true);
        
        return testDir;
    }

    protected void resolveProjectDependencies(MavenSession session) throws Exception {
        final List<String> scopesToResolve = new ArrayList<String>();
        scopesToResolve.add(Artifact.SCOPE_COMPILE);
        scopesToResolve.add(Artifact.SCOPE_TEST);
        final List<String> scopesToCollect = new ArrayList<String>();
        final LifecycleDependencyResolver lifeCycleDependencyResolver = lookup(LifecycleDependencyResolver.class);
        session.getCurrentProject().setArtifacts(null);
        lifeCycleDependencyResolver.resolveProjectDependencies(session.getCurrentProject(), scopesToCollect,
                scopesToResolve, session, false,
                Collections.<Artifact> emptySet());
        session.getCurrentProject().setArtifactFilter(new CumulativeScopeArtifactFilter(scopesToResolve));
    }
    
    protected void assertIterableCount(Iterable<?> iter, int count) {
    	int result = 0;
    	for (@SuppressWarnings("unused") final Object elm : iter) {
    		result++;
    	}
    	assertEquals(count, result);
    }
    
    protected <T> void assertIterableContains(Iterable<T> iter, T element) {
    	boolean found = false;
    	for (final T elm : iter) {
    		if (elm.equals(element)) {
    			found = true;
    			break;
    		}
    	}
    	if (!found) {
    		fail("Element " + element + " not found.");
    	}
    }

}