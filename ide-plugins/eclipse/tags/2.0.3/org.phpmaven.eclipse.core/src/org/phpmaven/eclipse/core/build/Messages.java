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
package org.phpmaven.eclipse.core.build;

import org.eclipse.osgi.util.NLS;

/**
 * Localized messages.
 * 
 * @author Martin Eisengardt
 */
public class Messages extends NLS {
    
    /** the bundle name */
    private static final String BUNDLE_NAME = "org.phpmaven.eclipse.core.build.messages"; //$NON-NLS-1$
    
    /** Buildpath container title */
    public static String Container_Title;
    
    /** Builder step: Reset the maven dependencies */
    public static String MavenProjectChangedListener_ResetMavenDependencies;
    
    static {
        // initialize resource bundle
        NLS.initializeMessages(Messages.BUNDLE_NAME, Messages.class);
    }
    
    /**
     * Constructor.
     */
    private Messages() {
    }
}
