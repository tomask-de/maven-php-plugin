/*******************************************************************************
 * Copyright (c) 2011 PHP-Maven.org
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     PHP-Maven.org
 *******************************************************************************/

package org.phpmaven.eclipse.core;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Extension;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IBuildpathContainer;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.search.DLTKSearchParticipant;
import org.eclipse.dltk.core.search.IDLTKSearchConstants;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.core.search.SearchEngine;
import org.eclipse.dltk.core.search.SearchMatch;
import org.eclipse.dltk.core.search.SearchParticipant;
import org.eclipse.dltk.core.search.SearchPattern;
import org.eclipse.dltk.core.search.SearchRequestor;
import org.eclipse.dltk.internal.ui.search.DLTKSearchScopeFactory;
import org.eclipse.dltk.ui.search.PatternQuerySpecification;
import org.eclipse.dltk.utils.ResourceUtil;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.php.internal.core.PHPLanguageToolkit;
import org.eclipse.php.internal.core.PHPVersion;
import org.eclipse.php.internal.core.includepath.IncludePath;
import org.eclipse.php.internal.core.includepath.IncludePathManager;
import org.eclipse.php.internal.core.language.LanguageModelInitializer;
import org.eclipse.php.internal.core.project.PHPNature;
import org.eclipse.php.internal.core.project.ProjectOptions;

/**
 * Utility class
 * 
 * @author Martin Eisengardt
 */
@SuppressWarnings("restriction")
public class MavenPhpUtils {
    
    /** content type for php files */
    public final static String ContentTypeID_PHP = "org.eclipse.php.core.phpsource"; //$NON-NLS-1$
    
    /** The pom.xml initial file contents */
    private static final String POM_CONTENTS = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + //$NON-NLS-1$
            "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " + //$NON-NLS-1$
            "xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" + //$NON-NLS-1$
            "  <modelVersion>4.0.0</modelVersion>\n" + //$NON-NLS-1$
            "  <groupId>${groupId}</groupId>\n" + //$NON-NLS-1$
            "  <artifactId>${artifactId}</artifactId>\n" + //$NON-NLS-1$
            "  <version>${version}</version>\n" + //$NON-NLS-1$
            "  <packaging>php</packaging>\n" + //$NON-NLS-1$
            "  <name>Sample php-maven project</name>\n" + //$NON-NLS-1$
            "  <description>The generic parent pom for php projects</description>\n" + //$NON-NLS-1$
            "\n" + //$NON-NLS-1$
            "  <parent>\n" + //$NON-NLS-1$
            "    <groupId>org.phpmaven</groupId>\n" + //$NON-NLS-1$
            "    <artifactId>php-parent-pom</artifactId>\n" + //$NON-NLS-1$
            "    <version>2.0.0</version>\n" + //$NON-NLS-1$
            "  </parent>\n" + //$NON-NLS-1$
            "\n" + //$NON-NLS-1$
            "    <properties>\n" + //$NON-NLS-1$
            "        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>\n" + //$NON-NLS-1$
            "        <phpunit.version>3.6.10</phpunit.version>\n" + //$NON-NLS-1$
            "    </properties>\n" + //$NON-NLS-1$
            "\n" + //$NON-NLS-1$
            "    <build>\n" + //$NON-NLS-1$
            "        <plugins>\n" + //$NON-NLS-1$
            "            <plugin>\n" + //$NON-NLS-1$
            "                <groupId>org.phpmaven</groupId>\n" + //$NON-NLS-1$
            "                <artifactId>maven-php-plugin</artifactId>\n" + //$NON-NLS-1$
            "                <version>${phpmaven.plugin.version}</version>\n" + //$NON-NLS-1$
            "                <configuration></configuration>\n" + //$NON-NLS-1$
            "            </plugin>\n" + //$NON-NLS-1$
            "            <plugin>\n" + //$NON-NLS-1$
            "                <groupId>org.apache.maven.plugins</groupId>\n" + //$NON-NLS-1$
            "                <artifactId>maven-site-plugin</artifactId>\n" + //$NON-NLS-1$
            "                <version>3.0</version>\n" + //$NON-NLS-1$
            "                <inherited>true</inherited>\n" + //$NON-NLS-1$
            "                <configuration>\n" + //$NON-NLS-1$
            "                    <reportPlugins>\n" + //$NON-NLS-1$
            "                        <plugin>\n" + //$NON-NLS-1$
            "                            <groupId>org.phpmaven</groupId>\n" + //$NON-NLS-1$
            "                            <artifactId>maven-php-plugin</artifactId>\n" + //$NON-NLS-1$
            "                            <reportSets>\n" + //$NON-NLS-1$
            "                                <reportSet>\n" + //$NON-NLS-1$
            "                                    <reports>\n" + //$NON-NLS-1$
            "                                        <report>phpdocumentor</report>\n" + //$NON-NLS-1$
            "                                        <report>phpunit</report>\n" + //$NON-NLS-1$
            "                                        <report>phpunit-coverage</report>\n" + //$NON-NLS-1$
            "                                    </reports>\n" + //$NON-NLS-1$
            "                                </reportSet>\n" + //$NON-NLS-1$
            "                            </reportSets>\n" + //$NON-NLS-1$
            "                        </plugin>\n" + //$NON-NLS-1$
            "                    </reportPlugins>\n" + //$NON-NLS-1$
            "                </configuration>\n" + //$NON-NLS-1$
            "            </plugin>\n" + //$NON-NLS-1$
            "        </plugins>\n" + //$NON-NLS-1$
            "    </build>\n" + //$NON-NLS-1$
            "\n" + //$NON-NLS-1$
            "    <dependencies>\n" + //$NON-NLS-1$
            "      <dependency>\n" + //$NON-NLS-1$
            "        <groupId>de.phpunit</groupId>\n" + //$NON-NLS-1$
            "        <artifactId>PHPUnit</artifactId>\n" + //$NON-NLS-1$
            "        <version>${phpunit.version}</version>\n" + //$NON-NLS-1$
            "        <type>phar</type>\n" + //$NON-NLS-1$
            "        <scope>test</scope>\n" + //$NON-NLS-1$
            "      </dependency>\n" + //$NON-NLS-1$
            "    </dependencies>\n" + //$NON-NLS-1$
            "</project>"; //$NON-NLS-1$
    
    /**
     * Creates a search scope for php to search within project
     * 
     * @param project
     * @return search scope
     */
    public static IDLTKSearchScope createProjectScope(final IScriptProject project) {
        final DLTKSearchScopeFactory factory = DLTKSearchScopeFactory.getInstance();
        return factory.createProjectSearchScope(project, false);
    }
    
    /**
     * Creates a workspace search scope
     * 
     * @return search scope
     */
    public static IDLTKSearchScope createWorkspaceScope() {
        final DLTKSearchScopeFactory factory = DLTKSearchScopeFactory.getInstance();
        return factory.createWorkspaceScope(false, PHPLanguageToolkit.getDefault());
    }
    
    /**
     * Searches for the given php class within the workspace
     * 
     * @param className
     * @return search matches
     */
    public static SearchMatch[] findClass(final String className) {
        return MavenPhpUtils.findClass(className, MavenPhpUtils.createWorkspaceScope());
    }
    
    /**
     * Searches for the given php class within the workspace by using the given
     * matching rule
     * 
     * @param className
     * @param matchRule
     * @return search matches
     */
    public static SearchMatch[] findClass(final String className, final int matchRule) {
        return MavenPhpUtils.findClass(className, MavenPhpUtils.createWorkspaceScope(), matchRule);
    }
    
    /**
     * Searches for the given php class within the given search scope
     * 
     * @param className
     * @param scope
     * @return search matches
     */
    public static SearchMatch[] findClass(final String className, final IDLTKSearchScope scope) {
        return MavenPhpUtils.findClass(className, scope, SearchPattern.R_EXACT_MATCH);
    }
    
    /**
     * Searches for the given php class within the given search scope and by
     * using the given mathing rule
     * 
     * @param className
     * @param scope
     * @param matchRule
     * @return search matches
     */
    public static SearchMatch[] findClass(final String className, final IDLTKSearchScope scope, final int matchRule) {
        final PatternQuerySpecification querySpec = new PatternQuerySpecification(className, IDLTKSearchConstants.TYPE, false, IDLTKSearchConstants.DECLARATIONS, scope, ""); //$NON-NLS-1$
        
        final SearchPattern pattern = SearchPattern.createPattern(querySpec.getPattern(), querySpec.getSearchFor(), querySpec.getLimitTo(), matchRule, scope.getLanguageToolkit());
        
        return MavenPhpUtils.findMatches(pattern, scope);
    }
    
    /**
     * Finds matches
     * 
     * @param pattern
     * @param scope
     * @return matches
     */
    private static SearchMatch[] findMatches(final SearchPattern pattern, final IDLTKSearchScope scope) {
        final SearchEngine engine = new SearchEngine();
        try {
            final PHPClassSearchRequestor requestor = new PHPClassSearchRequestor();
            engine.search(pattern, new SearchParticipant[] { new DLTKSearchParticipant() }, scope, requestor, new NullProgressMonitor());
            
            return requestor.getMatches();
            
        } catch (final CoreException e) {
            PhpmavenCorePlugin.logError("Error searching for php class", e); //$NON-NLS-1$
        }
        
        return new SearchMatch[0];
    }
    
    /**
     * Tests if a project is a php project
     * 
     * @param project
     * 
     * @return true if given project is a php project
     */
    public static boolean isPHPProject(final IProject project) {
        final IDLTKLanguageToolkit languageToolkit = DLTKLanguageManager.findToolkit(project);
        if (languageToolkit != null) {
            return PHPNature.ID.equals(languageToolkit.getNatureId());
        }
        return false;
    }
    
    /**
     * Tests if a project is a php project
     * 
     * @param project
     * 
     * @return true if given project is a php project
     */
    public static boolean isPHPProject(final IScriptProject project) {
        final String nature = MavenPhpUtils.getNatureFromProject(project);
        return PHPNature.ID.equals(nature);
    }
    
    /**
     * Returns the nature of given script project
     * 
     * @param project
     * 
     * @return nature
     */
    private static String getNatureFromProject(final IScriptProject project) {
        final IDLTKLanguageToolkit languageToolkit = DLTKLanguageManager.getLanguageToolkit(project);
        if (languageToolkit != null) {
            return languageToolkit.getNatureId();
        }
        return null;
    }
    
    /**
     * Tests if a project is a maven project
     * 
     * @param scriptProject
     * 
     * @return true if given project is a nature project
     */
    public static boolean isMavenProject(final IScriptProject scriptProject) {
        return MavenPhpUtils.isMavenProject(scriptProject.getProject());
    }
    
    /**
     * Tests if a project is a maven project
     * 
     * @param project
     * 
     * @return true if given project is a nature project
     */
    public static boolean isMavenProject(final IProject project) {
        try {
            return project.hasNature(IMavenConstants.NATURE_ID);
        } catch (final CoreException e) {
            PhpmavenCorePlugin.logError("Problems testing project nature", e); //$NON-NLS-1$
            return false;
        }
    }
    
    /**
     * Tests if a project is a php maven project
     * 
     * @param project
     * 
     * @return true if given project is a nature project
     */
    public static boolean isPhpmavenProject(final IProject project) {
        try {
            return project.hasNature(PhpmavenCorePlugin.PHPMAVEN_NATURE_ID);
        } catch (final CoreException e) {
            PhpmavenCorePlugin.logError("Problems testing project nature", e); //$NON-NLS-1$
            return false;
        }
    }
    
    /**
     * Returns the dependent projects; such projects having a dependency to
     * given project.
     * 
     * @param project
     *            project to look for
     * @return set of projects having a dependency to given project
     */
    public static Set<IProject> getDependingProjects(final IProject project) {
        final Set<IProject> result = new HashSet<IProject>();
        for (final IProject target : project.getWorkspace().getRoot().getProjects()) {
            if (!project.equals(target) && target.isOpen() && target.exists() && MavenPhpUtils.isPHPProject(target) && MavenPhpUtils.isMavenProject(target)) {
                // we will have a look at the build container
                final IScriptProject scriptTarget = DLTKCore.create(target);
                try {
                    final IBuildpathContainer container = DLTKCore.getBuildpathContainer(new Path(PhpmavenCorePlugin.BUILDPATH_CONTAINER_ID), scriptTarget);
                    if (container != null) {
                        for (final IBuildpathEntry entry : container.getBuildpathEntries()) {
                            if (entry.getEntryKind() == IBuildpathEntry.BPE_PROJECT) {
                                if (entry.getPath().equals(project.getFullPath())) {
                                    result.add(target);
                                    break;
                                }
                            }
                        }
                    }
                } catch (final ModelException ex) {
                    PhpmavenCorePlugin.logError("Exception while analyzing the projct dependencies.", ex); //$NON-NLS-1$
                }
            }
        }
        return result;
    }
    
    /**
     * Returns the projects the given one is depending on.
     * 
     * @param project
     *            project to look for
     * @return set of projects being a dependency of given project
     */
    public static Set<IProject> getProjectDependies(final IProject project) {
        final Set<IProject> result = new HashSet<IProject>();
        if (project.isOpen() && project.exists() && MavenPhpUtils.isPHPProject(project) && MavenPhpUtils.isMavenProject(project)) {
            // we will have a look at the build container
            final IScriptProject scriptTarget = DLTKCore.create(project);
            try {
                final IBuildpathContainer container = DLTKCore.getBuildpathContainer(new Path(PhpmavenCorePlugin.BUILDPATH_CONTAINER_ID), scriptTarget);
                if (container != null) {
                    for (final IBuildpathEntry entry : container.getBuildpathEntries()) {
                        if (entry.getEntryKind() == IBuildpathEntry.BPE_PROJECT) {
                            result.add(ResourcesPlugin.getWorkspace().getRoot().getProject(entry.getPath().lastSegment()));
                        }
                    }
                }
            } catch (final ModelException ex) {
                PhpmavenCorePlugin.logError("Exception while analyzing the projct dependencies.", ex); //$NON-NLS-1$
            }
        }
        return result;
    }
    
    /**
     * Fetches and returns the maven project facade for given eclipse project
     * 
     * @param project
     *            eclipse project
     * @return maven project facade
     */
    public static IMavenProjectFacade fetchProjectFacade(final IProject project) {
        return new FetchMavenProject().fetch(project);
    }
    
    /**
     * Fetches and returns the maven project facade.
     * @param project eclipse project
     * @param facade maven project facade
     * @return site output folder.
     */
    public static IFolder getSiteOutputFolder(final IProject project, IMavenProjectFacade facade) {
        // TODO
        return project.getFolder("target/site"); //$NON-NLS-1$
    }
    
    /**
     * Fetches the php-maven project config
     * 
     * @param project
     * @return php-maven config
     * @throws CoreException
     *             thrown on errors
     */
    public static PhpmavenBuildConfig fetchConfig(final IProject project) throws CoreException {
        return MavenPhpUtils.fetchConfig(project, MavenPhpUtils.fetchProjectFacade(project));
    }
    
    /**
     * Fetches the php-maven project config
     * 
     * @param project
     * @param facade
     * @return php-maven config
     * @throws CoreException
     *             thrown on errors
     */
    public static PhpmavenBuildConfig fetchConfig(final IProject project, final IMavenProjectFacade facade) throws CoreException {
        final PhpmavenBuildConfig result = new PhpmavenBuildConfig();
        for (final Plugin plugin : facade.getMavenProject().getBuild().getPlugins()) {
            if ("org.phpmaven".equals(plugin.getGroupId()) && "maven-php-plugin".equals(plugin.getArtifactId())) { //$NON-NLS-1$//$NON-NLS-2$
            
                final IMaven maven = MavenPlugin.getMaven();
                final MavenExecutionRequest mavenExecutionRequest = maven.createExecutionRequest(new NullProgressMonitor());
                final MavenSession session = maven.createSession(mavenExecutionRequest, facade.getMavenProject());
                final File dependenciesTargetDirectory = maven.getMojoParameterValue("dependenciesTargetDirectory", File.class, session, plugin, plugin, "resources"); //$NON-NLS-1$//$NON-NLS-2$
                final File testDependenciesTargetDirectory = maven.getMojoParameterValue("testDependenciesTargetDirectory", File.class, session, plugin, plugin, "resources"); //$NON-NLS-1$//$NON-NLS-2$
                
                // the output directory we find may be mapped outside in another
                // project (maybe a multi project layout)
                if (dependenciesTargetDirectory != null) {
                    for (final IContainer depTarget : ResourcesPlugin.getWorkspace().getRoot().findContainersForLocationURI(dependenciesTargetDirectory.toURI())) {
                        if (depTarget.getProject().equals(project)) {
                            result.setDepsFolder((IFolder) depTarget);
                            break;
                        }
                    }
                }
                
                if (testDependenciesTargetDirectory != null) {
                    for (final IContainer depTarget : ResourcesPlugin.getWorkspace().getRoot().findContainersForLocationURI(testDependenciesTargetDirectory.toURI())) {
                        if (depTarget.getProject().equals(project)) {
                            result.setTestDepsFolder((IFolder) depTarget);
                            break;
                        }
                    }
                }
                
                break; // do not look further
            }
        }
        
        // apply defaults if we did not get any onfiguration option
        if (result.getDepsFolder() == null) {
            result.setDepsFolder(project.getFolder(new Path("target/php-deps"))); //$NON-NLS-1$
        }
        if (result.getTestDepsFolder() == null) {
            result.setTestDepsFolder(project.getFolder(new Path("target/php-test-deps"))); //$NON-NLS-1$
        }
        
        return result;
    }
    
    /**
     * Project config related to php-maven
     */
    public static final class PhpmavenBuildConfig {
        
        /**
         * The php dependencies folder
         */
        private IFolder depsFolder;
        
        /**
         * The php test dependencies folder
         */
        private IFolder testDepsFolder;
        
        /**
         * Returns the php dependencies folder
         * 
         * @return php dependencies folder
         */
        public IFolder getDepsFolder() {
            return this.depsFolder;
        }
        
        /**
         * Sets the php dependencies folder
         * 
         * @param depsFolder
         *            php dependencies folder
         */
        public void setDepsFolder(final IFolder depsFolder) {
            this.depsFolder = depsFolder;
        }
        
        /**
         * Returns the php test dependencies folder
         * 
         * @return php test dependencies folder
         */
        public IFolder getTestDepsFolder() {
            return this.testDepsFolder;
        }
        
        /**
         * Sets the php test dependencies folder
         * 
         * @param testDepsFolder
         *            php test dependencies folder
         */
        public void setTestDepsFolder(final IFolder testDepsFolder) {
            this.testDepsFolder = testDepsFolder;
        }
        
    }
    
    /**
     * Returns the project compile source locations; fixes the problem that we
     * do not overwrite the src/main/java correctly in the pom.
     * 
     * @param project
     *            The project
     * @param facade
     *            The project facade
     * @return Source paths
     */
    public static IPath[] getCompileSourceLocations(final IProject project, final IMavenProjectFacade facade) {
        if (facade != null && facade.getCompileSourceLocations() != null) {
            final IPath[] paths = facade.getCompileSourceLocations().clone();
            if (paths.length == 1 && paths[0].equals(project.getFolder("src/main/java").getProjectRelativePath())) { //$NON-NLS-1$
                return new IPath[] { project.getFolder("src/main/php").getFullPath() }; //$NON-NLS-1$
            }
            for (int i = 0; i < paths.length; i++) {
                paths[i] = project.getFolder(paths[i]).getFullPath();
            }
            return paths;
        }
        return new IPath[] { project.getFolder("src/main/php").getFullPath() }; //$NON-NLS-1$
    }
    
    /**
     * Returns the project compile source locations; fixes the problem that we
     * do not overwrite the src/test/java correctly in the pom.
     * 
     * @param project
     *            The project
     * @param facade
     *            The project facade
     * @return Source paths
     */
    public static IPath[] getTestCompileSourceLocations(final IProject project, final IMavenProjectFacade facade) {
        if (facade != null && facade.getTestCompileSourceLocations() != null) {
            final IPath[] paths = facade.getTestCompileSourceLocations().clone();
            if (paths.length == 1 && paths[0].equals(project.getFolder("src/test/java").getProjectRelativePath())) { //$NON-NLS-1$
                return new IPath[] { project.getFolder("src/test/php").getFullPath() }; //$NON-NLS-1$
            }
            for (int i = 0; i < paths.length; i++) {
                paths[i] = project.getFolder(paths[i]).getFullPath();
            }
            return paths;
        }
        return new IPath[] { project.getFolder("src/test/php").getFullPath() }; //$NON-NLS-1$
    }
    
    /**
     * Search class requestor
     */
    private static class PHPClassSearchRequestor extends SearchRequestor {
        /** search matches */
        private ArrayList<SearchMatch> searchMatches;
        
        @Override
        public void beginReporting() {
            this.searchMatches = new ArrayList<SearchMatch>();
            super.beginReporting();
        }
        
        @Override
        public void acceptSearchMatch(final SearchMatch match) throws CoreException {
            if (match.isExact()) {
                this.searchMatches.add(match);
            }
        }
        
        /**
         * returns the matches
         * 
         * @return search matches
         */
        public SearchMatch[] getMatches() {
            return this.searchMatches.toArray(new SearchMatch[0]);
        }
    }
    
    /**
     * Creates a temporary file
     * 
     * @param prefix
     *            The filename prefix
     * @param suffix
     *            The filename suffix
     * @return temporary file
     * @throws IOException
     *             thrown if the temp file could not be created
     */
    public static File createTempFile(final String prefix, final String suffix) throws IOException {
        final File result = File.createTempFile(prefix, suffix);
        result.deleteOnExit();
        return result;
    }
    
    /**
     * Returns true if given file is a php file
     * 
     * @param file
     * @return true if given file is a php file
     */
    public static boolean isPhpFile(final IFile file) {
        IContentDescription contentDescription = null;
        if (!file.exists()) {
            return MavenPhpUtils.hasPhpExtention(file);
        }
        try {
            contentDescription = file.getContentDescription();
        } catch (final CoreException e) {
            return MavenPhpUtils.hasPhpExtention(file);
        }
        
        if (contentDescription == null) {
            return MavenPhpUtils.hasPhpExtention(file);
        }
        
        return MavenPhpUtils.ContentTypeID_PHP.equals(contentDescription.getContentType().getId());
    }
    
    /**
     * Returns true if given file has a php file extension
     * 
     * @param file
     * @return true if given file is a php file
     */
    public static boolean hasPhpExtention(final IFile file) {
        final String fileName = file.getName();
        final int index = fileName.lastIndexOf('.');
        if (index == -1) {
            return false;
        }
        final String extension = fileName.substring(index + 1);
        final IContentType type = Platform.getContentTypeManager().getContentType(MavenPhpUtils.ContentTypeID_PHP);
        final String[] validExtensions = type.getFileSpecs(IContentType.FILE_EXTENSION_SPEC);
        for (final String validExtension : validExtensions) {
            if (extension.equalsIgnoreCase(validExtension)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Returns true if given filename has a php extension
     * 
     * @param fileName
     * @return true if this is a php file extension
     */
    public static boolean hasPhpExtention(final String fileName) {
        if (fileName == null) {
            throw new IllegalArgumentException();
        }
        
        final int index = fileName.lastIndexOf('.');
        if (index == -1) {
            return false;
        }
        final String extension = fileName.substring(index + 1);
        
        final IContentType type = Platform.getContentTypeManager().getContentType(MavenPhpUtils.ContentTypeID_PHP);
        final String[] validExtensions = type.getFileSpecs(IContentType.FILE_EXTENSION_SPEC);
        for (final String validExtension : validExtensions) {
            if (extension.equalsIgnoreCase(validExtension)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Adds given nature to project
     * 
     * @param project
     *            project
     * @param natureId
     *            nature
     * @throws CoreException
     *             thrown on errors
     */
    public static void addNature(final IProject project, final String natureId) throws CoreException {
        final IProjectDescription description = project.getDescription();
        final String[] natures = description.getNatureIds();
        final String[] newNatures = new String[natures.length + 1];
        System.arraycopy(natures, 0, newNatures, 0, natures.length);
        newNatures[natures.length] = natureId;
        description.setNatureIds(newNatures);
        project.setDescription(description, null);
    }
    
    /**
     * Returns true if pom.xml is found in given project.
     * @param project project
     * @return true if pom.xml is found.
     */
    public static boolean hasPomXml(final IProject project) {
        final IFile pomXml = project.getFile("pom.xml"); //$NON-NLS-1$
        return pomXml.exists();
    }
    
    /**
     * Creates a php-maven pom.xml
     * @param project the project
     * @param groupId
     * @param artifactId
     * @param version
     * @throws CoreException
     */
    public static void createPhpmavenPomXml(final IProject project, final String groupId, final String artifactId, final String version) throws CoreException {
        final IFile pomXml = project.getFile("pom.xml"); //$NON-NLS-1$
        if (pomXml.exists()) {
            throw new CoreException(new Status(IStatus.ERROR, PhpmavenCorePlugin.PLUGIN_ID, "pom.xml found! Unable to recreate.")); //$NON-NLS-1$
        }
        pomXml.create(new ByteArrayInputStream(
                POM_CONTENTS.replace("${artifactId}", artifactId) //$NON-NLS-1$
                .replace("${groupId}", groupId) //$NON-NLS-1$
                .replace("${version}", version) //$NON-NLS-1$
                .getBytes()), true, new NullProgressMonitor());
    }
    
    /**
     * Adds maven nature to given project; requires pom.xml to be present.
     * @param project
     * @throws CoreException
     */
    public static void addMavenNature(final IProject project) throws CoreException {
        if (!isMavenProject(project)) {
            if (!hasPomXml(project)) {
                throw new CoreException(new Status(IStatus.ERROR, PhpmavenCorePlugin.PLUGIN_ID, "pom.xml not found! Cannot add maven nature.")); //$NON-NLS-1$
            }
            MavenPhpUtils.addNature(project, IMavenConstants.NATURE_ID);
        }
    }
    
    /**
     * Adds php nature to project.
     * @param project
     * @throws CoreException
     */
    public static void addPhpNature(final IProject project) throws CoreException {
        if (!isPHPProject(project)) {
            if (!hasPomXml(project)) {
                throw new CoreException(new Status(IStatus.ERROR, PhpmavenCorePlugin.PLUGIN_ID, "pom.xml not found! Cannot add php nature.")); //$NON-NLS-1$
            }
            
            final IMavenProjectFacade facade = fetchProjectFacade(project);
            final IPath[] paths = getCompileSourceLocations(project, facade);
            final IPath[] testPaths = getTestCompileSourceLocations(project, facade);
            
            final IScriptProject scriptProject = DLTKCore.create(project);
            final List<IBuildpathEntry> entries = new ArrayList<IBuildpathEntry>();
            final List<IncludePath> includes = new ArrayList<IncludePath>();
            for (final IPath path : paths) {
                entries.add(DLTKCore.newSourceEntry(path));
                includes.add(new IncludePath(project.getFolder(path.removeFirstSegments(1)), project));
            }
            for (final IPath testPath : testPaths) {
                entries.add(DLTKCore.newSourceEntry(testPath));
                includes.add(new IncludePath(project.getFolder(testPath.removeFirstSegments(1)), project));
            }
            
            final boolean useASPTags = false;
            final PHPVersion phpVersion = PHPVersion.PHP5_3;
            ProjectOptions.setSupportingAspTags(useASPTags, project);
            ProjectOptions.setPhpVersion(phpVersion, project);
            
            ResourceUtil.addNature(project, new NullProgressMonitor(), PHPNature.ID);
            
            // TODO JsWebNature jsWebNature = new JsWebNature(getProject(),
            // new SubProgressMonitor(monitor, 1));
            // jsWebNature.configure();
            
            scriptProject.setRawBuildpath(entries.toArray(new IBuildpathEntry[entries.size()]), new NullProgressMonitor());
            LanguageModelInitializer.enableLanguageModelFor(scriptProject);
            
            IncludePathManager.getInstance().setIncludePath(project, includes.toArray(new IncludePath[includes.size()]));
        }
    }
    
    /**
     * Adds maven nature to given project; requires pom.xml to be present.
     * @param project
     * @throws CoreException
     */
    public static void addPhpMavenNature(final IProject project) throws CoreException {
        if (!isPHPProject(project)) {
            throw new CoreException(new Status(IStatus.ERROR, PhpmavenCorePlugin.PLUGIN_ID, "Unable to add php-maven nature on non-php-projects")); //$NON-NLS-1$
        }
        if (!isMavenProject(project)) {
            throw new CoreException(new Status(IStatus.ERROR, PhpmavenCorePlugin.PLUGIN_ID, "Unable to add php-maven nature on non-maven-projects")); //$NON-NLS-1$
        }
        
        if (!isPhpmavenProject(project)) {
            addNature(project, PhpmavenCorePlugin.PHPMAVEN_NATURE_ID);
            final IScriptProject scriptProject = DLTKCore.create(project);
            final IBuildpathEntry[] entries = scriptProject.getRawBuildpath();
            final IBuildpathEntry[] newEntries = Arrays.copyOf(entries, entries.length + 1);
            newEntries[entries.length] = DLTKCore.newContainerEntry(new Path(PhpmavenCorePlugin.BUILDPATH_CONTAINER_ID));
            scriptProject.setRawBuildpath(newEntries, new NullProgressMonitor());

            // XXX: fix src paths/ include paths
            // src/main/php and src/test/php
        }
    }
    
    /**
     * Returns true if maven is offline.
     * @return true for offline maven.
     */
    public static boolean isOffline() {
        return MavenPlugin.getMavenConfiguration().isOffline();
    }
    
    /**
     * Builds a new artifact.
     * @param groupId group id.
     * @param artifactId artifact id.
     * @param version version.
     * @return artifact
     * @throws CoreException thrown on problems
     */
    @SuppressWarnings("unchecked")
    public static Artifact buildArtifact(String groupId, String artifactId, String version) throws CoreException {
        return MavenPlugin.getMaven().resolve(
                groupId,
                artifactId,
                version,
                null, "pom", //$NON-NLS-1$
                Collections.EMPTY_LIST, new NullProgressMonitor());
    }
    
    /**
     * Returns the effective pom.
     * @param facade facade.
     * @return effective pom.
     */
    public static String getEffectivePom(IMavenProjectFacade facade) {
        final StringWriter sw = new StringWriter();
        try {
            new MavenXpp3Writer().write(sw, facade.getMavenProject().getModel());
        }
        catch (IOException ex) {
            // should never happen
            return ex.toString();
        }
        return sw.toString();
    }
    
    /**
     * Returns the effective pom.
     * @param facade facade.
     * @return effective pom.
     * @throws CoreException 
     */
    public static Model getEffectiveModel(IMavenProjectFacade facade) throws CoreException {
        final String effectivePom = getEffectivePom(facade);
        return MavenPlugin.getMavenModelManager().readMavenModel(new ByteArrayInputStream(effectivePom.getBytes()));
    }

    /**
     * Returns the php-maven version that is used (main plugin version)
     * @param facade facade
     * @return the php-maven plugin version
     * @throws CoreException 
     */
    public static String getPhpmavenVersion(IMavenProjectFacade facade) throws CoreException {
        final Model effectiveModel = getEffectiveModel(facade);
        return getPhpmavenVersion(effectiveModel);
    }

    /**
     * Returns the php-maven version that is used (main plugin version)
     * @param effectiveModel the effective maven model
     * @return the php-maven plugin version
     * @throws CoreException 
     */
    public static String getPhpmavenVersion(Model effectiveModel) throws CoreException {
        for (final Extension ext : effectiveModel.getBuild().getExtensions()) {
            if ("org.phpmaven".equals(ext.getGroupId()) && "maven-php-plugin".equals(ext.getArtifactId())) {  //$NON-NLS-1$//$NON-NLS-2$
                return ext.getVersion();
            }
        }
        for (final Plugin plugin : effectiveModel.getBuild().getPlugins()) {
            if ("org.phpmaven".equals(plugin.getGroupId()) && "maven-php-plugin".equals(plugin.getArtifactId())) {  //$NON-NLS-1$//$NON-NLS-2$
                return plugin.getVersion();
            }
        }
        return null;
    }
    
}
