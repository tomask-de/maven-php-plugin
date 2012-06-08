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
import org.phpmaven.eclipse.core.phpunit.ITestError;

/**
 * test case implementation
 * 
 * @author Martin Eisengardt
 * 
 */
public class TestCase implements ITestCase {
    
    /** */
    private float time;
    /** */
    private int assertions;
    /** */
    private int line;
    /** */
    private String file;
    /** */
    private String className;
    /** */
    private String name;
    /** */
    private final List<ITestError> list = new ArrayList<ITestError>();
    
    /**
     * @see org.phpmaven.eclipse.core.phpunit.ITestCase#getName()
     */
    @Override
    public String getName() {
        return this.name;
    }
    
    /**
     * @see org.phpmaven.eclipse.core.phpunit.ITestCase#getClassName()
     */
    @Override
    public String getClassName() {
        return this.className;
    }
    
    /**
     * @see org.phpmaven.eclipse.core.phpunit.ITestCase#getFile()
     */
    @Override
    public String getFile() {
        return this.file;
    }
    
    /**
     * @see org.phpmaven.eclipse.core.phpunit.ITestCase#getLine()
     */
    @Override
    public int getLine() {
        return this.line;
    }
    
    /**
     * @see org.phpmaven.eclipse.core.phpunit.ITestCase#getAssertions()
     */
    @Override
    public int getAssertions() {
        return this.assertions;
    }
    
    /**
     * @see org.phpmaven.eclipse.core.phpunit.ITestCase#getTime()
     */
    @Override
    public float getTime() {
        return this.time;
    }
    
    /**
     * Adds a new test error
     * 
     * @param error
     *            error
     */
    public void addError(final TestError error) {
        this.list.add(error);
    }
    
    /**
     * @param time
     *            the time to set
     */
    public void setTime(final float time) {
        this.time = time;
    }
    
    /**
     * @param assertions
     *            the assertions to set
     */
    public void setAssertions(final int assertions) {
        this.assertions = assertions;
    }
    
    /**
     * @param line
     *            the line to set
     */
    public void setLine(final int line) {
        this.line = line;
    }
    
    /**
     * @param file
     *            the file to set
     */
    public void setFile(final String file) {
        this.file = file;
    }
    
    /**
     * @param className
     *            the className to set
     */
    public void setClassName(final String className) {
        this.className = className;
    }
    
    /**
     * @param name
     *            the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }
    
    /**
     * @see org.phpmaven.eclipse.core.phpunit.ITestCase#getErrors()
     */
    @Override
    public ITestError[] getErrors() {
        return this.list.toArray(new ITestError[this.list.size()]);
    }
    
}
