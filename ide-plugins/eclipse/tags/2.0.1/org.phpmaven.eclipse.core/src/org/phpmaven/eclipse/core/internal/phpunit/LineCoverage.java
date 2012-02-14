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

import org.phpmaven.eclipse.core.phpunit.ILineCoverage;

/**
 * a line coverage
 */
public final class LineCoverage implements ILineCoverage {
    
    /** the target line number */
    private int lineNumber;
    
    /** number of calls */
    private int calls;
    
    /**
     * 
     */
    public LineCoverage() {
        // empty
    }
    
    /**
     * @return the lineNumber
     */
    @Override
    public int getLineNumber() {
        return this.lineNumber;
    }
    
    /**
     * @param lineNumber
     *            the lineNumber to set
     */
    public void setLineNumber(final int lineNumber) {
        this.lineNumber = lineNumber;
    }
    
    /**
     * @return the calls
     */
    @Override
    public int getCalls() {
        return this.calls;
    }
    
    /**
     * @param calls
     *            the calls to set
     */
    public void setCalls(final int calls) {
        this.calls = calls;
    }
    
}