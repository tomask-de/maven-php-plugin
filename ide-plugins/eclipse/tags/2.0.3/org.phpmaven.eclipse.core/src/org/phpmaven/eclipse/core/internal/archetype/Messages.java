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

import org.eclipse.osgi.util.NLS;

/**
 * @author mepeisen
 *
 */
public class Messages extends NLS {
    /** */
    private static final String BUNDLE_NAME = "org.phpmaven.eclipse.core.internal.archetype.messages"; //$NON-NLS-1$
    /** */
    public static String DummyArchetype_Description;
    /** */
    public static String DummyArchetype_Name;
    /** */
    public static String DummyArchetype_Task_AddPhpMavenNature;
    /** */
    public static String DummyArchetype_Task_AddPhpNature;
    /** */
    public static String DummyArchetype_Task_ApplyParent;
    /** */
    public static String DummyArchetype_Task_GenerateFolders;
    /** */
    public static String DummyArchetype_Task_GeneratePom;
    /** */
    public static String DummyArchetype_Task_GenerateProject;
    /** */
    public static String DummyArchetype_Task_ResolveParent;
    /** */
    public static String DummyArchetype_Task_Title;
    /** */
    public static String GenericArchetype_CreateProject;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
    
    /** */
    private Messages() {
    }
}
