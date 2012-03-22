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

package org.phpmaven.eclipse.ui.menus;

import org.eclipse.osgi.util.NLS;

/**
 * Localized messages
 * 
 * @author Martin Eisengardt
 */
public class Messages extends NLS {
    /** the bundle name */
    private static final String BUNDLE_NAME = "org.phpmaven.eclipse.ui.menus.messages"; //$NON-NLS-1$
    /** This is already a php maven project */
    public static String AddNatureAction_AlreadyPhpMavenProject;
    /** Message box title for errors */
    public static String AddNatureAction_ErrorTitle;
    /** This is an invalid packaging */
    public static String AddNatureAction_InvalidPackaging;
    /** Needing a php project */
    public static String AddNatureAction_NeedPhpProject;
    static {
        // initialize resource bundle
        NLS.initializeMessages(Messages.BUNDLE_NAME, Messages.class);
    }
    
    /**
     * Hidden constructor
     */
    private Messages() {
    }
}
