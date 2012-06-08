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
 * A trace element within a stack trace
 * 
 * @author Martin Eisengardt
 * 
 */
public interface ITraceElement {
    
    /**
     * Returns the file name
     * 
     * @return file name
     */
    String getFileName();
    
    /**
     * Returns the line number
     * 
     * @return line number
     */
    int getLine();
    
}
