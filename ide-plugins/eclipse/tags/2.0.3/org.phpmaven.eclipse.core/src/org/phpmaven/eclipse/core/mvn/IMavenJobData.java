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

import org.apache.maven.execution.MavenExecutionRequest;
import org.eclipse.core.resources.IProject;
import org.eclipse.m2e.core.project.IMavenProjectFacade;

/**
 * A maven job on an eclipse project.
 * 
 * @author Martin Eisengardt
 */
public interface IMavenJobData {
    
    /**
     * Returns the project
     * 
     * @return project
     */
    public IProject getProject();
    
    /**
     * Returns the maven commands
     * 
     * @return maven commands
     */
    public String[] getMavenCommands();
    
    /**
     * Manipulates the requests
     * 
     * @param request
     *            the maven request
     */
    void manipulateRequest(MavenExecutionRequest request);
    
    /**
     * returns true if the process can be executed
     * 
     * @param request
     *            request
     * @param projectFacade
     *            project facade
     * @return boolean
     */
    boolean canProcessRequest(MavenExecutionRequest request, IMavenProjectFacade projectFacade);
    
}
