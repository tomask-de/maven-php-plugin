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

package org.phpmaven.eclipse.core.builder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.maven.execution.MavenExecutionRequest;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.phpmaven.eclipse.core.MavenPhpUtils;
import org.phpmaven.eclipse.core.ResourceUtils;
import org.phpmaven.eclipse.core.mvn.IMavenJobData;
import org.phpmaven.eclipse.core.mvn.MavenJob;

/**
 * The maven builder.
 * 
 * Processing:<br />
 * <ul>
 * <li><b>Full build:</b> sum up depended projects; copy resources and php files
 * to target folders; extract dependencies</li>
 * <li><b>Incremental build:</b> sum up dependend projects; copy (or delete)
 * resources and php files to target folders</li>
 * </ul>
 * 
 * @author Martin Eisengardt
 */
public class MavenBuilder extends IncrementalProjectBuilder {
    
    /**
     * The delta visitor
     * 
     * @author Martin Eisengardt
     */
    class DeltaVisitor implements IResourceDeltaVisitor {
        
        /**
         * Cache for the maven facade lookup
         */
        private final Map<IProject, IMavenProjectFacade> cachedFacades = new HashMap<IProject, IMavenProjectFacade>();
        
        /**
         * Cache for the maven project config
         */
        private final Map<IProject, MavenPhpUtils.PhpmavenBuildConfig> cachedConfigs = new HashMap<IProject, MavenPhpUtils.PhpmavenBuildConfig>();
        
        /**
         * Cache of depending projects.
         */
        private Set<IProject> dependendProjects = new HashSet<IProject>();
        
        /**
         * Constructor
         * 
         * @param thisProject
         */
        public DeltaVisitor(final IProject thisProject) {
            this.dependendProjects = MavenPhpUtils.getDependingProjects(thisProject);
        }
        
        /**
         * Returns the facade for given project doing a lookup if needed
         * 
         * @param project
         * @return facade
         */
        private IMavenProjectFacade getFacade(final IProject project) {
            if (!this.cachedFacades.containsKey(project)) {
                this.cachedFacades.put(project, MavenPhpUtils.fetchProjectFacade(project));
            }
            return this.cachedFacades.get(project);
        }
        
        /**
         * Returns the config for given project doing a lookup if needed
         * 
         * @param project
         * @return config
         * @throws CoreException
         *             thrown on errors
         */
        private MavenPhpUtils.PhpmavenBuildConfig getConfig(final IProject project) throws CoreException {
            if (!this.cachedConfigs.containsKey(project)) {
                this.cachedConfigs.put(project, MavenPhpUtils.fetchConfig(project, this.getFacade(project)));
            }
            return this.cachedConfigs.get(project);
        }
        
        /**
         * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
         */
        @Override
        public boolean visit(final IResourceDelta delta) throws CoreException {
            final IResource resource = delta.getResource();
            final IProject project = resource.getProject();
            final IMavenProjectFacade facade = this.getFacade(project);
            
            if (resource.getProject().equals(project)) {
                // this is a resource within the project
                
                // although the docs are saying that returning a project list at
                // method build
                // will cause that every change on projects will cause a builder
                // invocation on dependend projects
                // this seems not to work...
                // XXX: Why does it not work?
                
                // look if it is part of sources or test sources
                for (final IPath path : MavenPhpUtils.getCompileSourceLocations(project, facade)) {
                    if (path.isPrefixOf(resource.getFullPath())) {
                        // this is a compile source file
                        final IPath relPath = resource.getFullPath().removeFirstSegments(path.segmentCount());
                        this.perform(relPath, resource, delta.getKind(), facade.getOutputLocation());
                        // XXX: Remove iteration through depending projects as
                        // soon as the builder works for us
                        for (final IProject depProject : this.dependendProjects) {
                            final MavenPhpUtils.PhpmavenBuildConfig config = this.getConfig(depProject);
                            this.perform(relPath, resource, delta.getKind(), config.getDepsFolder().getFullPath());
                        }
                        return true;
                    }
                }
                for (final IPath path : MavenPhpUtils.getTestCompileSourceLocations(project, facade)) {
                    if (path.isPrefixOf(resource.getFullPath())) {
                        // this is a test compile source file
                        this.perform(resource.getFullPath().removeFirstSegments(path.segmentCount()), resource, delta.getKind(), facade.getTestOutputLocation());
                        return true;
                    }
                }
            } else {
                // a resource we are depending on (from another project)
                // look if it is part of sources or test sources
                for (final IPath path : MavenPhpUtils.getCompileSourceLocations(project, facade)) {
                    if (path.isPrefixOf(resource.getFullPath())) {
                        // this is a compile source file
                        final MavenPhpUtils.PhpmavenBuildConfig thisConfig = this.getConfig(MavenBuilder.this.getProject());
                        final IPath relPath = resource.getFullPath().removeFirstSegments(path.segmentCount());
                        this.perform(relPath, resource, delta.getKind(), thisConfig.getDepsFolder().getFullPath());
                        this.perform(relPath, resource, delta.getKind(), thisConfig.getTestDepsFolder().getFullPath());
                        return true;
                    }
                }
            }
            
            // return true to continue visiting children.
            return true;
        }
        
        /**
         * Performs the delta
         * 
         * @param relPath
         *            relative path name
         * @param resource
         *            The resource
         * @param kind
         *            the delta kind
         * @param outputLocation
         *            output location to be used
         * @throws CoreException
         *             thrown on errors
         */
        private void perform(final IPath relPath, final IResource resource, final int kind, final IPath outputLocation) throws CoreException {
            final IPath targetPath = outputLocation.append(relPath);
            
            switch (resource.getType()) {
                case IResource.FILE:
                    final IFile targetFile = ResourcesPlugin.getWorkspace().getRoot().getFile(targetPath);
                    switch (kind) {
                        case IResourceDelta.ADDED:
                            // handle added resource
                            ResourceUtils.copy((IFile) resource, targetFile);
                            break;
                        case IResourceDelta.REMOVED:
                            // handle removed resource
                            if (targetFile.exists()) {
                                targetFile.delete(true, new NullProgressMonitor());
                            }
                            break;
                        case IResourceDelta.CHANGED:
                            // handle changed resource
                            ResourceUtils.copy((IFile) resource, targetFile);
                            break;
                    }
                    break;
                case IResource.FOLDER:
                    final IFolder targetFolder = ResourcesPlugin.getWorkspace().getRoot().getFolder(targetPath);
                    switch (kind) {
                        case IResourceDelta.ADDED:
                            // handle added resource
                            ResourceUtils.mkdirs(targetFolder);
                            break;
                        case IResourceDelta.REMOVED:
                            // handle removed resource
                            if (targetFolder.exists()) {
                                targetFolder.delete(true, new NullProgressMonitor());
                            }
                            break;
                        case IResourceDelta.CHANGED:
                            // handle changed resource
                            break;
                    }
                    break;
            }
        }
    }
    
    /**
     * @see org.eclipse.core.resources.IncrementalProjectBuilder#build(int,
     *      java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    protected IProject[] build(final int kind, final Map<String, String> args, final IProgressMonitor monitor) throws CoreException {
        if (MavenPhpUtils.isMavenProject(this.getProject()) && MavenPhpUtils.isPHPProject(this.getProject())) {
            if (kind == IncrementalProjectBuilder.FULL_BUILD) {
                this.fullBuild(monitor);
            } else {
                final IResourceDelta delta = this.getDelta(this.getProject());
                if (delta == null) {
                    this.fullBuild(monitor);
                } else {
                    this.incrementalBuild(delta, monitor);
                }
            }
            
            // fetch the dependencies
            final Set<IProject> dependencies = MavenPhpUtils.getProjectDependies(this.getProject());
            return dependencies.toArray(new IProject[dependencies.size()]);
        }
        return null;
    }
    
    /**
     * Perfoms a full build on specified project.
     * 
     * @param monitor
     *            the progress monitor
     * @throws CoreException
     *             thrown on errors during build
     */
    protected void fullBuild(final IProgressMonitor monitor) throws CoreException {
        // extractDependencies, extractTestDependencies
        final IMavenJobData jobData = new IMavenJobData() {
            
            @Override
            public void manipulateRequest(final MavenExecutionRequest request) {
                // does nothing
            }
            
            @Override
            public IProject getProject() {
                return MavenBuilder.this.getProject();
            }
            
            @Override
            public String[] getMavenCommands() {
                return new String[] { "org.phpmaven:maven-php-plugin:extractDependencies", "org.phpmaven:maven-php-plugin:extractTestDependencies" }; //$NON-NLS-1$ //$NON-NLS-2$
            }
            
            @Override
            public boolean canProcessRequest(final MavenExecutionRequest request, final IMavenProjectFacade projectFacade) {
                return true;
            }
        };
        final MavenJob job = new MavenJob(jobData);
        monitor.worked(20);
        monitor.setTaskName("execute"); //$NON-NLS-1$
        final SubProgressMonitor subMonitor = new SubProgressMonitor(monitor, 50, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);
        final IStatus mavenResult = job.execute(subMonitor);
        // TODO analyze MavenResult
        
        // copy the sources from this project and all dependend projects
        final Set<IProject> dependencies = MavenPhpUtils.getProjectDependies(this.getProject());
        final IMavenProjectFacade thisFacade = MavenPhpUtils.fetchProjectFacade(this.getProject());
        final MavenPhpUtils.PhpmavenBuildConfig buildConfig = MavenPhpUtils.fetchConfig(this.getProject(), thisFacade);
        
        // ***** target/classes
        
        // fetch and create output location
        final IPath outputLocation = thisFacade.getOutputLocation();
        final IFolder outputFolder = ResourcesPlugin.getWorkspace().getRoot().getFolder(outputLocation);
        ResourceUtils.mkdirs(outputFolder);
        
        // copy the sources
        ResourceUtils.copy(MavenPhpUtils.getCompileSourceLocations(this.getProject(), thisFacade), outputFolder);
        
        // ***** target/test-classes
        
        // fetch and create output location
        final IPath testOutputLocation = thisFacade.getTestOutputLocation();
        final IFolder testOutputFolder = ResourcesPlugin.getWorkspace().getRoot().getFolder(testOutputLocation);
        ResourceUtils.mkdirs(testOutputFolder);
        
        // copy the sources
        ResourceUtils.copy(MavenPhpUtils.getTestCompileSourceLocations(this.getProject(), thisFacade), testOutputFolder);
        
        // ***** target/php-deps
        
        // fetch and create output location
        final IFolder depsOutputFolder = buildConfig.getDepsFolder();
        ResourceUtils.mkdirs(depsOutputFolder);
        
        // iterate projects and copy them
        for (final IProject project : dependencies) {
            final IMavenProjectFacade facade = MavenPhpUtils.fetchProjectFacade(project);
            ResourceUtils.copy(MavenPhpUtils.getCompileSourceLocations(project, facade), depsOutputFolder);
        }
        
        // ***** target/php-test-deps
        
        // fetch and create output location
        // XXX: Do we need to copy them to the test folder? They are already
        // within the normal deps folder
        // We do not have the scope at this point. Maybe we should introduce two
        // build ontainers, one for testing, one for compile dependencies to
        // divide them at this point.
        // Another solution would be parsing the dependencies and looking for
        // projects at this point but not parsing the build container that mixes
        // up both.
        // final IFolder testDepsOutputFolder = buildConfig.getTestDepsFolder();
        // ResourceUtils.mkdirs(testDepsOutputFolder);
        //
        // // iterate projects and copy them
        // for (final IProject project : dependencies) {
        // final IMavenProjectFacade facade =
        // MavenPhpUtils.fetchProjectFacade(project);
        // // NOTE: copies the compile sources and not the test sources. This is
        // a wanted behavior and not a bug.
        // ResourceUtils.copy(this.getCompileSourceLocations(project, facade),
        // testDepsOutputFolder);
        // }
    }
    
    /**
     * Performs an incremental build on specified project.
     * 
     * @param delta
     *            delta to build
     * @param monitor
     *            the progress monitor
     * @throws CoreException
     *             thrown on errors during build
     */
    protected void incrementalBuild(final IResourceDelta delta, final IProgressMonitor monitor) throws CoreException {
        // the visitor does the work.
        delta.accept(new DeltaVisitor(this.getProject()));
    }
}
