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
import org.phpmaven.eclipse.core.phpunit.IClassCoverage;
import org.phpmaven.eclipse.core.phpunit.IFileCoverage;
import org.phpmaven.eclipse.core.phpunit.ILineCoverage;
import org.phpmaven.eclipse.core.phpunit.IMethodCoverage;
import org.phpmaven.eclipse.ui.PhpmavenUiPlugin;
import org.phpmaven.eclipse.ui.views.CoverageTreeContentProvider.ClassCoverage;
import org.phpmaven.eclipse.ui.views.CoverageTreeContentProvider.FileCoverage;
import org.phpmaven.eclipse.ui.views.CoverageTreeContentProvider.LineCoverage;

/**
 * @author Martin Eisengardt <mep_eisen@web.de>
 * 
 */
public class CoverageTreeLabelProvider extends LabelProvider {
    
    /**
     * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
     */
    @Override
    public Image getImage(final Object object) {
        if (object instanceof FileCoverage) {
            final FileCoverage cov = (FileCoverage) object;
            for (final IFileCoverage filecov : cov.fileCoverage) {
                for (final ILineCoverage linecov : filecov.getLineCoverage()) {
                    if (linecov.getCalls() == 0) {
                        return PhpmavenUiPlugin.getImage("obj16/test/error"); //$NON-NLS-1$
                    }
                }
            }
            return PhpmavenUiPlugin.getImage("obj16/test/ok"); //$NON-NLS-1$
        }
        
        if (object instanceof IFileCoverage) {
            for (final ILineCoverage linecov : ((IFileCoverage) object).getLineCoverage()) {
                if (linecov.getCalls() == 0) {
                    return PhpmavenUiPlugin.getImage("obj16/test/error"); //$NON-NLS-1$
                }
            }
            return PhpmavenUiPlugin.getImage("obj16/test/ok"); //$NON-NLS-1$
        }
        
        if (object instanceof ClassCoverage) {
            for (final IClassCoverage clscov : ((ClassCoverage) object).classCoverage) {
                if (clscov.getMethodCount() != clscov.getCoveredMethods()) {
                    return PhpmavenUiPlugin.getImage("obj16/test/error"); //$NON-NLS-1$
                }
            }
            return PhpmavenUiPlugin.getImage("obj16/test/ok"); //$NON-NLS-1$
        }
        
        if (object instanceof LineCoverage) {
            for (final ILineCoverage linecov : ((LineCoverage) object).lineCoverage) {
                if (linecov.getCalls() == 0) {
                    return PhpmavenUiPlugin.getImage("obj16/test/error"); //$NON-NLS-1$
                }
            }
            return PhpmavenUiPlugin.getImage("obj16/test/ok"); //$NON-NLS-1$
        }
        
        if (object instanceof IClassCoverage) {
            if (((IClassCoverage) object).getMethodCount() != ((IClassCoverage) object).getCoveredMethods()) {
                return PhpmavenUiPlugin.getImage("obj16/test/error"); //$NON-NLS-1$
            }
            return PhpmavenUiPlugin.getImage("obj16/test/ok"); //$NON-NLS-1$
        }
        
        if (object instanceof IMethodCoverage) {
            if (((IMethodCoverage) object).getCount() == 0) {
                return PhpmavenUiPlugin.getImage("obj16/test/error"); //$NON-NLS-1$
            }
            return PhpmavenUiPlugin.getImage("obj16/test/ok"); //$NON-NLS-1$
        }
        
        if (object instanceof ILineCoverage) {
            if (((ILineCoverage) object).getCalls() == 0) {
                return PhpmavenUiPlugin.getImage("obj16/test/error"); //$NON-NLS-1$
            }
            return PhpmavenUiPlugin.getImage("obj16/test/ok"); //$NON-NLS-1$
        }
        
        return null;
    }
    
    /**
     * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
     */
    @Override
    public String getText(final Object object) {
        if (object instanceof FileCoverage) {
            return Messages.CoverageTreeLabelProvider_Files;
        }
        
        if (object instanceof IFileCoverage) {
            return ((IFileCoverage) object).getFileName();
        }
        
        if (object instanceof ClassCoverage) {
            return Messages.CoverageTreeLabelProvider_Classes;
        }
        
        if (object instanceof LineCoverage) {
            return Messages.CoverageTreeLabelProvider_Lines;
        }
        
        if (object instanceof IClassCoverage) {
            return ((IClassCoverage) object).getClassName();
        }
        
        if (object instanceof IMethodCoverage) {
            return ((IMethodCoverage) object).getName();
        }
        
        if (object instanceof ILineCoverage) {
            return String.valueOf(((ILineCoverage) object).getLineNumber());
        }
        
        return super.getText(object);
    }
    
}
