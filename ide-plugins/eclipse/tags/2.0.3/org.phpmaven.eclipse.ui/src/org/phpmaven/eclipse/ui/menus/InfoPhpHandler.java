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

import java.io.ByteArrayInputStream;
import java.util.Properties;

import org.apache.maven.execution.MavenExecutionRequest;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.progress.UIJob;
import org.phpmaven.eclipse.core.MavenPhpUtils;
import org.phpmaven.eclipse.core.ResourceUtils;
import org.phpmaven.eclipse.core.mvn.IMavenJobData;
import org.phpmaven.eclipse.core.mvn.MavenJob;
import org.phpmaven.eclipse.ui.PhpmavenUiPlugin;

/**
 * Menu action to invoke phpunit tests on selected resource.
 * 
 * @author Martin Eisengardt
 */
public class InfoPhpHandler extends AbstractProjectHandler {
    
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
            super(Messages.InfoPhpHandler_JobTitle);
            this.resources = resources;
        }
        
        @Override
        public IStatus run(final IProgressMonitor monitor) {
            monitor.beginTask(Messages.InfoPhpHandler_Task_Starting, this.resources.length);
            for (final IResource resource : this.resources) {
                final IProject project = resource.getProject();
                monitor.setTaskName(Messages.InfoPhpHandler_Task_RunFor + project.getName());
                
                final IMavenProjectFacade facade = MavenPhpUtils.fetchProjectFacade(project);
                final IFolder targetFolder = project.getFolder(facade.getOutputLocation().removeFirstSegments(1).append("eclipse")); //$NON-NLS-1$
                final IFile phpInfoPhp = targetFolder.getFile("phpinfo.php"); //$NON-NLS-1$
                final IFile phpInfoTxt = targetFolder.getFile("phpinfo.txt"); //$NON-NLS-1$
                try {
                    ResourceUtils.mkdirs(targetFolder);
                    if (phpInfoPhp.exists()) {
                        phpInfoPhp.setContents(new ByteArrayInputStream(
                                ("<?php\n" + //$NON-NLS-1$
                        		"ob_start();\n" + //$NON-NLS-1$
                        		"phpinfo();\n" + //$NON-NLS-1$
                        		"file_put_contents(__DIR__.'/phpinfo.txt', ob_get_contents());\n" + //$NON-NLS-1$
                        		"ob_end_clean();\n").getBytes()), IResource.FORCE, monitor); //$NON-NLS-1$
                    } else {
                        phpInfoPhp.create(new ByteArrayInputStream(
                                ("<?php\n" + //$NON-NLS-1$
                                "ob_start();\n" + //$NON-NLS-1$
                                "phpinfo();\n" + //$NON-NLS-1$
                                "file_put_contents(__DIR__.'/phpinfo.txt', ob_get_contents());\n" + //$NON-NLS-1$
                                "ob_end_clean();\n").getBytes()), IResource.FORCE, monitor); //$NON-NLS-1$
                    }
                } catch (CoreException ex) {
                    final IStatus status = new Status(IStatus.ERROR, PhpmavenUiPlugin.PLUGIN_ID, Messages.InfoPhpHandler_ErrorCreatingPhpinfoPhp, ex);
                    monitor.done();
                    this.done(status);
                    return status;
                }
                
                String phpMavenVersion = null;
                try {
                    phpMavenVersion = MavenPhpUtils.getPhpmavenVersion(facade);
                } catch (CoreException ex) {
                    final IStatus status = new Status(IStatus.ERROR, PhpmavenUiPlugin.PLUGIN_ID, Messages.InfoPhpHandler_ErrorFetchingPhpmavenVersion, ex);
                    monitor.done();
                    this.done(status);
                    return status;
                }
                final String mavenCommand = "org.phpmaven:maven-php-plugin:" + phpMavenVersion + ":exec" ; //$NON-NLS-1$ //$NON-NLS-2$
                
                final IMavenJobData jobData = new IMavenJobData() {
                    
                    @Override
                    public void manipulateRequest(final MavenExecutionRequest request) {
                        final Properties sysProps = request.getSystemProperties();
                        sysProps.put("phpFile", phpInfoPhp.getLocation().toOSString()); //$NON-NLS-1$
                    }
                    
                    @Override
                    public IProject getProject() {
                        return project;
                    }
                    
                    @Override
                    public String[] getMavenCommands() {
                        return new String[] { mavenCommand };
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
                
                final Job job2 = new ExecutionUiJob(Messages.InfoPhpHandler_DisplayingInfo, phpInfoTxt);
                job2.setRule(this.getRule());
                job2.setUser(false);
                job2.schedule();
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
        /** the phpinfo txt file */
        private IFile phpInfoTxt;

        /**
         * Constructor
         * @param name
         * @param phpInfoTxt
         */
        private ExecutionUiJob(String name, IFile phpInfoTxt) {
            super(name);
            this.phpInfoTxt = phpInfoTxt;
        }
        
        @Override
        public IStatus runInUIThread(final IProgressMonitor monitor) {
            try {
                final IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(this.phpInfoTxt.getName());
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(new FileEditorInput(this.phpInfoTxt), desc.getId());
            } catch (Exception ex) {
                // TODO Exception handling
            }
            monitor.done();
            this.done(Status.OK_STATUS);
            return Status.OK_STATUS;
        }
    }
    
}
