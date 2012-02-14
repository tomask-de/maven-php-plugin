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

import java.io.File;

import org.eclipse.core.resources.IProject;

/**
 * The phpunit tooling
 * 
 * @author Martin Eisengardt
 */
public interface IPhpUnit {
    
    /**
     * Returns the version number
     * 
     * @return version number
     */
    String getVersionNumber();
    
    /**
     * Returns the cli file that is able to execute tests
     * 
     * @return cli file
     */
    File getCliFile();
    
    /**
     * Parses the coverage info from output
     * 
     * @param output
     *            output
     * @param type
     *            the coverage info type (f.e. "clover")
     * @param project
     *            project
     * @return coverage info
     */
    ICoverageInfo parseCoverageInfo(String output, String type, IProject project);
    
    /**
     * Parses the testsuites results from output
     * 
     * @param output
     *            output
     * @param type
     *            the result type (f.e. "xml")
     * @param project
     *            project
     * @return test suites
     */
    ITestSuite[] parseTestResults(String output, String type, IProject project);
    
}
