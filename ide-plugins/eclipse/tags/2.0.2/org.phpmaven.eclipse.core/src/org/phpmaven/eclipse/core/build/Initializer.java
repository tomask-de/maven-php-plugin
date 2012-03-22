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

package org.phpmaven.eclipse.core.build;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.BuildpathContainerInitializer;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathContainer;
import org.eclipse.dltk.core.IScriptProject;
import org.phpmaven.eclipse.core.MavenPhpUtils;
import org.phpmaven.eclipse.core.PhpmavenCorePlugin;

/**
 * The buildpath container initializer for maven inclusions.
 * 
 * @author Martin Eisengardt
 */
public class Initializer extends BuildpathContainerInitializer {
    
    /**
     * Constructor.
     */
    public Initializer() {
    }
    
    /**
     * @see org.eclipse.dltk.core.BuildpathContainerInitializer#initialize(org.eclipse.core.runtime.IPath,
     *      org.eclipse.dltk.core.IScriptProject)
     */
    @Override
    public void initialize(final IPath containerPath, final IScriptProject scriptProject) throws CoreException {
        if (containerPath.segmentCount() > 0 && containerPath.segment(0).equals(PhpmavenCorePlugin.BUILDPATH_CONTAINER_ID)) {
            try {
                if (MavenPhpUtils.isPHPProject(scriptProject) && MavenPhpUtils.isMavenProject(scriptProject)) {
                    // sets the buildpath container (may result in an empty
                    // buildpath until the maven update request is finished)
                    DLTKCore.setBuildpathContainer(containerPath, new IScriptProject[] { scriptProject }, new IBuildpathContainer[] { new Container(containerPath, scriptProject) }, null);
                }
            } catch (final Exception e) {
                PhpmavenCorePlugin.logError("Exception while initializing buildpath container", e); //$NON-NLS-1$
            }
        }
    }
    
}
