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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.phpmaven.eclipse.core.mvn.ITestExecutionTooling.ITestResults;
import org.phpmaven.eclipse.core.phpunit.ITestCase;
import org.phpmaven.eclipse.core.phpunit.ITestError;
import org.phpmaven.eclipse.core.phpunit.ITestSuite;

/**
 * @author Martin Eisengardt <mep_eisen@web.de>
 * 
 */
public class TestsTreeContentProvider implements ITreeContentProvider {
    
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
            final ITestSuite[] testSuites = ((ITestResults) parent).getTestSuites();
            for (final ITestSuite suite : testSuites) {
                this.childrenToParents.put(suite, parent);
            }
            return testSuites;
        }
        
        if (parent instanceof ITestSuite) {
            final List<Object> result = new ArrayList<Object>();
            for (final ITestCase test : ((ITestSuite) parent).getTestCases()) {
                this.childrenToParents.put(test, parent);
                result.add(test);
            }
            for (final ITestSuite suite : ((ITestSuite) parent).getSubSuites()) {
                this.childrenToParents.put(suite, parent);
                result.add(suite);
            }
            return result.toArray(new Object[result.size()]);
        }
        
        if (parent instanceof ITestCase) {
            final ITestError[] errors = ((ITestCase) parent).getErrors();
            for (final ITestError error : errors) {
                this.childrenToParents.put(error, parent);
            }
            return errors;
        }
        
        return new Object[0];
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
