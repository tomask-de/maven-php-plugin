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

import org.phpmaven.eclipse.core.phpunit.ICoverageInfo;
import org.phpmaven.eclipse.core.phpunit.IFileCoverage;

/**
 * Information on coverage
 * 
 * @author Martin Eisengardt
 */
public class CoverageInfo implements ICoverageInfo {
    
    /**
     * the coverage infos
     */
    private final Map<String, FileCoverage> infos = new HashMap<String, FileCoverage>();
    
    /**
     * @see org.phpmaven.eclipse.core.phpunit.ICoverageInfo#getFileCoverage()
     */
    @Override
    public IFileCoverage[] getFileCoverage() {
        return this.infos.values().toArray(new IFileCoverage[this.infos.size()]);
    }
    
    /**
     * @see org.phpmaven.eclipse.core.phpunit.ICoverageInfo#getFileCoverage(String)
     */
    @Override
    public IFileCoverage getFileCoverage(final String filename) {
        return this.infos.get(filename);
    }
    
    /**
     * Adds the file coverage
     * 
     * @param coverage
     *            file coverage
     */
    public void addFileCoverage(final FileCoverage coverage) {
        this.infos.put(coverage.getFileName(), coverage);
    }
    
}
