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
 * The single test case error or failure
 * 
 * @author Martin Eisengardt
 */
public interface ITestError {
    
    /**
     * Returns true if this is an error
     * 
     * @return boolean
     */
    boolean isError();
    
    /**
     * Returns true if this is a failure
     * 
     * @return boolean
     */
    boolean isFailure();
    
    /**
     * Returns the failure type (f.e. Exception class name)
     * 
     * @return type
     */
    String getType();
    
    /**
     * Returns the message (including trace)
     * 
     * @return message
     */
    String getMessage();
    
    /**
     * Returns the trace elements
     * 
     * @return trace
     */
    ITraceElement[] getTrace();
    
    /**
     * Returns the exact position within the test method
     * 
     * @return trace position of the error within the test method; null if the
     *         trace element was not found
     */
    ITraceElement getTestMethodTraceElement();
    
}
