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

import java.util.Properties;

import org.apache.maven.archetype.catalog.Archetype;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.ProjectImportConfiguration;
import org.eclipse.osgi.util.NLS;
import org.phpmaven.eclipse.core.MavenPhpUtils;
import org.phpmaven.eclipse.core.archetype.ProjectCreationRequest;

/**
 * The dummy archetype for empty php projects.
 * 
 * @author mepeisen
 */
class GenericArchetype extends AbstractArchetype {
    
    /** the underlying archetype. */
    private Archetype archetype;

    /**
     * Constructor
     * @param at the underlying archetype
     */
    public GenericArchetype(Archetype at) {
        this.archetype = at;
    }

    /**
     * @see org.phpmaven.eclipse.core.archetype.IArchetype#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        return false;
    }
    
    /**
     * @see org.phpmaven.eclipse.core.archetype.IArchetype#getArchetype()
     */
    @Override
    public Archetype getArchetype() {
        return this.archetype;
    }
    
    /**
     * @see org.phpmaven.eclipse.core.archetype.IArchetype#getGroupId()
     */
    @Override
    public String getGroupId() {
        return this.archetype.getGroupId();
    }
    
    /**
     * @see org.phpmaven.eclipse.core.archetype.IArchetype#getArtifactId()
     */
    @Override
    public String getArtifactId() {
        return this.archetype.getArtifactId();
    }
    
    /**
     * @see org.phpmaven.eclipse.core.archetype.IArchetype#getVersion()
     */
    @Override
    public String getVersion() {
        return this.archetype.getVersion();
    }
    
    /**
     * @see org.phpmaven.eclipse.core.archetype.IArchetype#getName()
     */
    @Override
    public String getName() {
        return this.archetype.getArtifactId() +
                " [" + //$NON-NLS-1$
                this.getGroupId() + ":" + this.getArtifactId() + ":" + this.getVersion() + //$NON-NLS-1$ //$NON-NLS-2$
        		"]"; //$NON-NLS-1$
    }
    
    /**
     * @see org.phpmaven.eclipse.core.archetype.IArchetype#getDescription()
     */
    @Override
    public String getDescription() {
        return this.archetype.getDescription();
    }

    @Override
    public void createProject(ProjectCreationRequest request) throws CoreException {
        request.getMonitor().beginTask(NLS.bind(Messages.GenericArchetype_CreateProject, new String[]{this.getGroupId(), this.getArtifactId(), this.getVersion()}), 100);
        
        final ProjectImportConfiguration configuration = new ProjectImportConfiguration();
        configuration.setProjectNameTemplate("[artifactId]-[version]"); //$NON-NLS-1$
        final IProject parent = this.resolveParentProject(request);
        final IPath parentPath = this.resolveParentPath(parent, request);
        final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(request.getProjectName());
        
        MavenPlugin.getProjectConfigurationManager().createArchetypeProject(
                project, request.getProjectLocation(),
                this.archetype,
                request.getGroupId(), request.getArtifactId(), request.getVersion(),
                null,
                new Properties(),
                configuration,
                request.getMonitor());
        request.getMonitor().worked(60);
        
        final IMavenProjectFacade facade = MavenPhpUtils.fetchProjectFacade(project);
        facade.getMavenProject().setName(request.getPomProjectName());
        facade.getMavenProject().setDescription(request.getPomProjectDescription());
        request.getMonitor().worked(10);

        this.applyParent(facade, parent, parentPath, request);
        request.getMonitor().worked(10);
        
        MavenPhpUtils.addPhpNature(project);
        request.getMonitor().worked(10);

        MavenPhpUtils.addPhpMavenNature(project);
        request.getMonitor().worked(10);
        
        request.getMonitor().done();
    }
    
}
