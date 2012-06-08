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

package org.phpmaven.eclipse.core.internal.archetype;

import org.apache.maven.artifact.Artifact;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.phpmaven.eclipse.core.MavenPhpUtils;
import org.phpmaven.eclipse.core.ResourceUtils;
import org.phpmaven.eclipse.core.archetype.IArchetype;
import org.phpmaven.eclipse.core.archetype.ProjectCreationRequest;

/**
 * Abstract helper class for archetypes.
 * 
 * @author mepeisen
 */
abstract class AbstractArchetype implements IArchetype {

    /**
     * @param facade
     * @param project
     * @throws CoreException 
     */
    protected void generateSrcFolders(IMavenProjectFacade facade, IProject project) throws CoreException {
        final IPath[] paths = MavenPhpUtils.getCompileSourceLocations(project, facade);
        final IPath[] testPaths = MavenPhpUtils.getTestCompileSourceLocations(project, facade);
        for (final IPath path : paths) {
            final IFolder folder = project.getFolder(path.removeFirstSegments(1));
            ResourceUtils.mkdirs(folder);
        }
        for (final IPath path : testPaths) {
            final IFolder folder = project.getFolder(path.removeFirstSegments(1));
            ResourceUtils.mkdirs(folder);
        }
    }

    /**
     * @param facade
     * @param parent
     * @param parentPath
     * @param request
     * @throws CoreException 
     */
    protected void applyParent(IMavenProjectFacade facade, IProject parent, IPath parentPath, ProjectCreationRequest request) throws CoreException {
        if (request.getParentGroupId() != null && request.getParentGroupId().length() > 0) {
            final Artifact artifact = MavenPhpUtils.buildArtifact(request.getParentGroupId(), request.getParentArtifactId(), request.getParentVersion());
            facade.getMavenProject().setParentArtifact(artifact);
        }
        // TODO set relative path
        // TODO add as module in parent path
    }

    /**
     * @param parentPath
     * @param request
     * @return new project
     * @throws CoreException 
     */
    protected IProject generateEmptyProject(IPath parentPath, ProjectCreationRequest request) throws CoreException {
        final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(request.getProjectName());
        if (!project.exists()) {
            final IProjectDescription desc = project.getWorkspace().newProjectDescription(project.getName());
            desc.setLocation(request.getProjectLocation());
            project.create(desc, new NullProgressMonitor());
        }
        if (!project.isOpen()) {
            project.open(new NullProgressMonitor());
        }
        return project;
    }

    /**
     * @param parent
     * @param request
     * @return parent or null if no parent was resolved
     */
    protected IPath resolveParentPath(IProject parent, ProjectCreationRequest request) {
        if (parent != null) {
            return parent.getFullPath();
        }
        return null;
    }

    /**
     * @param request
     * @return parent or null if no parent was resolved
     */
    protected IProject resolveParentProject(ProjectCreationRequest request) {
        if (request.getParentGroupId() != null && request.getParentGroupId().length() > 0) {
            // TODO
        }
        return null;
    }
    
}
