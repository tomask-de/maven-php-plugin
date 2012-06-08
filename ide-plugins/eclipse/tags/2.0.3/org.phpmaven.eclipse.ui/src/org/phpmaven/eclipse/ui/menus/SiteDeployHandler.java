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

import org.apache.maven.execution.MavenExecutionRequest;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.phpmaven.eclipse.core.mvn.IMavenJobData;
import org.phpmaven.eclipse.core.mvn.MavenJob;

/**
 * Menu action to invoke site:deploy command on selected resource.
 * 
 * @author Martin Eisengardt
 */
public class SiteDeployHandler extends AbstractProjectHandler {

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
            super(Messages.SiteDeployHandler_JobTitle);
            this.resources = resources;
        }
        
        @Override
        public IStatus run(final IProgressMonitor monitor) {
            monitor.beginTask(Messages.SiteDeployHandler_Task_Starting, this.resources.length);
            for (final IResource resource : this.resources) {
                final IProject project = resource.getProject();
                monitor.setTaskName(Messages.SiteDeployHandler_Task_RunFor + project.getName());
                
                final IMavenJobData jobData = new IMavenJobData() {
                    
                    @Override
                    public void manipulateRequest(final MavenExecutionRequest request) {
                        // empty
                    }
                    
                    @Override
                    public IProject getProject() {
                        return project;
                    }
                    
                    @Override
                    public String[] getMavenCommands() {
                        return new String[] { "site:deploy" }; //$NON-NLS-1$
                    }
                    
                    @Override
                    public boolean canProcessRequest(final MavenExecutionRequest request, final IMavenProjectFacade projectFacade) {
                        return true;
                    }
                };
                final MavenJob job = new MavenJob(jobData);
                final SubProgressMonitor subMonitor = new SubProgressMonitor(monitor, 1, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);
                final IStatus mavenResult = job.execute(subMonitor);
                subMonitor.done();
                
                if (!mavenResult.isOK()) {
                    monitor.done();
                    this.done(mavenResult);
                    return mavenResult;
                }
            }
            monitor.done();
            this.done(Status.OK_STATUS);
            return Status.OK_STATUS;
        }
    }
    
}
