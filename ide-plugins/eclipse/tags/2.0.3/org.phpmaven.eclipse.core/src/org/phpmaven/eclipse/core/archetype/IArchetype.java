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

package org.phpmaven.eclipse.core.archetype;

import org.apache.maven.archetype.catalog.Archetype;
import org.eclipse.core.runtime.CoreException;

/**
 * A single archetype.
 * 
 * @author mepeisen
 */
public interface IArchetype {
    
    /**
     * A non-archetype or empty placeholder for clean projects.
     * @return true for the empty project placeholder
     */
    boolean isEmpty();
    
    /**
     * Returns the archetype.
     * @return Archetype
     */
    Archetype getArchetype();
    
    /**
     * Returns the archetype group id.
     * @return archetype group id
     */
    String getGroupId();
    
    /**
     * Returns the archetype artifact id.
     * @return archetype artifact id.
     */
    String getArtifactId();
    
    /**
     * Returns the archetype version.
     * @return archetype version.
     */
    String getVersion();
    
    /**
     * Returns the name of the archetype.
     * @return archetype name.
     */
    String getName();
    
    /**
     * Returns the archetype description.
     * @return archetype description.
     */
    String getDescription();
    
    /**
     * Creates a new project from this archetype.
     * @param request the project creation request.
     * @throws CoreException thrown on errors
     */
    void createProject(ProjectCreationRequest request) throws CoreException;
    
}
