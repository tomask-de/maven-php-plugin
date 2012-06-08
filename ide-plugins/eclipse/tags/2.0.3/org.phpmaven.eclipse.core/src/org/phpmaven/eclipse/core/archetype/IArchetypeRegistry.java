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

/**
 * The archetype registry. Receive an existing instance through PhpmavenUiPlugin.
 * 
 * @author mepeisen
 */
public interface IArchetypeRegistry {
    
    /**
     * Returns the known archetypes.
     * @return known archetypes.
     */
    Iterable<IArchetype> getArchetypes();
    
}
