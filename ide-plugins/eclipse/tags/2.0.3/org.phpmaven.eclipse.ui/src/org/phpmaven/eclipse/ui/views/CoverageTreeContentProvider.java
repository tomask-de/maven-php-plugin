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

package org.phpmaven.eclipse.ui.views;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.phpmaven.eclipse.core.mvn.ITestExecutionTooling.ITestResults;
import org.phpmaven.eclipse.core.phpunit.IClassCoverage;
import org.phpmaven.eclipse.core.phpunit.ICoverageInfo;
import org.phpmaven.eclipse.core.phpunit.IFileCoverage;
import org.phpmaven.eclipse.core.phpunit.ILineCoverage;
import org.phpmaven.eclipse.core.phpunit.IMethodCoverage;

/**
 * @author Martin Eisengardt <mep_eisen@web.de>
 * 
 */
public class CoverageTreeContentProvider implements ITreeContentProvider {
    
    /** parent relationships */
    private final Map<Object, Object> childrenToParents = new HashMap<Object, Object>();
    
    /**
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    @Override
    public void dispose() {
        this.childrenToParents.clear();
    }
    
    /**
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
     *      java.lang.Object, java.lang.Object)
     */
    @Override
    public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
        this.childrenToParents.clear();
    }
    
    /**
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    @Override
    public Object[] getChildren(final Object parent) {
        if (parent instanceof ITestResults) {
            final ICoverageInfo covInfo = ((ITestResults) parent).getCoverageInfo();
            final FileCoverage fcov = new FileCoverage(covInfo.getFileCoverage());
            this.childrenToParents.put(fcov, parent);
            return new Object[] { fcov };
        }
        
        if (parent instanceof FileCoverage) {
            final IFileCoverage[] filecovs = ((FileCoverage) parent).fileCoverage;
            for (final IFileCoverage cov : filecovs) {
                this.childrenToParents.put(cov, parent);
            }
            return filecovs;
        }
        
        if (parent instanceof IFileCoverage) {
            final IFileCoverage filecov = (IFileCoverage) parent;
            final ClassCoverage clscov = new ClassCoverage(filecov.getClassCoverage());
            final LineCoverage linecov = new LineCoverage(filecov.getLineCoverage());
            this.childrenToParents.put(clscov, parent);
            this.childrenToParents.put(linecov, parent);
            return new Object[] { clscov, linecov };
        }
        
        if (parent instanceof ClassCoverage) {
            final IClassCoverage[] clscovs = ((ClassCoverage) parent).classCoverage;
            for (final IClassCoverage cov : clscovs) {
                this.childrenToParents.put(cov, parent);
            }
            return clscovs;
        }
        
        if (parent instanceof LineCoverage) {
            final ILineCoverage[] linecovs = ((LineCoverage) parent).lineCoverage;
            for (final ILineCoverage cov : linecovs) {
                this.childrenToParents.put(cov, parent);
            }
            return linecovs;
        }
        
        if (parent instanceof IClassCoverage) {
            final IMethodCoverage[] mthcovs = ((IClassCoverage) parent).getMethodCoverage();
            for (final IMethodCoverage cov : mthcovs) {
                this.childrenToParents.put(cov, parent);
            }
            return mthcovs;
        }
        
        return new Object[0];
    }
    
    /**
     * the file coverages
     */
    protected static final class FileCoverage {
        /**
         * @param fileCoverage
         */
        public FileCoverage(final IFileCoverage[] fileCoverage) {
            this.fileCoverage = fileCoverage;
        }
        
        /**
         * The file coverage
         */
        public IFileCoverage[] fileCoverage;
        
        /**
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + Arrays.hashCode(this.fileCoverage);
            return result;
        }
        
        /**
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            final FileCoverage other = (FileCoverage) obj;
            if (!Arrays.equals(this.fileCoverage, other.fileCoverage)) {
                return false;
            }
            return true;
        }
    }
    
    /**
     * the class coverages
     */
    protected static final class ClassCoverage {
        /**
         * @param classCoverage
         */
        public ClassCoverage(final IClassCoverage[] classCoverage) {
            this.classCoverage = classCoverage;
        }
        
        /**
         * The class coverage
         */
        public IClassCoverage[] classCoverage;
        
        /**
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + Arrays.hashCode(this.classCoverage);
            return result;
        }
        
        /**
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            final ClassCoverage other = (ClassCoverage) obj;
            if (!Arrays.equals(this.classCoverage, other.classCoverage)) {
                return false;
            }
            return true;
        }
    }
    
    /**
     * the class coverages
     */
    protected static final class LineCoverage {
        /**
         * @param lineCoverage
         */
        public LineCoverage(final ILineCoverage[] lineCoverage) {
            this.lineCoverage = lineCoverage;
        }
        
        /**
         * The line coverage
         */
        public ILineCoverage[] lineCoverage;
        
        /**
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + Arrays.hashCode(this.lineCoverage);
            return result;
        }
        
        /**
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            final LineCoverage other = (LineCoverage) obj;
            if (!Arrays.equals(this.lineCoverage, other.lineCoverage)) {
                return false;
            }
            return true;
        }
    }
    
    /**
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getElements(java.lang.Object)
     */
    @Override
    public Object[] getElements(final Object parent) {
        return this.getChildren(parent);
    }
    
    /**
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     */
    @Override
    public Object getParent(final Object child) {
        return this.childrenToParents.get(child);
    }
    
    /**
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     */
    @Override
    public boolean hasChildren(final Object parent) {
        return this.getChildren(parent).length > 0;
    }
    
}
