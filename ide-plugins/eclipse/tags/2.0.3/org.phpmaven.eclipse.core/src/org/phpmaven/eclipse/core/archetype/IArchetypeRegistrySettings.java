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

import org.eclipse.core.runtime.CoreException;

/**
 * Interface to control the archetype registry settings.
 * 
 * @author mepeisen
 */
public interface IArchetypeRegistrySettings {
    
    /**
     * loads the remote xml into the cache.
     * @param def definition.
     * @throws CoreException
     */
    void doCache(IArchetypeRegistryDefinition def) throws CoreException;
    
    /**
     * removes an existing entry from cache.
     * @param def definition
     * @throws CoreException
     */
    void removeCache(IArchetypeRegistryDefinition def) throws CoreException;
    
    /**
     * removes an existing entry.
     * @param def
     * @throws CoreException
     */
    void remove(IArchetypeRegistryDefinition def) throws CoreException;
    
    /**
     * Creates a new entry.
     * @param title title
     * @param url url
     * @param timeoutInMinutes the timeout to refresh remote in minutes.
     * @return entry
     * @throws CoreException
     */
    IArchetypeRegistryDefinition create(String title, String url, int timeoutInMinutes) throws CoreException;
    
    /**
     * Returns the definitions being available.
     * @return definition.
     */
    Iterable<IArchetypeRegistryDefinition> getDefinitions();
    
}
