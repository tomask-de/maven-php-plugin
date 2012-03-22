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
package org.phpmaven.eclipse.core.mvn;

import java.util.Arrays;

import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.embedder.IMavenConfiguration;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.internal.MavenPluginActivator;
import org.eclipse.m2e.core.internal.project.registry.MavenProjectManager;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.MavenUpdateRequest;
import org.eclipse.osgi.util.NLS;
import org.phpmaven.eclipse.core.PhpmavenCorePlugin;
import org.phpmaven.eclipse.core.internal.mvn.MvnLoggingAppender;

/**
 * A maven job on an eclipse project.
 * 
 * @author Martin Eisengardt
 */
@SuppressWarnings("restriction")
public class MavenJob extends Job {
    
    /**
     * The project
     */
    protected IMavenJobData data;
    
    /**
     * The maven result of the invocation
     */
    private MavenExecutionResult result;
    
    /**
     * The fetched sys out/ logging events
     */
    private String sysout;
    
    /**
     * @param data
     *            the project/job data
     */
    public MavenJob(final IMavenJobData data) {
        super(Messages.MavenJob_Title);
        this.data = data;
    }
    
    /**
     * Executes this job
     * 
     * @param monitor
     *            the monitor
     * @return job status
     */
    public IStatus execute(final IProgressMonitor monitor) {
        monitor.beginTask(Messages.MavenJob_BeginJob, 100);
        try {
            MvnLoggingAppender.startFetching();
            // final MavenPlugin plugin = MavenPlugin.getDefault();
            // final MavenConsole console = plugin.getConsole();
            final MavenProjectManager projectManager = MavenPluginActivator.getDefault().getMavenProjectManager();
            final IMavenConfiguration mavenConfiguration = MavenPlugin.getMavenConfiguration();
            
            // read the pom.xml and request the maven facade
            monitor.setTaskName(Messages.MavenJob_ReadingPom);
            monitor.worked(10);
            final IProject project = this.data.getProject();
            final IFile pomResource = project.getFile(IMavenConstants.POM_FILE_NAME);
            if (pomResource == null) {
                final String message = NLS.bind(Messages.MavenJob_ProjectPomNotReadable, new Object[] { project.getName() });
                PhpmavenCorePlugin.logError(message);
                return new Status(IStatus.ERROR, PhpmavenCorePlugin.PLUGIN_ID, message);
            }
            
            try {
                // fetch the maven facade
                final IMavenProjectFacade projectFacade = projectManager.create(project, monitor);
                if (projectFacade == null) {
                    projectManager.create(pomResource, true, monitor); // this
                                                                       // forces
                                                                       // the
                                                                       // pom
                                                                       // loading;
                                                                       // this
                                                                       // will
                                                                       // result
                                                                       // in
                                                                       // error
                                                                       // logs
                    final String message = NLS.bind(Messages.MavenJob_InvalidPom, new Object[] { project.getName() });
                    PhpmavenCorePlugin.logError(message);
                    return new Status(IStatus.ERROR, PhpmavenCorePlugin.PLUGIN_ID, message);
                }
                monitor.worked(20);
                
                // Refresh the maven project if it is needed
                if (projectFacade.isStale()) {
                    monitor.setTaskName(Messages.MavenJob_RefreshingPom);
                    final MavenUpdateRequest updateRequest = new MavenUpdateRequest(project, mavenConfiguration.isOffline() /* offline */, false /* updateSnapshots */);
                    projectManager.refresh(updateRequest, monitor);
                    if (projectManager.create(project, monitor) == null) {
                        // error marker should have been created
                        final String message = NLS.bind(Messages.MavenJob_InvalidPom, new Object[] { project.getName() });
                        PhpmavenCorePlugin.logError(message);
                        return new Status(IStatus.ERROR, PhpmavenCorePlugin.PLUGIN_ID, message);
                    }
                }
                monitor.worked(20);
                
                monitor.setTaskName(NLS.bind(Messages.MavenJob_RunningMavenGoals, new Object[] { Arrays.asList(this.data.getMavenCommands()).toString() }));
                final IMaven maven = MavenPlugin.getMaven();
                final MavenExecutionRequest request = projectManager.createExecutionRequest(pomResource, projectFacade.getResolverConfiguration(), monitor);
                request.setGoals(Arrays.asList(this.data.getMavenCommands()));
                
                this.data.manipulateRequest(request);
                
                if (!this.data.canProcessRequest(request, projectFacade)) {
                    return Status.OK_STATUS;
                }
                
                /*
                 * MavenProject mavenProject = null; mavenProject =
                 */projectFacade.getMavenProject(monitor);
                
                this.result = maven.execute(request, monitor);
                monitor.worked(40);
                if (this.result.hasExceptions()) {
                    for (final Throwable t : this.result.getExceptions()) {
                        PhpmavenCorePlugin.logError("Exception while running maven request", t); //$NON-NLS-1$
                    }
                    PhpmavenCorePlugin.logError("Build returned with errors"); //$NON-NLS-1$
                    return new Status(IStatus.ERROR, PhpmavenCorePlugin.PLUGIN_ID, "Build returned with errors.", this.result.getExceptions().get(0)); //$NON-NLS-1$
                }
                
                // refresh the whole project
                // TODO Should we refresh the whole workspace? What about
                // modules in a flat project layout?
                monitor.setTaskName(Messages.MavenJob_RefreshingProject);
                project.refreshLocal(IResource.DEPTH_INFINITE, null);
                monitor.worked(10);
            } catch (final CoreException ce) {
                // unable to read the project facade
                return new Status(IStatus.ERROR, PhpmavenCorePlugin.PLUGIN_ID, "Error during maven invocation", ce); //$NON-NLS-1$
            }
            return Status.OK_STATUS;
        } finally {
            this.sysout = MvnLoggingAppender.stopFetching();
            monitor.done();
        }
    }
    
    @Override
    protected IStatus run(final IProgressMonitor monitor) {
        return this.execute(monitor);
    }
    
    /**
     * Returns the maven results on successful execution
     * 
     * @return Maven result
     */
    public MavenExecutionResult getMavenResult() {
        return this.result;
    }
    
    /**
     * Returns the sys out while performing the job execution.
     * @return sys out
     */
    public String getSysout() {
        return this.sysout;
    }
    
}
