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
import org.apache.maven.model.Dependency;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuilder;
import org.apache.maven.settings.building.SettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuildingResult;
import org.codehaus.plexus.PlexusTestCase;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.impl.internal.SimpleLocalRepositoryManager;
import org.sonatype.aether.util.DefaultRepositorySystemSession;

/**
 * Abstract base class for testing the modules.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public abstract class AbstractTestCase extends PlexusTestCase {
    
    /** list of already installed project versions */
    private static List<String> installedProjects = new ArrayList<String>();
    
    /**
     * Returns true for hudson builds. That causes the test case to assume that we have a proper local repository
     * where all maven plugins are present. So let us not create our own local repository (much more cheeper).
     * For development builds in eclipse this will result in using older snapshots from repository. So in eclipse
     * you should not use this flag to let the test cases always create their own repository (much more time
     * consuming).
     * @return true for hudson builds.
     */
    protected boolean isHudsonBuild() {
        return "1".equals(System.getProperty("phpmaven.hudsonintegration", "0"));
    }
    
    /**
     * Local repository directory.
     * @return local repos dir
     * @throws VerificationException thrown on verififcation errors.
     */
    protected File getLocalReposDir() throws VerificationException {
        if (this.isHudsonBuild()) {
            // in hudson builds we always use the local repository from our hudson.
            final Verifier verifier = new Verifier("foo", true);
            return new File(verifier.localRepo);
        }
        final File tempDir = new File(System.getProperty("java.io.tmpdir", "/temp"));
        return new File(tempDir, "local-repos");
    }
    
    /**
     * Local log file.
     * @return local log file
     */
    protected File getLocalLogFile() {
        final File tempDir = new File(System.getProperty("java.io.tmpdir", "/temp"));
        return new File(tempDir, "log.txt");
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
        
        final File localReposFile = this.getLocalReposDir();
        
        final SimpleLocalRepositoryManager localRepositoryManager = new SimpleLocalRepositoryManager(
                localReposFile);
        
        final DefaultRepositorySystemSession repositorySession = new DefaultRepositorySystemSession();
        for (final Map.Entry<Object, Object> entry : System.getProperties().entrySet()) {
            repositorySession.getSystemProperties().put(entry.getKey().toString(), entry.getValue().toString());
        }
        repositorySession.getSystemProperties().put("java.version", System.getProperty("java.version"));
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
        final MavenArtifactRepository userRepos = new MavenArtifactRepository(
                "user",
                new File(MavenCli.userMavenConfigurationHome, "/repository").toURI().toURL().toString(),
                layout,
                policy,
                policy);
        
        request.setLocalRepository(repos);
        // skip wrapping local repository as a remote one for hudson
        if (!this.isHudsonBuild()) {
            request.getRemoteRepositories().add(userRepos);
        }
        request.setSystemProperties(new Properties(System.getProperties()));
        request.getSystemProperties().put("java.version", System.getProperty("java.version"));
        repositorySession.setLocalRepositoryManager(localRepositoryManager);
        final MavenExecutionResult result = null;
        final File projectFile = new File(testDir, "pom.xml");
        final ProjectBuildingRequest buildingRequest = new DefaultProjectBuildingRequest();
        buildingRequest.setLocalRepository(request.getLocalRepository());
        buildingRequest.setRepositorySession(repositorySession);
        buildingRequest.setSystemProperties(request.getSystemProperties());
        // skip wrapping local repository as a remote one for hudson
        if (!this.isHudsonBuild()) {
            buildingRequest.getPluginArtifactRepositories().add(userRepos);
        }
        buildingRequest.getRemoteRepositories().addAll(request.getRemoteRepositories());
        buildingRequest.setProfiles(request.getProfiles());
        buildingRequest.setActiveProfileIds(request.getActiveProfiles());
        buildingRequest.setProcessPlugins(processPlugins);
        buildingRequest.setResolveDependencies(resolveDependencies);

        final MavenProject project = lookup(ProjectBuilder.class).build(projectFile, buildingRequest).getProject();
        
        final MavenSession session = new MavenSession(getContainer(), repositorySession, request, result);
        session.setCurrentProject(project);
        
        return session;
    }
    
    protected void installPhpParentPom() throws Exception {
        // skip installing of projects for hudson build
        if (!this.isHudsonBuild()) {
            final String localRepos = getLocalReposDir().getAbsolutePath();
            String rootPath = new File(".").getAbsolutePath();
            rootPath = rootPath.endsWith(".") ? rootPath.substring(0, rootPath.length() - 2) : rootPath;
            rootPath = new File(new File(rootPath).getParentFile(), "php-parent-pom").getAbsolutePath();
            this.installDirToRepos(localRepos, rootPath);
            this.installLocalProject(localRepos, new File(rootPath).getParent() + "/generic-parent", false);
        }
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
        
        // skip installing of projects for hudson build
        if (!this.isHudsonBuild()) {
            final String localRepos = getLocalReposDir().getAbsolutePath();
            final String rootPath = new File(".").getAbsolutePath();
            this.installDirToRepos(localRepos, rootPath.endsWith(".") ? rootPath.substring(0, rootPath.length() - 2) : rootPath);
        }
        return testDir;
    }
    
    /**
     * Installs the archetypes
     * @throws Exception
     */
    protected void installArchetypes() throws Exception {
        final String localRepos = getLocalReposDir().getAbsolutePath();
        final String rootPath = new File(".").getAbsolutePath();
        final File rootFile = new File(rootPath.endsWith(".") ? rootPath.substring(0, rootPath.length() - 2) : rootPath);
        this.installLocalProject(localRepos, rootFile.getParent() + "/archetypes", true);
    }
    
    /**
     * Returns the test dir.
     * @param strTestDir local path to test folder.
     * @return absolute test dir in temporary path.
     * @throws IOException io exception.
     */
    protected File getTestDir(String strTestDir) throws IOException {
        final String tempDirPath = System.getProperty(
                "maven.test.tmpdir", System.getProperty("java.io.tmpdir"));
        final File testDir = new File(tempDirPath, "org/phpmaven/test/projects/" + strTestDir);
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
    
    /**
     * Prepares a verifier and installs php maven to a local repository.
     * @param strTestDir strTestDir The local test directory for the project to be tested
     * @return The verifier to be used for testing
     * @throws Exception 
     */
    protected Verifier getPhpMavenVerifier(final String strTestDir)
        throws Exception {
        final File testDir = this.preparePhpMavenLocalRepos(strTestDir);
        final File localReposFile = this.getLocalReposDir();
        final Verifier verifier = new Verifier(testDir.getAbsolutePath(), true);
        verifier.setLocalRepo(localReposFile.getAbsolutePath());
        verifier.addCliOption("-nsu");
        verifier.setForkJvm(true);
        return verifier;
    }
    
    /**
     * Prepares a verifier and installs php maven to a local repository.
     * @param strTestDir strTestDir The local test directory for the project to be tested
     * @return The verifier to be used for testing
     * @throws VerificationException 
     * @throws IOException 
     */
    protected Verifier getVerifier(final String strTestDir)
        throws IOException, VerificationException {
        final File testDir = this.prepareResources(strTestDir);
        final File localReposFile = this.getLocalReposDir();
        final Verifier verifier = new Verifier(testDir.getAbsolutePath(), true);
        verifier.setLocalRepo(localReposFile.getAbsolutePath());
        verifier.addCliOption("-nsu");
        verifier.setForkJvm(true);
        return verifier;
    }
    
    /**
     * Prepares a verifier and installs php maven to a local repository.
     * @param strTestDir strTestDir The local test directory for the project to be tested
     * @return The verifier to be used for testing
     * @throws VerificationException 
     * @throws IOException 
     */
    protected Verifier getVerifierWithoutPrepare(final String strTestDir)
        throws IOException, VerificationException {
        final File testDir = this.getTestDir(strTestDir);
        final File localReposFile = this.getLocalReposDir();
        final Verifier verifier = new Verifier(testDir.getAbsolutePath(), true);
        verifier.setLocalRepo(localReposFile.getAbsolutePath());
        verifier.addCliOption("-nsu");
        verifier.setForkJvm(true);
        return verifier;
    }
    
    /**
     * Installs the project identified by given pom to the local repository.
     * @param reposPath the repository path
     * @param root path to local pom that will be installed; an absolute path
     * @throws Exception thrown if the given pom cannot be installed
     */
    protected void installDirToRepos(final String reposPath, String root)
        throws Exception {
        
        // do not install in hudson builds
        if (this.isHudsonBuild()) {
            return;
        }
        
        final File phpmavenDir = new File(reposPath + "/org/phpmaven");
        if (phpmavenDir.exists() && installedProjects.isEmpty()) {
            FileUtils.deleteDirectory(phpmavenDir);
        }
        
        installLocalProject(reposPath, root, false);
    }
    
    protected void installPhpmavenProjectToRepos(String prjName) throws Exception{// skip installing of projects for hudson build
        if (!this.isHudsonBuild()) {
            final String localRepos = getLocalReposDir().getAbsolutePath();
            String rootPath = new File(".").getAbsolutePath();
            rootPath = rootPath.endsWith(".") ? rootPath.substring(0, rootPath.length() - 2) : rootPath;
            rootPath = new File(new File(rootPath).getParentFile(), prjName).getAbsolutePath();
            this.installDirToRepos(localRepos, rootPath);
        }
    }

    /**
     * Installs a local project into target directory
     * @param reposPath repos path
     * @param root root of the project to be installed
     * @param withModules
     * @throws Exception
     */
    private void installLocalProject(final String reposPath, String root, boolean withModules) throws Exception {
        final String projectName = new File(root).getName();
        if (installedProjects.contains(projectName)) {
            return;
        }
        installedProjects.add(projectName);
        
        if (!projectName.equals("var")) {
            this.installLocalProject(reposPath, new File(root).getParentFile().getParentFile().getParent() + "/var", false);
            this.installLocalProject(reposPath, new File(root).getParent() + "/java-parent", false);
        }
        
        // first install dependencies
        final File projectFile = new File(root, "pom.xml");
        final ProjectBuildingRequest buildingRequest = new DefaultProjectBuildingRequest();
        buildingRequest.setLocalRepository(null);
        buildingRequest.setRepositorySession(null);
        buildingRequest.setSystemProperties(null);
        // buildingRequest.getRemoteRepositories().addAll(request.getRemoteRepositories());
        buildingRequest.setProfiles(null);
        buildingRequest.setActiveProfileIds(null);
        buildingRequest.setProcessPlugins(false);
        buildingRequest.setResolveDependencies(false);

        final MavenProject project = lookup(ProjectBuilder.class).build(projectFile, buildingRequest).getProject();
        
        for (final Dependency dep : project.getDependencies()) {
            if ("org.phpmaven".equals(dep.getGroupId())) {
                this.installLocalProject(reposPath, new File(root).getParent() + "/" + dep.getArtifactId(), false);
            }
        }
        
        // install the project itself
        final Verifier verifier = new Verifier(root, true);
        verifier.setLocalRepo(reposPath);
        verifier.setAutoclean(false);
        verifier.setForkJvm(true);
        final File target = new File(root, "target");
        if (!target.exists()) {
            target.mkdir();
        }
        verifier.setLogFileName("target/log.txt");
        verifier.addCliOption("-N");
        verifier.addCliOption("-nsu");
        verifier.addCliOption("-Dmaven.test.skip=true");
        verifier.executeGoal("install");
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();
        
        if (withModules) {
            for (final String module : project.getModules()) {
                this.installLocalProject(reposPath, new File(root) + "/" + module, false);
            }
        }
    }

    protected void resolveProjectDependencies(MavenSession session) throws Exception {
        final List<String> scopesToResolve = new ArrayList<String>();
        scopesToResolve.add(Artifact.SCOPE_COMPILE);
        scopesToResolve.add(Artifact.SCOPE_TEST);
        final List<String> scopesToCollect = new ArrayList<String>();
        final LifecycleDependencyResolver lifeCycleDependencyResolver = lookup(LifecycleDependencyResolver.class);
        session.getCurrentProject().setArtifacts(null);
        session.getCurrentProject().setArtifactFilter(new CumulativeScopeArtifactFilter(scopesToResolve));
        lifeCycleDependencyResolver.resolveProjectDependencies(session.getCurrentProject(), scopesToCollect,
                scopesToResolve, session, false,
                Collections.<Artifact> emptySet());
    }

}