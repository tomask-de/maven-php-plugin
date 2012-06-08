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

package org.phpmaven.eclipse.core.internal.archetype;

import org.w3c.dom.Node;

/**
 * The core definition (php-maven domain) of the archetype registry.
 * 
 * @author mepeisen
 */
public class ArchetypeRegistryCoreDefinition extends ArchetypeRegistryDefinition {

    /**
     * Constructor to read from xml
     * @param n
     */
    public ArchetypeRegistryCoreDefinition(Node n) {
        super(n);
    }
    
    /**
     * Default constructor
     */
    public ArchetypeRegistryCoreDefinition() {
        super("core", "PHP-Maven", "http://www.php-maven.org/archetype-catalog.xml", 24*60); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    @Override
    public String getUrl() {
        return "http://www.php-maven.org/archetype-catalog.xml"; //$NON-NLS-1$
    }

    @Override
    public void setUrl(String url) {
        // ignore
    }

    @Override
    public boolean isSystem() {
        return true;
    }

    @Override
    public String getId() {
        return "core"; //$NON-NLS-1$
    }
    
}
