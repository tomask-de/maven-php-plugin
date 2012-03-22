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

package org.phpmaven.eclipse.core.internal.phpunit;

import java.util.ArrayList;
import java.util.List;

import org.phpmaven.eclipse.core.phpunit.ITestCase;
import org.phpmaven.eclipse.core.phpunit.ITestSuite;

/**
 * The test suite
 * 
 * @author Martin Eisengardt
 * 
 */
public class TestSuite implements ITestSuite {
    
    /** */
    private String name;
    
    /** */
    private final List<ITestCase> list = new ArrayList<ITestCase>();
    
    /** */
    private float time;
    
    /** */
    private int errors;
    
    /** */
    private int failures;
    
    /** */
    private int assertions;
    
    /** */
    private int tests;
    
    /** */
    private String file;
    
    /**
     * @see org.phpmaven.eclipse.core.phpunit.ITestSuite#getName()
     */
    @Override
    public String getName() {
        return this.name;
    }
    
    /**
     * @see org.phpmaven.eclipse.core.phpunit.ITestSuite#getFile()
     */
    @Override
    public String getFile() {
        return this.file;
    }
    
    /**
     * @see org.phpmaven.eclipse.core.phpunit.ITestSuite#getTests()
     */
    @Override
    public int getTests() {
        return this.tests;
    }
    
    /**
     * @param name
     *            the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }
    
    /**
     * @param time
     *            the time to set
     */
    public void setTime(final float time) {
        this.time = time;
    }
    
    /**
     * @param errors
     *            the errors to set
     */
    public void setErrors(final int errors) {
        this.errors = errors;
    }
    
    /**
     * @param failures
     *            the failures to set
     */
    public void setFailures(final int failures) {
        this.failures = failures;
    }
    
    /**
     * @param assertions
     *            the assertions to set
     */
    public void setAssertions(final int assertions) {
        this.assertions = assertions;
    }
    
    /**
     * @param tests
     *            the tests to set
     */
    public void setTests(final int tests) {
        this.tests = tests;
    }
    
    /**
     * @param file
     *            the file to set
     */
    public void setFile(final String file) {
        this.file = file;
    }
    
    /**
     * @see org.phpmaven.eclipse.core.phpunit.ITestSuite#getAssertions()
     */
    @Override
    public int getAssertions() {
        return this.assertions;
    }
    
    /**
     * @see org.phpmaven.eclipse.core.phpunit.ITestSuite#getFailures()
     */
    @Override
    public int getFailures() {
        return this.failures;
    }
    
    /**
     * @see org.phpmaven.eclipse.core.phpunit.ITestSuite#getErrors()
     */
    @Override
    public int getErrors() {
        return this.errors;
    }
    
    /**
     * @see org.phpmaven.eclipse.core.phpunit.ITestSuite#getTime()
     */
    @Override
    public float getTime() {
        return this.time;
    }
    
    /**
     * Adds an existing test case
     * 
     * @param testCase
     *            test case
     */
    public void addTestCase(final TestCase testCase) {
        this.list.add(testCase);
    }
    
    /**
     * @see org.phpmaven.eclipse.core.phpunit.ITestSuite#getTestCases()
     */
    @Override
    public ITestCase[] getTestCases() {
        return this.list.toArray(new ITestCase[this.list.size()]);
    }
    
}
