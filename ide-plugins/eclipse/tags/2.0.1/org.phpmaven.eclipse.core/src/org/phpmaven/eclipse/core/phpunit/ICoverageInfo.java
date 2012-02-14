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
 * The associated coverage info
 * 
 * @author Martin Eisengardt
 */
public interface ICoverageInfo {
    
    /**
     * Returns the file coverage
     * 
     * @return file coverage
     */
    IFileCoverage[] getFileCoverage();
    
    /**
     * Returns the file coverage for given file name
     * 
     * @param filename
     *            file name
     * @return file coverage
     */
    IFileCoverage getFileCoverage(String filename);
    
}
