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

import java.util.HashMap;
import java.util.Map;

import org.phpmaven.eclipse.core.phpunit.IClassCoverage;
import org.phpmaven.eclipse.core.phpunit.IMethodCoverage;

/**
 * Class coverage
 */
public final class ClassCoverage implements IClassCoverage {
    
    /**
     * Name of the class
     */
    private String className;
    
    /**
     * number of methods
     */
    private int methodCount;
    
    /**
     * number of covered methods
     */
    private int coveredMethods;
    
    /**
     * number of statements
     */
    private int statements;
    
    /**
     * number of covered statements
     */
    private int coveredStatements;
    
    /**
     * number of elements
     */
    private int elements;
    
    /**
     * number of covered elements
     */
    private int coveredElements;
    
    /**
     * the method coverage
     */
    private final Map<String, MethodCoverage> methodCoverage = new HashMap<String, MethodCoverage>();
    
    /**
     * @return the className
     */
    @Override
    public String getClassName() {
        return this.className;
    }
    
    /**
     * @param className
     *            the className to set
     */
    public void setClassName(final String className) {
        this.className = className;
    }
    
    /**
     * @return the methodCount
     */
    @Override
    public int getMethodCount() {
        return this.methodCount;
    }
    
    /**
     * @param methodCount
     *            the methodCount to set
     */
    public void setMethodCount(final int methodCount) {
        this.methodCount = methodCount;
    }
    
    /**
     * @return the coveredMethods
     */
    @Override
    public int getCoveredMethods() {
        return this.coveredMethods;
    }
    
    /**
     * @param coveredMethods
     *            the coveredMethods to set
     */
    public void setCoveredMethods(final int coveredMethods) {
        this.coveredMethods = coveredMethods;
    }
    
    /**
     * @return the statements
     */
    @Override
    public int getStatements() {
        return this.statements;
    }
    
    /**
     * @param statements
     *            the statements to set
     */
    public void setStatements(final int statements) {
        this.statements = statements;
    }
    
    /**
     * @return the coveredStatements
     */
    @Override
    public int getCoveredStatements() {
        return this.coveredStatements;
    }
    
    /**
     * @param coveredStatements
     *            the coveredStatements to set
     */
    public void setCoveredStatements(final int coveredStatements) {
        this.coveredStatements = coveredStatements;
    }
    
    /**
     * @return the elements
     */
    @Override
    public int getElements() {
        return this.elements;
    }
    
    /**
     * @param elements
     *            the elements to set
     */
    public void setElements(final int elements) {
        this.elements = elements;
    }
    
    /**
     * @return the coveredElements
     */
    @Override
    public int getCoveredElements() {
        return this.coveredElements;
    }
    
    /**
     * @param coveredElements
     *            the coveredElements to set
     */
    public void setCoveredElements(final int coveredElements) {
        this.coveredElements = coveredElements;
    }
    
    /**
     * @return the methodCoverage
     */
    @Override
    public IMethodCoverage[] getMethodCoverage() {
        return this.methodCoverage.values().toArray(new IMethodCoverage[this.methodCoverage.size()]);
    }
    
    /**
     * Constructor
     */
    public ClassCoverage() {
        // empty
    }
    
}