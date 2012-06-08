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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.dltk.core.IScriptProject;
import org.phpmaven.eclipse.core.phpunit.ICoverageInfo;
import org.phpmaven.eclipse.core.phpunit.ITestSuite;

/**
 * A specialized tooling class that support executing tests.
 * 
 * @author Martin Eisengardt
 */
public interface ITestExecutionTooling {
    
    /**
     * Interface for returning test results
     * 
     * @author Martin Eisengardt
     */
    public interface ITestResults {
        
        /**
         * Returns the coverage info associated with this test run
         * 
         * @return coverage info
         */
        ICoverageInfo getCoverageInfo();
        
        /**
         * Returns the test suites info (the actual test results)
         * 
         * @return test suites or empty array if not test suites are available
         */
        ITestSuite[] getTestSuites();
        
        /**
         * Returns the test state (ok or fatal error info)
         * 
         * @return test state of this test run
         */
        ITestState getState();
        
    }
    
    /**
     * The failure information for a failing test.
     *
     */
    public interface ITestState {
        
        /**
         * Returns the status containing the failure description or an ok status.
         * 
         * @return status
         */
        IStatus getStatus();
        
        /**
         * Returns the std out that may be used by the user to analyze the problem or the succeeding test output.
         * 
         * @return std out to analyze the failure or the succeeding test output.
         */
        String getStdout();
        
        /**
         * Returns the xml type that was used.
         * @return xml type
         */
        String getXmlType();
        
        /**
         * Returns the coverage type that was used.
         * @return coverage type.
         */
        String getCoverageType();
        
        /**
         * Returns the xml content that was used.
         * @return xml content.
         */
        String getXmlContent();
        
        /**
         * Returns the coverage content that was used.
         * @return coverage content.
         */
        String getCoverageContent();
        
        /**
         * Returns the start date.
         * @return start date.
         */
        long getDateStarted();
        
        /**
         * Returns the end date.
         * @return end date.
         */
        long getEndDate();
        
    }
    
    /**
     * The available test modes
     */
    public enum TestMode {
        /**
         * to test the whole project (all available test classes)
         */
        WHOLE_PROJECT,
        
        /**
         * to test a list of test classes returned by
         * getTestClassesForResource()
         */
        TEST_CLASSES,
        
        /**
         * to test an IFolder
         */
        FOLDER,
        
        /**
         * to test an IFile
         */
        FILE
    }
    
    /**
     * returns true if this test tooling is responsible to execute the tests
     * within the given project
     * 
     * @param project
     *            project
     * @param scriptProject
     *            script project
     * @return true if this tooling should be used for given project
     */
    boolean isResponsibleForProject(IProject project, IScriptProject scriptProject);
    
    /**
     * Returns the preferred test mode for given resource
     * 
     * @param project
     *            project
     * @param scriptProject
     *            script project
     * @param resource
     *            the resource selected to test for
     * @return the preferred test mode for given resource
     */
    TestMode getTestMode(IProject project, IScriptProject scriptProject, IResource resource);
    
    /**
     * Returns the test classes for given resource; only used if getTestMode
     * returns TEST_CLASSES.
     * 
     * @param resource
     *            IProject, IFolder or IFile
     * @return Name of the available test classes
     */
    String[] getTestClassesForResource(IResource resource);
    
    /**
     * Tests the whole project
     * 
     * @param project
     *            project
     * @param scriptProject
     *            script project
     * @param monitor
     *            progress monitor
     * @return test results
     */
    ITestResults testProject(IProject project, IScriptProject scriptProject, IProgressMonitor monitor);
    
    /**
     * Tests the whole project
     * 
     * @param project
     *            project
     * @param scriptProject
     *            script project
     * @param toTest
     *            file to be tested
     * @param monitor
     *            progress monitor
     * @return test results
     */
    ITestResults testFile(IProject project, IScriptProject scriptProject, IFile toTest, IProgressMonitor monitor);
    
    /**
     * Tests the whole project
     * 
     * @param project
     *            project
     * @param scriptProject
     *            script project
     * @param toTest
     *            folder to be tested
     * @param monitor
     *            progress monitor
     * @return test results
     */
    ITestResults testFolder(IProject project, IScriptProject scriptProject, IFolder toTest, IProgressMonitor monitor);
    
    /**
     * Tests the whole project
     * 
     * @param project
     *            project
     * @param scriptProject
     *            script project
     * @param toTest
     *            classes to be tested
     * @param monitor
     *            progress monitor
     * @return test results
     */
    ITestResults testClasses(IProject project, IScriptProject scriptProject, String[] toTest, IProgressMonitor monitor);
    
}
