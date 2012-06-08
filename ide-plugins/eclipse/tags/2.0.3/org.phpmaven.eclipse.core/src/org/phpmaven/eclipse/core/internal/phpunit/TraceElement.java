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

import org.phpmaven.eclipse.core.phpunit.ITraceElement;

/**
 * Trace element
 * 
 * @author Martin Eisengardt
 * 
 */
public class TraceElement implements ITraceElement {
    
    /** */
    private String fileName;
    
    /** */
    private int line;
    
    /**
     * @param fileName
     *            the fileName to set
     */
    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }
    
    /**
     * @param line
     *            the line to set
     */
    public void setLine(final int line) {
        this.line = line;
    }
    
    /**
     * @see org.phpmaven.eclipse.core.phpunit.ITraceElement#getFileName()
     */
    @Override
    public String getFileName() {
        return this.fileName;
    }
    
    /**
     * @see org.phpmaven.eclipse.core.phpunit.ITraceElement#getLine()
     */
    @Override
    public int getLine() {
        return this.line;
    }
    
}
