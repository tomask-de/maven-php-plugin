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

import java.util.Date;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.phpmaven.eclipse.core.mvn.ITestExecutionTooling.ITestResults;
import org.phpmaven.eclipse.core.phpunit.ITestCase;
import org.phpmaven.eclipse.core.phpunit.ITestError;
import org.phpmaven.eclipse.core.phpunit.ITestSuite;

/**
 * @author Martin Eisengardt <mep_eisen@web.de>
 * 
 */
public class PhpUnitResultComposite extends Composite {
    
    /** the start date */
    private final DateTime labelStartedDate;
    /** duration info */
    private final Label labelDurationMs;
    /** state */
    private final Label labelStateText;
    /** command line */
    private final Text labelCommandline;
    /** stdout */
    private final Text labelStdout;
    /** coverage tree */
    private final TreeViewer coverageTreeViewer;
    /** tests tree */
    private final TreeViewer testsTreeViewer;
    /** the tests details info */
    private final Text testInfoArea;
    /** the std out tab item */
    private final TabItem stdoutItem;
    /** the test results (if any) */
    private ITestResults results;
    
    /**
     * Constructor
     * 
     * @param parent
     *            the parent composite
     */
    public PhpUnitResultComposite(final Composite parent) {
        super(parent, SWT.NONE);
        
        // common infos
        
        final GridLayout gl = new GridLayout();
        gl.numColumns = 1;
        this.setLayout(gl);
        
        final Composite infoComposite = new Composite(this, SWT.BORDER);
        final GridData gdInfo = new GridData();
        gdInfo.horizontalAlignment = GridData.FILL;
        gdInfo.grabExcessHorizontalSpace = true;
        infoComposite.setLayoutData(gdInfo);
        final GridLayout glInfo = new GridLayout();
        glInfo.numColumns = 2;
        infoComposite.setLayout(glInfo);
        
        final Label labelStarted = new Label(infoComposite, SWT.NONE);
        labelStarted.setText(Messages.PhpUnitResultComposite_Label_Started);
        this.labelStartedDate = new DateTime(infoComposite, SWT.TIME | SWT.MEDIUM);
        this.labelStartedDate.setData(new Date());
        this.labelStartedDate.setEnabled(false);
        final GridData gdStartedDate = new GridData();
        gdStartedDate.horizontalAlignment = GridData.FILL;
        gdStartedDate.grabExcessHorizontalSpace = true;
        this.labelStartedDate.setLayoutData(gdStartedDate);
        
        final Label labelDuration = new Label(infoComposite, SWT.NONE);
        labelDuration.setText(Messages.PhpUnitResultComposite_Label_Duration);
        this.labelDurationMs = new Label(infoComposite, SWT.NONE);
        final GridData gdLabelDuration = new GridData();
        gdLabelDuration.horizontalAlignment = GridData.FILL;
        gdLabelDuration.grabExcessHorizontalSpace = true;
        this.labelDurationMs.setLayoutData(gdLabelDuration);
        
        final Label labelState = new Label(infoComposite, SWT.NONE);
        labelState.setText(Messages.PhpUnitResultComposite_Label_State);
        this.labelStateText = new Label(infoComposite, SWT.NONE);
        final GridData gdState = new GridData();
        gdState.horizontalAlignment = GridData.FILL;
        gdState.grabExcessHorizontalSpace = true;
        this.labelStateText.setText(Messages.PhpUnitResultComposite_State_Preparation);
        this.labelStateText.setLayoutData(gdState);
        
        // tab panel
        final TabFolder tabFolder = new TabFolder(this, SWT.NONE);
        final GridData gdTab = new GridData();
        gdTab.horizontalAlignment = GridData.FILL;
        gdTab.verticalAlignment = GridData.FILL;
        gdTab.grabExcessHorizontalSpace = true;
        gdTab.grabExcessVerticalSpace = true;
        tabFolder.setLayoutData(gdTab);
        
        // tab "tests"
        final TabItem testsItem = new TabItem(tabFolder, SWT.NONE);
        testsItem.setText(Messages.PhpUnitResultComposite_Label_TestResult);
        final Composite testsComposite = new Composite(tabFolder, SWT.NONE);
        final GridLayout glTests = new GridLayout();
        glTests.numColumns = 1;
        testsComposite.setLayout(glTests);
        testsItem.setControl(testsComposite);
        
        final Composite testsTreeComposite = new Composite(testsComposite, SWT.NONE);
        testsTreeComposite.setLayout(new FillLayout());
        this.testsTreeViewer = new TreeViewer(testsTreeComposite);
        this.testsTreeViewer.setContentProvider(new TestsTreeContentProvider());
        this.testsTreeViewer.setLabelProvider(new TestsTreeLabelProvider());
        this.testsTreeViewer.addSelectionChangedListener(new SelectionChangedListener());
        final GridData gdTestsTree = new GridData();
        gdTestsTree.horizontalAlignment = GridData.FILL;
        gdTestsTree.verticalAlignment = GridData.FILL;
        gdTestsTree.grabExcessHorizontalSpace = true;
        gdTestsTree.grabExcessVerticalSpace = true;
        testsTreeComposite.setLayoutData(gdTestsTree);
        
        this.testInfoArea = new Text(testsComposite, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
        final GridData gdTestsInfo = new GridData();
        gdTestsInfo.horizontalAlignment = GridData.FILL;
        gdTestsInfo.verticalAlignment = GridData.FILL;
        gdTestsInfo.grabExcessHorizontalSpace = true;
        gdTestsInfo.grabExcessVerticalSpace = true;
        this.testInfoArea.setLayoutData(gdTestsInfo);
        this.testInfoArea.setEditable(false);
        
        // tab "coverage"
        final TabItem covItem = new TabItem(tabFolder, SWT.NONE);
        covItem.setText(Messages.PhpUnitResultComposite_Label_CodeCoverage);
        final Composite covComposite = new Composite(tabFolder, SWT.NONE);
        covComposite.setLayout(new FillLayout());
        this.coverageTreeViewer = new TreeViewer(covComposite);
        this.coverageTreeViewer.setContentProvider(new CoverageTreeContentProvider());
        this.coverageTreeViewer.setLabelProvider(new CoverageTreeLabelProvider());
        covItem.setControl(covComposite);
        
        this.stdoutItem = new TabItem(tabFolder, SWT.NONE);
        this.stdoutItem.setText(Messages.PhpUnitResultComposite_Label_StdOut);
        final Composite stdoutComposite = new Composite(tabFolder, SWT.NONE);
        final GridLayout glStdout = new GridLayout();
        glStdout.numColumns = 1;
        stdoutComposite.setLayout(glStdout);
        this.stdoutItem.setControl(stdoutComposite);
        
        final Label labelStdoutCommandline = new Label(stdoutComposite, SWT.NONE);
        labelStdoutCommandline.setText(Messages.PhpUnitResultComposite_Label_CLI);
        this.labelCommandline = new Text(stdoutComposite, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
        this.labelCommandline.setEditable(false);
        final GridData gdCommandline = new GridData();
        gdCommandline.horizontalAlignment = GridData.FILL;
        gdCommandline.grabExcessHorizontalSpace = true;
        this.labelCommandline.setLayoutData(gdCommandline);
        
        final Label labelStdoutResult = new Label(stdoutComposite, SWT.NONE);
        labelStdoutResult.setText(Messages.PhpUnitResultComposite_Label_Result);
        this.labelStdout = new Text(stdoutComposite, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
        this.labelStdout.setEditable(false);
        final GridData gdStdout = new GridData();
        gdStdout.horizontalAlignment = GridData.FILL;
        gdStdout.verticalAlignment = GridData.FILL;
        gdStdout.grabExcessHorizontalSpace = true;
        gdStdout.grabExcessVerticalSpace = true;
        this.labelStdout.setLayoutData(gdStdout);
    }
    
    /**
     * Sets the command line and sets the state to running
     * 
     * @param commandline
     *            command line
     */
    public void setCommandLine(final String commandline) {
        this.labelCommandline.setText(commandline);
        this.labelStateText.setText(Messages.PhpUnitResultComposite_State_Running);
        this.testsTreeViewer.setInput(null);
        this.coverageTreeViewer.setInput(null);
        this.labelStdout.setText(Messages.PhpUnitResultComposite_Stdout_Empty);
        this.results = null;
    }
    
    /**
     * Sets the failure info (including stdout report)
     * 
     * @param stdout
     *            std out
     */
    public void setFailure(final String stdout) {
        this.labelStdout.setText(stdout == null ? Messages.PhpUnitResultComposite_Stdout_NULL : stdout);
        this.labelStateText.setText(Messages.PhpUnitResultComposite_State_FatalError);
        this.stdoutItem.getParent().setSelection(this.stdoutItem);
        this.testsTreeViewer.setInput(null);
        this.coverageTreeViewer.setInput(null);
        this.results = null;
    }
    
    /**
     * Sets the results
     * 
     * @param stdout
     *            stdout
     * @param results
     *            results
     */
    public void setResults(final String stdout, final ITestResults results) {
        this.results = results;
        this.testsTreeViewer.setInput(results);
        this.coverageTreeViewer.setInput(results);
        this.testsTreeViewer.expandToLevel(1);
        this.coverageTreeViewer.expandToLevel(1);
        for (final ITestSuite suite : results.getTestSuites()) {
            if (suite.getFailures() > 0 || suite.getErrors() > 0) {
                this.labelStateText.setText(Messages.PhpUnitResultComposite_State_Failures);
                return;
            }
        }
        this.labelStateText.setText(Messages.PhpUnitResultComposite_State_Success);
        this.labelStdout.setText(stdout);
        // TODO switch to tests tab
    }
    
    /**
     * 
     */
    public class SelectionChangedListener implements ISelectionChangedListener {
        
        /**
         * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
         */
        @Override
        public void selectionChanged(final SelectionChangedEvent evt) {
            if (evt.getSelection() instanceof IStructuredSelection && ((IStructuredSelection) evt.getSelection()).size() > 0) {
                final Object selected = ((IStructuredSelection) evt.getSelection()).getFirstElement();
                
                if (selected instanceof ITestSuite) {
                    final ITestSuite suite = (ITestSuite) selected;
                    PhpUnitResultComposite.this.testInfoArea.setText("file: " + suite.getFile() + "\n" + "name: " + suite.getName() + "\n" + "assertions: " + suite.getAssertions() + "\n" + "tests: "
                            + suite.getTests() + "\n" + "duration: " + suite.getTime() + "\n" + "failures: " + (suite.getErrors() + suite.getFailures()) + "\n");
                    return;
                }
                
                if (selected instanceof ITestCase) {
                    final ITestCase test = (ITestCase) selected;
                    PhpUnitResultComposite.this.testInfoArea.setText("class: " + test.getClassName() + "\n" + "method: " + test.getName() + "\n" + "line: " + test.getLine() + "\n" + "assertions: "
                            + test.getAssertions() + "\n" + "tests: " + test.getTime() + "\n");
                    return;
                }
                
                if (selected instanceof ITestError) {
                    PhpUnitResultComposite.this.testInfoArea.setText(((ITestError) selected).getMessage());
                    return;
                }
            }
            PhpUnitResultComposite.this.testInfoArea.setText("");
        }
        
    }

    /**
     * Returns the text result.
     * @return text result.
     */
    public ITestResults getResults() {
        return this.results;
    }
    
}
