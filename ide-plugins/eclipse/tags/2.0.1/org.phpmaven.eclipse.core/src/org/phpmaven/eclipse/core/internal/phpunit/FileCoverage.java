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

import java.util.HashMap;
import java.util.Map;

import org.phpmaven.eclipse.core.phpunit.IClassCoverage;
import org.phpmaven.eclipse.core.phpunit.IFileCoverage;
import org.phpmaven.eclipse.core.phpunit.ILineCoverage;

/**
 * a file coverage info
 */
public final class FileCoverage implements IFileCoverage {
    
    /** the file name */
    private String fileName;
    
    /** line coverages */
    private final Map<Integer, LineCoverage> lineCoverage = new HashMap<Integer, LineCoverage>();
    
    /** class coverages */
    private final Map<String, ClassCoverage> classCoverage = new HashMap<String, ClassCoverage>();
    
    /**
     * 
     */
    public FileCoverage() {
        // empty
    }
    
    /**
     * @return the fileName
     */
    @Override
    public String getFileName() {
        return this.fileName;
    }
    
    /**
     * @param fileName
     *            the fileName to set
     */
    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }
    
    /**
     * @return the lineCoverage
     */
    @Override
    public ILineCoverage[] getLineCoverage() {
        return this.lineCoverage.values().toArray(new ILineCoverage[this.lineCoverage.size()]);
    }
    
    /**
     * @see org.phpmaven.eclipse.core.phpunit.IFileCoverage#getClassCoverage()
     */
    @Override
    public IClassCoverage[] getClassCoverage() {
        return this.classCoverage.values().toArray(new IClassCoverage[this.classCoverage.size()]);
    }
    
    /**
     * Adds line coverage
     * 
     * @param cov
     *            line coverage
     */
    public void addLineCoverage(final LineCoverage cov) {
        this.lineCoverage.put(cov.getLineNumber(), cov);
    }
    
    /**
     * Adds class coverage
     * 
     * @param cov
     *            class coverage
     */
    public void addClassCoverage(final ClassCoverage cov) {
        this.classCoverage.put(cov.getClassName(), cov);
    }
    
    /**
     * @param lineNumber
     *            line number
     * @return coverage or null
     */
    public LineCoverage getLineCoverage(final int lineNumber) {
        return this.lineCoverage.get(lineNumber);
    }
    
}