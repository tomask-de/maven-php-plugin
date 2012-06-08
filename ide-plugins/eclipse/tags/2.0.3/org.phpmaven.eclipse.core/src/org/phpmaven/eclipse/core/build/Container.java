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
package org.phpmaven.eclipse.core.build;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.resolver.filter.ScopeArtifactFilter;
import org.apache.maven.project.MavenProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathContainer;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.internal.core.BuildpathEntry;
import org.eclipse.m2e.core.internal.MavenPluginActivator;
import org.eclipse.m2e.core.internal.project.registry.MavenProjectManager;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.phpmaven.eclipse.core.PhpmavenCorePlugin;

/**
 * The maven buildpath container; caches the results. After changing the
 * dependencies the MavenProjectChangedListener will be invoked and will set a
 * new instance that re-calculates the dependencies.
 * 
 * @author Martin Eisengardt
 */
@SuppressWarnings("restriction")
public class Container implements IBuildpathContainer {
    
    /**
     * The container path
     */
    private final IPath containerPath;
    
    /**
     * The cached buildpath entries
     */
    private IBuildpathEntry[] buildPathEntries;
    
    /**
     * The script project
     */
    private final IScriptProject fProject;
    
    /**
     * Artifact filter (everything including test dependencies will be
     * respected)
     */
    private static final ArtifactFilter SCOPE_FILTER_TEST = new ScopeArtifactFilter(Artifact.SCOPE_TEST);
    
    /**
     * Constructor
     * 
     * @param containerPath
     * @param project
     */
    public Container(final IPath containerPath, final IScriptProject project) {
        this.containerPath = containerPath;
        this.fProject = project;
    }
    
    /**
     * Returns the build path entries for given project
     * 
     * @param project
     * 
     * @return the buildpath entries
     */
    public IBuildpathEntry[] getBuildpathEntries(final IScriptProject project) {
        // already cached?
        if (this.buildPathEntries == null) {
            // Fetch the maven project
            final List<IBuildpathEntry> entries = new ArrayList<IBuildpathEntry>();
            final IProgressMonitor monitor = new NullProgressMonitor();
            final IMavenProjectFacade mvnFacade = MavenPluginActivator.getDefault().getMavenProjectManager().create(project.getProject(), monitor);
            final MavenProjectManager projectManager = MavenPluginActivator.getDefault().getMavenProjectManager();
            if (mvnFacade != null) {
                try {
                    final MavenProject mavenProject = mvnFacade.getMavenProject(monitor);
                    final Set<Artifact> artifacts = mavenProject.getArtifacts();
                    for (final Artifact a : artifacts) {
                        if (!Container.SCOPE_FILTER_TEST.include(a) /*|| !a.getArtifactHandler().isAddedToClasspath()*/) {
                            continue;
                        }
                        
                        // fetch project facade of dependency
                        final IMavenProjectFacade dependency = projectManager.getMavenProject(a.getGroupId(), a.getArtifactId(), a.getVersion());
                        if (dependency != null && dependency.getProject().equals(mvnFacade.getProject())) {
                            continue;
                        }
                        
                        IBuildpathEntry entry = null;
                        if (dependency != null && dependency.getFullPath(a.getFile()) != null) {
                            // local project; set the reference
                            entry = DLTKCore.newProjectEntry(dependency.getFullPath(), false);
                        } else {
                            // file reference within the local repository
                            final File artifactFile = a.getFile();
                            if (artifactFile != null /*
                                                      * &&
                                                      * artifactFile.canRead()
                                                      */) {
                                final IEnvironment env = EnvironmentManager.getEnvironment(project);
                                final IPath path = Path.fromOSString(artifactFile.getAbsolutePath());
                                entry = DLTKCore.newLibraryEntry(EnvironmentPathUtils.getFullPath(env, path), BuildpathEntry.NO_ACCESS_RULES, BuildpathEntry.NO_EXTRA_ATTRIBUTES,
                                        BuildpathEntry.INCLUDE_ALL, BuildpathEntry.EXCLUDE_NONE, false, true);
                            }
                        }
                        
                        if (entry != null) {
                            // add it; if entry is null there is something wrong
                            // with the package, simply ignore it
                            // other builder (from maven plugin) may report the
                            // missing dependency and set a error marker
                            // at the pom.xml already.
                            entries.add(entry);
                        }
                    }
                } catch (final CoreException e) {
                    // log and ignore; should not happen. Even if this happens
                    // we should not fail
                    // with the buildpath container. This may confuse eclipse.
                    PhpmavenCorePlugin.logError("Error calculating the maven buildpath", e); //$NON-NLS-1$
                }
            }
            this.buildPathEntries = entries.toArray(new IBuildpathEntry[0]);
        }
        return this.buildPathEntries;
    }
    
    /**
     * Returns the description (=title)
     */
    @Override
    public String getDescription() {
        return Messages.Container_Title;
    }
    
    /**
     * Returns the kind used to sort the entries; K_APPLICATION will sort it
     * after the php system library
     */
    @Override
    public int getKind() {
        return IBuildpathContainer.K_APPLICATION;
    }
    
    /**
     * Returns the container path
     */
    @Override
    public IPath getPath() {
        return this.containerPath;
    }
    
    /**
     * Returns the build path entries
     */
    @Override
    public IBuildpathEntry[] getBuildpathEntries() {
        return this.getBuildpathEntries(this.fProject);
    }
    
}
