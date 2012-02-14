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

import org.eclipse.osgi.util.NLS;

/**
 * Localized messages
 * 
 * @author Martin Eisengardt
 */
public class Messages extends NLS {
    /** the bundle name */
    private static final String BUNDLE_NAME = "org.phpmaven.eclipse.core.mvn.messages"; //$NON-NLS-1$
    /** Maven job: Begin */
    public static String MavenJob_BeginJob;
    /** Maven job: Invalid pom */
    public static String MavenJob_InvalidPom;
    /** Maven job: Pom not readable */
    public static String MavenJob_ProjectPomNotReadable;
    /** Maven job: Reading pom */
    public static String MavenJob_ReadingPom;
    /** Maven job: Refreshing pom */
    public static String MavenJob_RefreshingPom;
    /** Maven job: Refreshing project */
    public static String MavenJob_RefreshingProject;
    /** Maven job: Running maven goals */
    public static String MavenJob_RunningMavenGoals;
    /** Maven job: Job title */
    public static String MavenJob_Title;
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
