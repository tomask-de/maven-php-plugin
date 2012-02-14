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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.phpmaven.eclipse.core.mvn.ITestExecutionTooling.ITestResults;

/**
 * View to display every tool running
 * 
 * @author Martin Eisengardt <mep_eisen@web.de>
 */
public class PhpUnitResultView extends ViewPart implements IResultDisplayPart {
    
    /** the result composite */
    private PhpUnitResultComposite resultComposite;
    
    /**
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPartControl(final Composite parent) {
        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new FillLayout());
        this.resultComposite = new PhpUnitResultComposite(composite);
    }
    
    /**
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    @Override
    public void setFocus() {
        // ignore
    }
    
    /**
     * Sets the command line and sets the state to running
     * 
     * @param commandline
     *            command line
     */
    @Override
    public void setCommandLine(final String commandline) {
        this.resultComposite.setCommandLine(commandline);
    }
    
    /**
     * Sets the failure info (including stdout report)
     * 
     * @param stdout
     *            std out
     */
    @Override
    public void setFailure(final String stdout) {
        this.resultComposite.setFailure(stdout);
    }
    
    /**
     * Sets the results
     * 
     * @param stdout
     *            stdout
     * @param results
     *            results
     */
    @Override
    public void setResults(final String stdout, final ITestResults results) {
        this.resultComposite.setResults(stdout, results);
    }
    
}
