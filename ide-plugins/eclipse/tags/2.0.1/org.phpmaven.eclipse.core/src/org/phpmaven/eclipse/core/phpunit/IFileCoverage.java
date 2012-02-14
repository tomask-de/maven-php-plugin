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
 * The file coverage info
 * 
 * @author Martin Eisengardt
 */
public interface IFileCoverage {
    
    /**
     * The covered file name
     * 
     * @return covered file name
     */
    String getFileName();
    
    /**
     * The line coverage
     * 
     * @return line coverage
     */
    ILineCoverage[] getLineCoverage();
    
    /**
     * The class coverage
     * 
     * @return class coverage
     */
    IClassCoverage[] getClassCoverage();
    
}
