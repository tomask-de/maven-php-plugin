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
 * The single test case info
 * 
 * @author Martin Eisengardt
 */
public interface ITestCase {
    
    /**
     * Returns the test case name
     * 
     * @return test case name
     */
    String getName();
    
    /**
     * Returns the class name
     * 
     * @return class name
     */
    String getClassName();
    
    /**
     * Returns the file name
     * 
     * @return file name
     */
    String getFile();
    
    /**
     * Returns the line
     * 
     * @return line number
     */
    int getLine();
    
    /**
     * Returns the assertions
     * 
     * @return assertions
     */
    int getAssertions();
    
    /**
     * Returns the amount of time
     * 
     * @return amount of time
     */
    float getTime();
    
    /**
     * Returns the errors
     * 
     * @return errors
     */
    ITestError[] getErrors();
    
}
