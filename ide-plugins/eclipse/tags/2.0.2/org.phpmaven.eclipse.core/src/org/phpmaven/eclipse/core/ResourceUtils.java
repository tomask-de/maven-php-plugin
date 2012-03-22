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
package org.phpmaven.eclipse.core;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * Helper class for resource manipulation
 * 
 * @author Martin Eisengardt
 */
public class ResourceUtils {
    
    /**
     * Makes all directories
     * 
     * @param folder
     *            the folder to be created
     * @throws CoreException
     *             thrown upon creation error
     */
    public static void mkdirs(final IFolder folder) throws CoreException {
        if (folder.exists()) {
            return;
        }
        final IContainer parent = folder.getParent();
        if (parent.getType() == IResource.FOLDER) {
            ResourceUtils.mkdirs((IFolder) folder.getParent());
        }
        folder.create(true, true, new NullProgressMonitor());
    }
    
    /**
     * Copies the folder content from source to target
     * 
     * @param src
     *            sources
     * @param target
     *            target
     * @throws CoreException
     *             thrown on errors
     */
    public static void copy(final IFolder src, final IFolder target) throws CoreException {
        if (src.exists()) {
            src.accept(new CopyResourceVisitor(src, target));
        }
    }
    
    /**
     * Copies the file content from source to target
     * 
     * @param src
     *            sources
     * @param target
     *            target
     * @throws CoreException
     *             thrown on errors
     */
    public static void copy(final IFile src, final IFile target) throws CoreException {
        if (src.exists()) {
            ResourceUtils.mkdirs((IFolder) target.getParent());
            if (target.exists()) {
                target.setContents(src.getContents(true), true, false, new NullProgressMonitor());
            } else {
                src.copy(target.getFullPath(), true, new NullProgressMonitor());
            }
        }
    }
    
    /**
     * Copies the file content from multiple sources to target
     * 
     * @param src
     *            sources
     * @param target
     *            target
     * @throws CoreException
     *             thrown on errors
     */
    public static void copy(final IFolder[] src, final IFolder target) throws CoreException {
        for (final IFolder s : src) {
            ResourceUtils.copy(s, target);
        }
    }
    
    /**
     * Copies the file content from multiple sources to target
     * 
     * @param src
     *            sources (need to be absolute paths resolvable from workspace
     *            root)
     * @param target
     *            target
     * @throws CoreException
     *             thrown on errors
     */
    public static void copy(final IPath[] src, final IFolder target) throws CoreException {
        for (final IPath s : src) {
            final IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getFolder(s);
            ResourceUtils.copy(folder, target);
        }
    }
    
    /**
     * Copies the resources from src to target
     */
    static class CopyResourceVisitor implements IResourceVisitor {
        
        /**
         * The source folder
         */
        private final IFolder src;
        
        /**
         * The target folder
         */
        private final IFolder target;
        
        /**
         * Copies the resources from src to target
         * 
         * @param src
         *            source
         * @param target
         *            target
         */
        public CopyResourceVisitor(final IFolder src, final IFolder target) {
            this.src = src;
            this.target = target;
        }
        
        @Override
        public boolean visit(final IResource resource) throws CoreException {
            if (resource.getType() == IResource.FILE) {
                final IPath relPath = resource.getFullPath().removeFirstSegments(this.src.getFullPath().segmentCount());
                final IFile targetFile = this.target.getFile(relPath);
                ResourceUtils.copy((IFile) resource, targetFile);
            }
            // return true to continue visiting children.
            return true;
        }
    }
    
}
