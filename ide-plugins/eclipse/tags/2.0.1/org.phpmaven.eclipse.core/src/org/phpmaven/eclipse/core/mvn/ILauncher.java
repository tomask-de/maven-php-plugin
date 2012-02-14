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
package org.phpmaven.eclipse.core.mvn;

import java.io.File;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;

/**
 * The tooling launcher to execute php directly
 * 
 * @author Martin Eisengardt
 */
public interface ILauncher {
    
    /**
     * Gets the runtime path
     * 
     * @return runtime path
     */
    File getRuntimePath();
    
    /**
     * Gets the command line arguments
     * 
     * @return command line arguments
     */
    String[] getCmdLine();
    
    /**
     * Gets the php exe to be used
     * 
     * @return php exe
     */
    String getPhpExe();
    
    /**
     * Gets the php script
     * 
     * @return php script
     */
    File getPhpScript();
    
    /**
     * Sets the runtime path
     * 
     * @param runtimePath
     *            runtime path
     */
    void setRuntimePath(File runtimePath);
    
    /**
     * Sets the runtime path
     * 
     * @param runtimePath
     *            runtime path
     */
    void setRuntimePath(IContainer runtimePath);
    
    /**
     * Sets the command line arguments
     * 
     * @param cmdLine
     *            command line arguments
     */
    void setCmdLine(String[] cmdLine);
    
    /**
     * Appends a command line argument
     * 
     * @param arg
     *            command line argument
     */
    void appendCmdLineArg(String arg);
    
    /**
     * Clears the command line arguments
     */
    void clearCmdLineArgs();
    
    /**
     * Sets the php exe item to be used
     * 
     * @param exe
     *            php exe item
     */
    void setPhpExe(String exe);
    
    /**
     * Sets the php script
     * 
     * @param phpScript
     *            php script
     */
    void setPhpScript(File phpScript);
    
    /**
     * Sets the php script
     * 
     * @param phpScript
     *            php script
     */
    void setPhpScript(IFile phpScript);
    
    /**
     * Launches the script and returns the stdout output
     * 
     * @return stdout output
     */
    String launch();
    
    /**
     * Returns the std out. This is identical to the results of the launch
     * method. It may be used to query the results later on.
     * 
     * @return std out
     */
    String getStdout();
    
}
