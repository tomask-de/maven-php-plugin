/**
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
package org.phpmaven.eclipse.core.internal.mvn;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.phpmaven.eclipse.core.MavenPhpUtils;
import org.phpmaven.eclipse.core.mvn.ISourceLocator;

/**
 * The source locator specialized in flow3 maven packages
 * 
 * @author Martin Eisengardt
 */
public class PhpmavenLocator implements ISourceLocator {
    
    /**
     * @see org.phpmaven.eclipse.core.mvn.ISourceLocator#isResponsibleForProject(org.eclipse.core.resources.IProject,
     *      org.eclipse.dltk.core.IScriptProject)
     */
    @Override
    public boolean isResponsibleForProject(final IProject project, final IScriptProject scriptProject) {
        final IMavenProjectFacade mvnFacade = MavenPhpUtils.fetchProjectFacade(project);
        return MavenPhpUtils.isMavenProject(project) && MavenPhpUtils.isPHPProject(project) && MavenPhpUtils.isPhpmavenProject(project) && "php".equals(mvnFacade.getPackaging()); //$NON-NLS-1$
    }
    
    /**
     * @see org.phpmaven.eclipse.core.mvn.ISourceLocator#locateRuntimeFile(org.eclipse.core.resources.IFile,
     *      org.eclipse.core.resources.IProject,
     *      org.eclipse.dltk.core.IScriptProject)
     */
    @Override
    public IFile locateRuntimeFile(final IFile sourceFile, final IProject project, final IScriptProject scriptProject) {
        final IMavenProjectFacade mvnFacade = MavenPhpUtils.fetchProjectFacade(project);
        
        // src/main/php maps to target/classes
        // src/main/test maps to target/test-classes
        final IPath[] srcPaths = MavenPhpUtils.getCompileSourceLocations(project, mvnFacade);
        for (final IPath srcPath : srcPaths) {
            if (srcPath.isPrefixOf(sourceFile.getProjectRelativePath())) {
                final IPath targetPath = mvnFacade.getOutputLocation().append(sourceFile.getProjectRelativePath().removeFirstSegments(srcPath.segmentCount()));
                return project.getFile(targetPath);
            }
        }
        
        final IPath[] srcTestPaths = MavenPhpUtils.getTestCompileSourceLocations(project, mvnFacade);
        for (final IPath srcTestPath : srcTestPaths) {
            if (srcTestPath.isPrefixOf(sourceFile.getProjectRelativePath())) {
                final IPath targetPath = mvnFacade.getOutputLocation().append(sourceFile.getProjectRelativePath().removeFirstSegments(srcTestPath.segmentCount()));
                return project.getFile(targetPath);
            }
        }
        
        // no match
        return sourceFile;
    }
    
    /**
     * @see org.phpmaven.eclipse.core.mvn.ISourceLocator#locateSourceFile(org.eclipse.core.resources.IFile,
     *      org.eclipse.core.resources.IProject,
     *      org.eclipse.dltk.core.IScriptProject)
     */
    @Override
    public IFile locateSourceFile(final IFile runtimeFile, final IProject project, final IScriptProject scriptProject) {
        final IMavenProjectFacade mvnFacade = MavenPhpUtils.fetchProjectFacade(project);
        
        // src/main/php maps to target/classes
        // src/main/test maps to target/test-classes
        IPath targetPath = mvnFacade.getOutputLocation();
        if (targetPath.isPrefixOf(runtimeFile.getProjectRelativePath())) {
            for (final IPath srcPath : MavenPhpUtils.getCompileSourceLocations(project, mvnFacade)) {
                final IPath resultPath = srcPath.append(runtimeFile.getProjectRelativePath().removeFirstSegments(targetPath.segmentCount()));
                final IFile result = project.getFile(resultPath);
                if (result.exists()) {
                    return result;
                }
            }
        }
        targetPath = mvnFacade.getTestOutputLocation();
        if (targetPath.isPrefixOf(runtimeFile.getProjectRelativePath())) {
            for (final IPath srcPath : MavenPhpUtils.getTestCompileSourceLocations(project, mvnFacade)) {
                final IPath resultPath = srcPath.append(runtimeFile.getProjectRelativePath().removeFirstSegments(targetPath.segmentCount()));
                final IFile result = project.getFile(resultPath);
                if (result.exists()) {
                    return result;
                }
            }
        }
        
        // no match
        return runtimeFile;
    }
    
    /**
     * @see org.phpmaven.eclipse.core.mvn.ISourceLocator#locateTestRuntimeFile(org.eclipse.core.resources.IFile,
     *      org.eclipse.core.resources.IProject,
     *      org.eclipse.dltk.core.IScriptProject)
     */
    @Override
    public IFile locateTestRuntimeFile(final IFile sourceFile, final IProject project, final IScriptProject scriptProject) {
        return this.locateRuntimeFile(sourceFile, project, scriptProject);
    }
    
    /**
     * @see org.phpmaven.eclipse.core.mvn.ISourceLocator#locateTestSourceFile(org.eclipse.core.resources.IFile,
     *      org.eclipse.core.resources.IProject,
     *      org.eclipse.dltk.core.IScriptProject)
     */
    @Override
    public IFile locateTestSourceFile(final IFile runtimeFile, final IProject project, final IScriptProject scriptProject) {
        return this.locateSourceFile(runtimeFile, project, scriptProject);
    }

    @Override
    public IFolder locateRuntimeFolder(IFolder sourceFolder, IProject project, IScriptProject scriptProject) {
        final IMavenProjectFacade mvnFacade = MavenPhpUtils.fetchProjectFacade(project);
        
        // src/main/php maps to target/classes
        // src/main/test maps to target/test-classes
        final IPath[] srcPaths = MavenPhpUtils.getCompileSourceLocations(project, mvnFacade);
        for (final IPath srcPath : srcPaths) {
            if (srcPath.isPrefixOf(sourceFolder.getProjectRelativePath())) {
                final IPath targetPath = mvnFacade.getOutputLocation().append(sourceFolder.getProjectRelativePath().removeFirstSegments(srcPath.segmentCount()));
                return project.getFolder(targetPath);
            }
        }
        
        final IPath[] srcTestPaths = MavenPhpUtils.getTestCompileSourceLocations(project, mvnFacade);
        for (final IPath srcTestPath : srcTestPaths) {
            if (srcTestPath.isPrefixOf(sourceFolder.getProjectRelativePath())) {
                final IPath targetPath = mvnFacade.getOutputLocation().append(sourceFolder.getProjectRelativePath().removeFirstSegments(srcTestPath.segmentCount()));
                return project.getFolder(targetPath);
            }
        }
        
        // no match
        return sourceFolder;
    }

    @Override
    public IFolder locateSourceFolder(IFolder runtimeFolder, IProject project, IScriptProject scriptProject) {
        final IMavenProjectFacade mvnFacade = MavenPhpUtils.fetchProjectFacade(project);
        
        // src/main/php maps to target/classes
        // src/main/test maps to target/test-classes
        IPath targetPath = mvnFacade.getOutputLocation();
        if (targetPath.isPrefixOf(runtimeFolder.getProjectRelativePath())) {
            for (final IPath srcPath : MavenPhpUtils.getCompileSourceLocations(project, mvnFacade)) {
                final IPath resultPath = srcPath.append(runtimeFolder.getProjectRelativePath().removeFirstSegments(targetPath.segmentCount()));
                final IFolder result = project.getFolder(resultPath);
                if (result.exists()) {
                    return result;
                }
            }
        }
        targetPath = mvnFacade.getTestOutputLocation();
        if (targetPath.isPrefixOf(runtimeFolder.getProjectRelativePath())) {
            for (final IPath srcPath : MavenPhpUtils.getTestCompileSourceLocations(project, mvnFacade)) {
                final IPath resultPath = srcPath.append(runtimeFolder.getProjectRelativePath().removeFirstSegments(targetPath.segmentCount()));
                final IFolder result = project.getFolder(resultPath);
                if (result.exists()) {
                    return result;
                }
            }
        }
        
        // no match
        return runtimeFolder;
    }

    @Override
    public IFolder locateTestRuntimeFolder(IFolder sourceFolder, IProject project, IScriptProject scriptProject) {
        return this.locateRuntimeFolder(sourceFolder, project, scriptProject);
    }

    @Override
    public IFolder locateTestSourceFolder(IFolder runtimeFolder, IProject project, IScriptProject scriptProject) {
        return this.locateSourceFolder(runtimeFolder, project, scriptProject);
    }
    
}
