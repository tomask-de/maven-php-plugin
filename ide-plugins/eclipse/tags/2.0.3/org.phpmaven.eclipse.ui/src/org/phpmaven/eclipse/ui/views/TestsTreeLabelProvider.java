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

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.phpmaven.eclipse.core.phpunit.ITestCase;
import org.phpmaven.eclipse.core.phpunit.ITestError;
import org.phpmaven.eclipse.core.phpunit.ITestSuite;
import org.phpmaven.eclipse.ui.PhpmavenUiPlugin;

/**
 * @author Martin Eisengardt <mep_eisen@web.de>
 * 
 */
public class TestsTreeLabelProvider extends LabelProvider {
    
    /**
     * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
     */
    @Override
    public Image getImage(final Object object) {
        if (object instanceof ITestSuite) {
            final ITestSuite suite = (ITestSuite) object;
            if (suite.getFailures() > 0 || suite.getErrors() > 0) {
                return PhpmavenUiPlugin.getImage("obj16/test/error"); //$NON-NLS-1$
            }
            return PhpmavenUiPlugin.getImage("obj16/test/ok"); //$NON-NLS-1$
        }
        
        if (object instanceof ITestCase) {
            final ITestCase test = (ITestCase) object;
            if (test.getErrors().length > 0) {
                return PhpmavenUiPlugin.getImage("obj16/test/error"); //$NON-NLS-1$
            }
            return PhpmavenUiPlugin.getImage("obj16/test/ok"); //$NON-NLS-1$
        }
        
        if (object instanceof ITestError) {
            return PhpmavenUiPlugin.getImage("obj16/test/error"); //$NON-NLS-1$
        }
        
        return null;
    }
    
    /**
     * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
     */
    @Override
    public String getText(final Object object) {
        if (object instanceof ITestSuite) {
            final ITestSuite suite = (ITestSuite) object;
            return suite.getName();
        }
        
        if (object instanceof ITestCase) {
            final ITestCase test = (ITestCase) object;
            return test.getClassName() + "::" + test.getName(); //$NON-NLS-1$
        }
        
        if (object instanceof ITestError) {
            return "failure"; //$NON-NLS-1$
        }
        
        return super.getText(object);
    }
    
}
