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

package org.phpmaven.eclipse.ui.prj;

import org.eclipse.osgi.util.NLS;

/**
 * Messages
 */
public class Messages extends NLS {
    /** */
    private static final String BUNDLE_NAME = "org.phpmaven.eclipse.ui.prj.messages"; //$NON-NLS-1$
    /** */
    public static String NewProjectWizard_Title;
    /** */
    public static String ProjWizArchetypePage_Description;
    /** */
    public static String ProjWizArchetypePage_LabelFind;
    /** */
    public static String ProjWizArchetypePage_Title;
    /** */
    public static String ProjWizMavenIdsPage_DefaultName;
    /** */
    public static String ProjWizMavenIdsPage_Description;
    /** */
    public static String ProjWizMavenIdsPage_Error_ArtifactIDWithSpaces;
    /** */
    public static String ProjWizMavenIdsPage_Error_GroupIDWithSpaces;
    /** */
    public static String ProjWizMavenIdsPage_Error_InvalidArtifactID;
    /** */
    public static String ProjWizMavenIdsPage_Error_InvalidGroupID;
    /** */
    public static String ProjWizMavenIdsPage_Error_InvalidParent;
    /** */
    public static String ProjWizMavenIdsPage_Error_InvalidVersion;
    /** */
    public static String ProjWizMavenIdsPage_Error_MissingArtifactId;
    /** */
    public static String ProjWizMavenIdsPage_Error_MissingGroupID;
    /** */
    public static String ProjWizMavenIdsPage_Error_MissingVersion;
    /** */
    public static String ProjWizMavenIdsPage_LabelArtifactID;
    /** */
    public static String ProjWizMavenIdsPage_LabelDescription;
    /** */
    public static String ProjWizMavenIdsPage_LabelGroupID;
    /** */
    public static String ProjWizMavenIdsPage_LabelName;
    /** */
    public static String ProjWizMavenIdsPage_LabelVersion;
    /** */
    public static String ProjWizMavenIdsPage_Title;
    /** */
    public static String ProjWizProjectPage_Description;
    /** */
    public static String ProjWizProjectPage_Error_EnterLocation;
    /** */
    public static String ProjWizProjectPage_Error_InvalidLocation;
    /** */
    public static String ProjWizProjectPage_Error_LocationIsWorkspace;
    /** */
    public static String ProjWizProjectPage_Error_MissingProjectName;
    /** */
    public static String ProjWizProjectPage_Error_ProjectExists;
    /** */
    public static String ProjWizProjectPage_Title;
    
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
    
    /** */
    private Messages() {
    }
}
