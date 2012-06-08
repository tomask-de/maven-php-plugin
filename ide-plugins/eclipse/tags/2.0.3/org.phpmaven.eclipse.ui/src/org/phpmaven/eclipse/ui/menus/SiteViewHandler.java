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

import java.net.MalformedURLException;

import org.apache.maven.execution.MavenExecutionRequest;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.phpmaven.eclipse.core.MavenPhpUtils;
import org.phpmaven.eclipse.core.mvn.IMavenJobData;
import org.phpmaven.eclipse.core.mvn.MavenJob;
import org.phpmaven.eclipse.ui.PhpmavenUiPlugin;

/**
 * Menu action to invoke site:view on selected resource.
 * 
 * @author Martin Eisengardt
 */
public class SiteViewHandler extends AbstractProjectHandler {

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
            super(Messages.SiteViewHandler_JobTitle);
            this.resources = resources;
        }
        
        @Override
        public IStatus run(final IProgressMonitor monitor) {
            monitor.beginTask(Messages.SiteViewHandler_Task_Starting, this.resources.length);
            for (final IResource resource : this.resources) {
                final IProject project = resource.getProject();
                final IMavenProjectFacade facade = MavenPhpUtils.fetchProjectFacade(project);
                final IFolder siteOutput = MavenPhpUtils.getSiteOutputFolder(project, facade);
                final IFile indexHtml = siteOutput.getFile("index.html"); //$NON-NLS-1$
                if (!indexHtml.exists()) {
                    monitor.setTaskName(Messages.SiteViewHandler_Task_RunFor + project.getName());
                    
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
                            return new String[] { "site:site" }; //$NON-NLS-1$
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
                
                if (indexHtml.exists()) {
                    try {
                        PlatformUI.getWorkbench().getBrowserSupport().createBrowser(null).openURL(indexHtml.getLocationURI().toURL());
                    }
                    catch (PartInitException ex) {
                        monitor.done();
                        final IStatus result = new Status(IStatus.ERROR, PhpmavenUiPlugin.PLUGIN_ID, Messages.SiteViewHandler_ErrorOpeningBrowser, ex);
                        this.done(result);
                        return result;
                    }
                    catch (MalformedURLException ex) {
                        monitor.done();
                        final IStatus result = new Status(IStatus.ERROR, PhpmavenUiPlugin.PLUGIN_ID, Messages.SiteViewHandler_ErrorOpeningBrowser, ex);
                        this.done(result);
                        return result;
                    }
                }
                else {
                    monitor.done();
                    final IStatus result = new Status(IStatus.ERROR, PhpmavenUiPlugin.PLUGIN_ID, Messages.SiteViewHandler_UnableToCreateSite);
                    this.done(result);
                    return result;
                }
                monitor.worked(1);
            }
            monitor.done();
            this.done(Status.OK_STATUS);
            return Status.OK_STATUS;
        }
    }
    
}
