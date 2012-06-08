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

package org.phpmaven.eclipse.ui.editors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.editors.text.TextEditor;
import org.phpmaven.eclipse.core.mvn.ITestExecutionTooling.ITestResults;
import org.phpmaven.eclipse.ui.views.IResultDisplayPart;
import org.phpmaven.eclipse.ui.views.PhpUnitResultComposite;

/**
 * The test result editor.
 */
public class TestResultEditor extends TextEditor implements IResultDisplayPart {

    /** the result composite. */
	private PhpUnitResultComposite resultComposite;

	/**
	 * Constructor.
	 */
    public TestResultEditor() {
		super();
		setDocumentProvider(new TestResultDocumentProvider());
	}
	
    @Override
	public void dispose() {
		super.dispose();
	}

    @Override
    public void createPartControl(Composite parent) {
        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new FillLayout());
        this.resultComposite = new PhpUnitResultComposite(composite);
    }
    
    @Override
    protected void doSetInput(IEditorInput input) throws CoreException {
        // TODO Auto-generated method stub
        super.doSetInput(input);
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
