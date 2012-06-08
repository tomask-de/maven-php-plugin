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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathContainer;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.m2e.core.project.IMavenProjectChangedListener;
import org.eclipse.m2e.core.project.MavenProjectChangedEvent;
import org.phpmaven.eclipse.core.MavenPhpUtils;
import org.phpmaven.eclipse.core.PhpmavenCorePlugin;

/**
 * Listener for maven projects changed (that means possible dependencies
 * changed).
 * 
 * @author Martin Eisengardt
 */
public class MavenProjectChangedListener implements IMavenProjectChangedListener {
    
    /**
     * Constructor.
     */
    public MavenProjectChangedListener() {
        // empty
    }
    
    /**
     * @see org.eclipse.m2e.core.project.IMavenProjectChangedListener#mavenProjectChanged(org.eclipse.m2e.core.project.MavenProjectChangedEvent[],
     *      org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void mavenProjectChanged(final MavenProjectChangedEvent[] events, final IProgressMonitor monitor) {
        final Set<IProject> projects = new HashSet<IProject>();
        monitor.setTaskName(Messages.MavenProjectChangedListener_ResetMavenDependencies);
        for (final MavenProjectChangedEvent event : events) {
            final IFile pom = event.getSource();
            final IProject project = pom.getProject();
            if (project.isAccessible() && projects.add(project) && MavenPhpUtils.isPHPProject(project)) {
                this.updateBuildpath(project, monitor);
                // XXX: generateResources(project, monitor);
            }
        }
    }
    
    /**
     * Updates the buildpath for a single project
     * 
     * @param project
     * @param monitor
     */
    public void updateBuildpath(final IProject project, final IProgressMonitor monitor) {
        try {
            final IScriptProject scriptProject = DLTKCore.create(project);
            DLTKCore.setBuildpathContainer(new Path(PhpmavenCorePlugin.BUILDPATH_CONTAINER_ID), new IScriptProject[] { scriptProject }, new IBuildpathContainer[] { new Container(new Path(
                    PhpmavenCorePlugin.BUILDPATH_CONTAINER_ID), scriptProject) }, null);
        } catch (final ModelException e) {
            PhpmavenCorePlugin.logError("Error updating the buildpath for project " + project.getName(), e); //$NON-NLS-1$
        }
    }
    
}
