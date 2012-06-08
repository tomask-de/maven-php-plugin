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
// mainly taken from pti

package org.phpmaven.eclipse.ui.menus;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;
import org.phpmaven.eclipse.core.MavenPhpUtils;
import org.phpmaven.eclipse.ui.PhpmavenUiPlugin;

/**
 * Menu action to invoke phpunit tests on selected resource.
 * 
 * @author Martin Eisengardt
 */
public class InfoPhpMavenHandler extends AbstractProjectHandler {

    @Override
    protected Job createJob(IResource[] resources) {
        return new ExecutionJob(resources);
    }

    /**
     * @author Martin Eisengardt
     */
    private static final class ExecutionJob extends Job {
        
        /** the projects to be packed. */
        private final IResource[] resources;
        
        /**
         * @param resources
         */
        public ExecutionJob(final IResource[] resources) {
            super(Messages.InfoPhpMavenHandler_JobTitle);
            this.resources = resources;
        }
        
        @Override
        public IStatus run(final IProgressMonitor monitor) {
            monitor.beginTask(Messages.InfoPhpMavenHandler_Task_Starting, this.resources.length);
            for (final IResource resource : this.resources) {
                final IProject project = resource.getProject();
                monitor.setTaskName(Messages.InfoPhpMavenHandler_Task_RunFor + project.getName());
                
                final IMavenProjectFacade facade = MavenPhpUtils.fetchProjectFacade(project);
                
                String phpMavenVersion = null;
                try {
                    phpMavenVersion = MavenPhpUtils.getPhpmavenVersion(facade);
                } catch (CoreException ex) {
                    final IStatus status = new Status(IStatus.ERROR, PhpmavenUiPlugin.PLUGIN_ID, Messages.InfoPhpMavenHandler_ErrorFetchingVersion, ex);
                    monitor.done();
                    this.done(status);
                    return status;
                }
                
                final Job job = new ExecutionUiJob(Messages.InfoPhpMavenHandler_UiTaskTitle, project, phpMavenVersion);
                job.setRule(this.getRule());
                job.setUser(false);
                job.schedule();
            }
            monitor.done();
            this.done(Status.OK_STATUS);
            return Status.OK_STATUS;
        }
    }
    
    /**
     * displays version info
     */
    private static final class ExecutionUiJob extends UIJob {
        /** the version to be displayed */
        private String phpMavenVersion;
        /** the project */
        private IProject project;

        /**
         * Constructor
         * @param name
         * @param project
         * @param version
         */
        private ExecutionUiJob(String name, IProject project, String version) {
            super(name);
            this.project = project;
            this.phpMavenVersion = version;
        }
        
        @Override
        public IStatus runInUIThread(final IProgressMonitor monitor) {
            final MessageBox box = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.ICON_INFORMATION | SWT.OK);
            box.setMessage(NLS.bind(Messages.InfoPhpMavenHandler_VersionInfo, new String[]{ this.project.getName(), this.phpMavenVersion}));
            box.open();
            monitor.done();
            this.done(Status.OK_STATUS);
            return Status.OK_STATUS;
        }
    }
    
}
