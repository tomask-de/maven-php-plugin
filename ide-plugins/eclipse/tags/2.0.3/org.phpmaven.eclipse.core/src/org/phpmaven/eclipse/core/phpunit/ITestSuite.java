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
package org.phpmaven.eclipse.core.phpunit;

/**
 * The test suites info (test results)
 * 
 * @author Martin Eisengardt
 */
public interface ITestSuite {
    
    /**
     * Returns the test suite name
     * 
     * @return test suite name
     */
    String getName();
    
    /**
     * Returns the file name
     * 
     * @return file name
     */
    String getFile();
    
    /**
     * Returns the number of tests
     * 
     * @return number of tests
     */
    int getTests();
    
    /**
     * Returns the number of assertions
     * 
     * @return number of assertions
     */
    int getAssertions();
    
    /**
     * Returns the number of failures
     * 
     * @return number of filures
     */
    int getFailures();
    
    /**
     * Returns the number of errors
     * 
     * @return number of errors
     */
    int getErrors();
    
    /**
     * Returns the amount of time
     * 
     * @return time
     */
    float getTime();
    
    /**
     * Returns the test cases
     * 
     * @return test cases
     */
    ITestCase[] getTestCases();
    
    /**
     * Returns the child suites for this test suite.
     * @return child suites.
     */
    ITestSuite[] getSubSuites();
    
}
