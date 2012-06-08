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

package org.phpmaven.eclipse.ui.views;

import org.eclipse.osgi.util.NLS;

/**
 * Messages.
 */
public class Messages extends NLS {
    /** */
    private static final String BUNDLE_NAME = "org.phpmaven.eclipse.ui.views.messages"; //$NON-NLS-1$
    /** */
    public static String CoverageTreeLabelProvider_Classes;
    /** */
    public static String CoverageTreeLabelProvider_Files;
    /** */
    public static String CoverageTreeLabelProvider_Lines;
    /** */
    public static String PhpUnitResultComposite_Label_CLI;
    /** */
    public static String PhpUnitResultComposite_Label_CodeCoverage;
    /** */
    public static String PhpUnitResultComposite_Label_Duration;
    /** */
    public static String PhpUnitResultComposite_Label_Result;
    /** */
    public static String PhpUnitResultComposite_Label_Started;
    /** */
    public static String PhpUnitResultComposite_Label_State;
    /** */
    public static String PhpUnitResultComposite_Label_StdOut;
    /** */
    public static String PhpUnitResultComposite_Label_TestResult;
    /** */
    public static String PhpUnitResultComposite_State_Failures;
    /** */
    public static String PhpUnitResultComposite_State_FatalError;
    /** */
    public static String PhpUnitResultComposite_State_Preparation;
    /** */
    public static String PhpUnitResultComposite_State_Running;
    /** */
    public static String PhpUnitResultComposite_State_Success;
    /** */
    public static String PhpUnitResultComposite_Stdout_Empty;
    /** */
    public static String PhpUnitResultComposite_Stdout_NULL;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
    
    /** */
    private Messages() {
    }
}
