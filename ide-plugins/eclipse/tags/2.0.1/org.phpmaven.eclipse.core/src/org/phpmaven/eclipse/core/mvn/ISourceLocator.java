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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.dltk.core.IScriptProject;

/**
 * A tooling that support locating sources
 * 
 * @author Martin Eisengardt
 */
public interface ISourceLocator {
    
    /**
     * returns true if this test tooling is responsible to locate source files
     * within given project
     * 
     * @param project
     *            project
     * @param scriptProject
     *            script project
     * @return true if this tooling should be used for given project
     */
    boolean isResponsibleForProject(IProject project, IScriptProject scriptProject);
    
    /**
     * Locates the runtime file for given source file
     * 
     * @param sourceFile
     *            the source file
     * @param project
     *            project
     * @param scriptProject
     *            script project
     * @return runtime file
     */
    IFile locateRuntimeFile(IFile sourceFile, IProject project, IScriptProject scriptProject);
    
    /**
     * Locates the source file for given runtime file
     * 
     * @param runtimeFile
     *            the runtime file
     * @param project
     *            project
     * @param scriptProject
     *            script project
     * @return runtime file
     */
    IFile locateSourceFile(IFile runtimeFile, IProject project, IScriptProject scriptProject);
    
    /**
     * Locates the test runtime file for given source file
     * 
     * @param sourceFile
     *            the source file
     * @param project
     *            project
     * @param scriptProject
     *            script project
     * @return test runtime file
     */
    IFile locateTestRuntimeFile(IFile sourceFile, IProject project, IScriptProject scriptProject);
    
    /**
     * Locates the test source file for given runtime file
     * 
     * @param runtimeFile
     *            the runtime file
     * @param project
     *            project
     * @param scriptProject
     *            script project
     * @return runtime file
     */
    IFile locateTestSourceFile(IFile runtimeFile, IProject project, IScriptProject scriptProject);
    
    /**
     * Locates the runtime folder for given source folder
     * 
     * @param sourceFolder
     *            the source folder
     * @param project
     *            project
     * @param scriptProject
     *            script project
     * @return runtime folder
     */
    IFolder locateRuntimeFolder(IFolder sourceFolder, IProject project, IScriptProject scriptProject);
    
    /**
     * Locates the source file for given runtime folder
     * 
     * @param runtimeFolder
     *            the runtime folder
     * @param project
     *            project
     * @param scriptProject
     *            script project
     * @return runtime folder
     */
    IFolder locateSourceFolder(IFolder runtimeFolder, IProject project, IScriptProject scriptProject);
    
    /**
     * Locates the test runtime file for given source folder
     * 
     * @param sourceFolder
     *            the source folder
     * @param project
     *            project
     * @param scriptProject
     *            script project
     * @return test runtime folder
     */
    IFolder locateTestRuntimeFolder(IFolder sourceFolder, IProject project, IScriptProject scriptProject);
    
    /**
     * Locates the test source file for given runtime folder
     * 
     * @param runtimeFolder
     *            the runtime folder
     * @param project
     *            project
     * @param scriptProject
     *            script project
     * @return runtime folder
     */
    IFolder locateTestSourceFolder(IFolder runtimeFolder, IProject project, IScriptProject scriptProject);
    
}
