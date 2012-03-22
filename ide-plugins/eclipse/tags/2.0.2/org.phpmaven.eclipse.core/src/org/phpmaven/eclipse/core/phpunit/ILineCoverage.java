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
 * A single line coverage
 * 
 * @author Martin Eisengardt
 * 
 */
public interface ILineCoverage {
    
    /**
     * Returns the line number
     * 
     * @return line number
     */
    int getLineNumber();
    
    /**
     * Returns the call count
     * 
     * @return call count
     */
    int getCalls();
    
}
