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

package org.phpmaven.mojos.test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.MavenArtifactRepository;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.cli.MavenCli;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequestPopulator;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;
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
    
    /**
     * Local repository directory.
     * @return local repos dir
     */
    private File getLocalReposDir() {
        final File tempDir = new File(System.getProperty("java.io.tmpdir", "/temp"));
        return new File(tempDir, "local-repos");
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
        final File testDir = ResourceExtractor.simpleExtractResources(
                getClass(), "/org/phpmaven/test/projects/" + strTestDir);
        final RepositorySystemSession systemSession = null;
        final MavenExecutionRequest request = new DefaultMavenExecutionRequest();
        final MavenExecutionResult result = null;
        final MavenSession session = new MavenSession(getContainer(), systemSession, request, result);
        final File projectFile = new File(testDir, "pom.xml");
        final ProjectBuildingRequest buildingRequest = new DefaultProjectBuildingRequest();
        final MavenProject project = lookup(ProjectBuilder.class).build(projectFile, buildingRequest).getProject();
        session.setCurrentProject(project);
        return session;
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
        request.getRemoteRepositories().add(userRepos);
        request.setSystemProperties(new Properties(System.getProperties()));
        request.getSystemProperties().put("java.version", System.getProperty("java.version"));
        repositorySession.setLocalRepositoryManager(localRepositoryManager);
        final MavenExecutionResult result = null;
        final MavenSession session = new MavenSession(getContainer(), repositorySession, request, result);
        final File projectFile = new File(testDir, "pom.xml");
        final ProjectBuildingRequest buildingRequest = new DefaultProjectBuildingRequest();
        buildingRequest.setLocalRepository(request.getLocalRepository());
        buildingRequest.setRepositorySession(repositorySession);
        buildingRequest.setSystemProperties(request.getSystemProperties());
        buildingRequest.getPluginArtifactRepositories().add(userRepos);
        buildingRequest.getRemoteRepositories().addAll(request.getRemoteRepositories());
        buildingRequest.setProfiles(request.getProfiles());
        buildingRequest.setActiveProfileIds(request.getActiveProfiles());
        buildingRequest.setProcessPlugins(false);
        buildingRequest.setResolveDependencies(false);

        final MavenProject project = lookup(ProjectBuilder.class).build(projectFile, buildingRequest).getProject();
        session.setCurrentProject(project);
        
        return session;
    }

    /**
     * Prepares a local repository and installs the php-maven projects.
     * @param strTestDir The local test directory for the project to be tested
     * @return the file path to the local test installation
     * @throws IOException
     * @throws VerificationException
     */
    private File preparePhpMavenLocalRepos(final String strTestDir)
        throws IOException, VerificationException {
        final File testDir = ResourceExtractor.simpleExtractResources(
                getClass(), "/org/phpmaven/test/projects/" + strTestDir);
        
        final String[] pomsToInstall = new String[]{
            // the common parent
            "../../../build/common-parent",
            // generics
            "../../../var/generic-parent-tags",
            "../../../var/generic-parent-branches",
            "../generic-parent",
            // php-parents
            "../php-parent-pom",
            // java-generics
            "../../../var/java-parent",
            "../../../var/java-parent-branches",
            "../../../var/java-parent-tags",
            "../../../var/java-resources",
            "../java-parent",
            // java-projects
            "../maven-php-core",
            "../maven-php-exec",
            "../maven-php-project",
            "../maven-php-pear",
            "../maven-php-phar",
            "../maven-php-phpunit",
            "../maven-php-phpdoc",
            "../maven-php-validate-lint",
            "../maven-php-plugin"
        };
        
        for (final String pom : pomsToInstall) {
            this.installToRepos(getLocalReposDir().getAbsolutePath(), pom);
        }
        return testDir;
    }
    
    /**
     * Prepares a verifier and installs php maven to a local repository.
     * @param strTestDir strTestDir The local test directory for the project to be tested
     * @return The verifier to be used for testing
     * @throws VerificationException 
     * @throws IOException 
     */
    protected Verifier getPhpMavenVerifier(final String strTestDir)
        throws IOException, VerificationException {
        final File testDir = this.preparePhpMavenLocalRepos(strTestDir);
        final File localReposFile = this.getLocalReposDir();
        final Verifier verifier = new Verifier(testDir.getAbsolutePath());
        verifier.setLocalRepo(localReposFile.getAbsolutePath());
        return verifier;
    }
    
    /**
     * see http://mrpmorris.blogspot.com/2007/05/convert-absolute-path-to-relative-path.html for
     * details.
     * @param absolutePath
     * @param relativeTo
     * @return
     */
    private String relativePath(String absolutePath, String relativeTo) {
        final String[] absoluteDirectories = absolutePath.split("[\\\\\\/]");
        final String[] relativeDirectories = relativeTo.split("[\\\\\\/]");
        
        // Get the shortest of the two paths
        final int length = absoluteDirectories.length < relativeDirectories.length ?
                absoluteDirectories.length : relativeDirectories.length;
        // Use to determine where in the loop we exited
        int lastCommonRoot = -1;
        int index;
        // Find common root 
        for (index = 0; index < length; index++) {
            if (absoluteDirectories[index].equals(relativeDirectories[index])) {
                lastCommonRoot = index;
            } else {
                break; 
            }
        }
        
        // If we didn't find a common prefix then throw
        if (lastCommonRoot == -1) {
            throw new IllegalArgumentException("Paths do not have a common base");
        }
        
        // Build up the relative path
        final StringBuilder relativePath = new StringBuilder();
        // Add on the ..
        for (index = lastCommonRoot + 1; index < absoluteDirectories.length; index++) {
            if (absoluteDirectories[index].length() > 0) {
                relativePath.append("..\\");
            }
        }
        
        // Add on the folders
        for (index = lastCommonRoot + 1; index < relativeDirectories.length - 1; index++) {
            relativePath.append(relativeDirectories[index] + "\\");
        }
        
        relativePath.append(relativeDirectories[relativeDirectories.length - 1]);
        return relativePath.toString();
    }
    
    /**
     * Installs the project identified by given pom to the local repository.
     * @param reposPath the repository path
     * @param pom path to local pom that will be installed; a relative path from current project root
     * @throws VerificationException thrown if the given pom cannot be installed
     * @throws IOException 
     */
    @SuppressWarnings("unchecked")
    private void installToRepos(final String reposPath, String pom)
        throws VerificationException, IOException {
        File root;
        try {
            root = new File(
                    this.getClass().getResource("/META-INF/plexus/components.xml").toURI()).
                    getParentFile().getParentFile().getParentFile().getParentFile().getParentFile();
        } catch (URISyntaxException e) {
            throw new VerificationException(e);
        }
        
        final File pomFile = new File(root, pom);
        final File logFile = new File(root, "../../../dev/log.txt");
        final String relLogPath = this.relativePath(pomFile.getCanonicalPath(), logFile.getCanonicalPath()); 
        
        final Verifier verifier = new Verifier(pomFile.getAbsolutePath());
        verifier.setLocalRepo(reposPath);
        verifier.setAutoclean(true);
        verifier.setForkJvm(true);
        verifier.setLogFileName(relLogPath);
        verifier.getCliOptions().add("-P");
        verifier.getCliOptions().add("php-maven-testing-profile");
//        verifier.getCliOptions().add("-Dmaven.repo.remote=" + (
//                new File(MavenCli.userMavenConfigurationHome, "/repository").toURI().toURL().toString()));
        verifier.executeGoal("install");
        // verifier.verifyErrorFreeLog();
        verifier.resetStreams();
    }

}