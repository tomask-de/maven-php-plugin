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

package org.phpmaven.eclipse.core;

import org.apache.maven.execution.MavenExecutionRequest;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.phpmaven.eclipse.core.mvn.IMavenJobData;
import org.phpmaven.eclipse.core.mvn.MavenJob;

/**
 * A helper class to fetch the maven project from eclipse project.
 * 
 * @author Martin Eisengardt
 */
class FetchMavenProject {
    
    /**
     * Maven job data
     */
    private final class JobData implements IMavenJobData {
        /** The eclipse project */
        private final IProject project;
        
        /** the maven project */
        private IMavenProjectFacade mavenProject;
        
        /**
         * Constructor.
         * 
         * @param project
         *            Eclipse project
         */
        private JobData(final IProject project) {
            this.project = project;
        }
        
        @Override
        public void manipulateRequest(final MavenExecutionRequest request) {
            // does nothing
        }
        
        @Override
        public IProject getProject() {
            return this.project;
        }
        
        @Override
        public String[] getMavenCommands() {
            // not important because it won't be executed
            return new String[] { "clean" }; //$NON-NLS-1$
        }
        
        @Override
        public boolean canProcessRequest(final MavenExecutionRequest request, final IMavenProjectFacade projectFacade) {
            this.mavenProject = projectFacade;
            // do not execute
            return false;
        }
        
        /**
         * Returns the maven project facade.
         * 
         * @return maven project facade.
         */
        public IMavenProjectFacade getMavenProject() {
            return this.mavenProject;
        }
    }
    
    /**
     * Returns the maven project for eclipse project.
     * 
     * @param project
     *            eclipse project.
     * @return maven project.
     */
    public IMavenProjectFacade fetch(final IProject project) {
        final JobData data = new JobData(project);
        final MavenJob job = new MavenJob(data);
        job.execute(new NullProgressMonitor());
        return data.getMavenProject();
    }
    
}
