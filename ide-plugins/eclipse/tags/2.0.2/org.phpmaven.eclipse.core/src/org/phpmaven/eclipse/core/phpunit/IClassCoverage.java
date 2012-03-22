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
 * Class coverage metrics
 * 
 * @author Martin Eisengardt
 */
public interface IClassCoverage {
    
    /**
     * Returns the full qualified class name
     * 
     * @return class name
     */
    String getClassName();
    
    /**
     * Gets the method count within this class
     * 
     * @return method count
     */
    int getMethodCount();
    
    /**
     * Gets the number of covered methods
     * 
     * @return covered method count
     */
    int getCoveredMethods();
    
    /**
     * Gets the number of statements within the class
     * 
     * @return statement count
     */
    int getStatements();
    
    /**
     * Returns the number of covered statements
     * 
     * @return covered statements count
     */
    int getCoveredStatements();
    
    /**
     * Returns the number of elements
     * 
     * @return number of elements
     */
    int getElements();
    
    /**
     * Returns the number of covered elements
     * 
     * @return covered element count
     */
    int getCoveredElements();
    
    /**
     * Returns the method coverages
     * 
     * @return method coverage
     */
    IMethodCoverage[] getMethodCoverage();
    
}
