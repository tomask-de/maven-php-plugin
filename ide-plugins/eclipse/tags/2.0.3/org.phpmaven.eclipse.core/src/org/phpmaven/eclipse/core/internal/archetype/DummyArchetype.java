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

import org.apache.maven.archetype.catalog.Archetype;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.phpmaven.eclipse.core.MavenPhpUtils;
import org.phpmaven.eclipse.core.archetype.ProjectCreationRequest;

/**
 * The dummy archetype for empty php projects.
 * 
 * @author mepeisen
 */
class DummyArchetype extends AbstractArchetype {
    
    /**
     * @see org.phpmaven.eclipse.core.archetype.IArchetype#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        return true;
    }
    
    /**
     * @see org.phpmaven.eclipse.core.archetype.IArchetype#getArchetype()
     */
    @Override
    public Archetype getArchetype() {
        return null;
    }
    
    /**
     * @see org.phpmaven.eclipse.core.archetype.IArchetype#getGroupId()
     */
    @Override
    public String getGroupId() {
        return null;
    }
    
    /**
     * @see org.phpmaven.eclipse.core.archetype.IArchetype#getArtifactId()
     */
    @Override
    public String getArtifactId() {
        return null;
    }
    
    /**
     * @see org.phpmaven.eclipse.core.archetype.IArchetype#getVersion()
     */
    @Override
    public String getVersion() {
        return null;
    }
    
    /**
     * @see org.phpmaven.eclipse.core.archetype.IArchetype#getName()
     */
    @Override
    public String getName() {
        return Messages.DummyArchetype_Name;
    }
    
    /**
     * @see org.phpmaven.eclipse.core.archetype.IArchetype#getDescription()
     */
    @Override
    public String getDescription() {
        return Messages.DummyArchetype_Description;
    }

    @Override
    public void createProject(ProjectCreationRequest request) throws CoreException {
        request.getMonitor().beginTask(Messages.DummyArchetype_Task_Title, 70);
        
        // 1. resolve parent if needed
        request.getMonitor().setTaskName(Messages.DummyArchetype_Task_ResolveParent);
        final IProject parent = this.resolveParentProject(request);
        final IPath parentPath = this.resolveParentPath(parent, request);
        request.getMonitor().worked(10);
        
        // 2. generate project
        request.getMonitor().setTaskName(Messages.DummyArchetype_Task_GenerateProject);
        final IProject project = this.generateEmptyProject(parentPath, request);
        request.getMonitor().worked(10);
        
        // 3. generate pom
        request.getMonitor().setTaskName(Messages.DummyArchetype_Task_GeneratePom);
        MavenPhpUtils.createPhpmavenPomXml(
                project,
                request.getGroupId(),
                request.getArtifactId(),
                request.getVersion());
        MavenPhpUtils.addMavenNature(project);
        final IMavenProjectFacade facade = MavenPhpUtils.fetchProjectFacade(project);
        facade.getMavenProject().setName(request.getPomProjectName());
        facade.getMavenProject().setDescription(request.getPomProjectDescription());
        request.getMonitor().worked(10);
        
        // 4. set parent if needed
        request.getMonitor().setTaskName(Messages.DummyArchetype_Task_ApplyParent);
        this.applyParent(facade, parent, parentPath, request);
        request.getMonitor().worked(10);
        
        // 5. generate needed folders
        request.getMonitor().setTaskName(Messages.DummyArchetype_Task_GenerateFolders);
        this.generateSrcFolders(facade, project);
        request.getMonitor().worked(10);

        // 6. add php nature
        request.getMonitor().setTaskName(Messages.DummyArchetype_Task_AddPhpNature);
        MavenPhpUtils.addPhpNature(project);
        request.getMonitor().worked(10);

        // 7. add phpmaven nature
        request.getMonitor().setTaskName(Messages.DummyArchetype_Task_AddPhpMavenNature);
        MavenPhpUtils.addPhpMavenNature(project);
        request.getMonitor().worked(10);
        request.getMonitor().done();
    }
    
}
