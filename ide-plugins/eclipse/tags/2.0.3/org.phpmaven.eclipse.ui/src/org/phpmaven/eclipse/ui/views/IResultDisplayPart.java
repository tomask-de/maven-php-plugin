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

import org.phpmaven.eclipse.core.mvn.ITestExecutionTooling.ITestResults;

/**
 * @author Martin Eisengardt <mep_eisen@web.de>
 * 
 */
public interface IResultDisplayPart {
    
    /**
     * Sets the command line and sets the state to running
     * 
     * @param commandline
     *            command line
     */
    void setCommandLine(String commandline);
    
    /**
     * Sets the failure info (including stdout report)
     * 
     * @param stdout
     *            std out
     */
    void setFailure(String stdout);
    
    /**
     * Sets the results
     * 
     * @param stdout
     *            stdout
     * @param results
     *            results
     */
    void setResults(String stdout, ITestResults results);
    
}
