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

import org.phpmaven.eclipse.core.phpunit.IMethodCoverage;

/**
 * the method coverage
 */
public final class MethodCoverage implements IMethodCoverage {
    
    /** method name */
    private String name;
    
    /** number of count */
    private int count;
    
    /** method change of risk */
    private int crap;
    
    /**
     * Constructor
     */
    public MethodCoverage() {
        // empty
    }
    
    /**
     * @return the name
     */
    @Override
    public String getName() {
        return this.name;
    }
    
    /**
     * @param name
     *            the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }
    
    /**
     * @return the count
     */
    @Override
    public int getCount() {
        return this.count;
    }
    
    /**
     * @param count
     *            the count to set
     */
    public void setCount(final int count) {
        this.count = count;
    }
    
    /**
     * @return the crap
     */
    @Override
    public int getCrap() {
        return this.crap;
    }
    
    /**
     * @param crap
     *            the crap to set
     */
    public void setCrap(final int crap) {
        this.crap = crap;
    }
    
}