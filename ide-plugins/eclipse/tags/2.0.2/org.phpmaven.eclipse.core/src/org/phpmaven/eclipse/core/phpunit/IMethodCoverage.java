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
 * The method coverage
 * 
 * @author Martin Eisengardt
 */
public interface IMethodCoverage {
    
    /**
     * Returns the name of the method
     * 
     * @return name of the method
     */
    String getName();
    
    /**
     * Returns the method invocation count
     * 
     * @return method invocation count
     */
    int getCount();
    
    /**
     * Returns the method change of risk
     * 
     * @return method change of risk
     */
    int getCrap();
    
}
