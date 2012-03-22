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

import org.phpmaven.eclipse.core.phpunit.ITestError;
import org.phpmaven.eclipse.core.phpunit.ITraceElement;

/**
 * Test error implementation
 * 
 * @author Martin Eisengardt
 */
public class TestError implements ITestError {
    
    /** */
    private final List<ITraceElement> trace = new ArrayList<ITraceElement>();
    /** */
    private boolean isError;
    /** */
    private boolean isFailure;
    /** */
    private String type;
    /** */
    private String message;
    /** */
    private ITraceElement testMethodTraceElement;
    
    /**
     * @see org.phpmaven.eclipse.core.phpunit.ITestError#isError()
     */
    @Override
    public boolean isError() {
        return this.isError;
    }
    
    /**
     * @param isError
     *            the isError to set
     */
    public void setError(final boolean isError) {
        this.isError = isError;
    }
    
    /**
     * @param isFailure
     *            the isFailure to set
     */
    public void setFailure(final boolean isFailure) {
        this.isFailure = isFailure;
    }
    
    /**
     * @param type
     *            the type to set
     */
    public void setType(final String type) {
        this.type = type;
    }
    
    /**
     * @param message
     *            the message to set
     */
    public void setMessage(final String message) {
        this.message = message;
    }
    
    /**
     * @param testMethodTraceElement
     *            the testMethodTraceElement to set
     */
    public void setTestMethodTraceElement(final ITraceElement testMethodTraceElement) {
        this.testMethodTraceElement = testMethodTraceElement;
    }
    
    /**
     * @see org.phpmaven.eclipse.core.phpunit.ITestError#isFailure()
     */
    @Override
    public boolean isFailure() {
        return this.isFailure;
    }
    
    /**
     * @see org.phpmaven.eclipse.core.phpunit.ITestError#getType()
     */
    @Override
    public String getType() {
        return this.type;
    }
    
    /**
     * @see org.phpmaven.eclipse.core.phpunit.ITestError#getMessage()
     */
    @Override
    public String getMessage() {
        return this.message;
    }
    
    /**
     * @see org.phpmaven.eclipse.core.phpunit.ITestError#getTrace()
     */
    @Override
    public ITraceElement[] getTrace() {
        return this.trace.toArray(new ITraceElement[this.trace.size()]);
    }
    
    /**
     * @see org.phpmaven.eclipse.core.phpunit.ITestError#getTestMethodTraceElement()
     */
    @Override
    public ITraceElement getTestMethodTraceElement() {
        return this.testMethodTraceElement;
    }
    
    /**
     * Adds a trace element
     * 
     * @param e
     *            element
     */
    public void addTraceElement(final ITraceElement e) {
        this.trace.add(e);
    }
    
}
