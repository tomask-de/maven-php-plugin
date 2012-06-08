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
// mainly taken from pti

package org.phpmaven.eclipse.ui.menus;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

/**
 * Menu action to invoke phpmaven commands on selected resource.
 * 
 * @author Martin Eisengardt
 */
abstract class AbstractProjectHandler extends AbstractResourceHandler {
    
    @Override
    protected ISchedulingRule createRule() {
        return ResourcesPlugin.getWorkspace().getRoot();
    }

    @Override
    protected List<IResource> normalizeResources(final List<IResource> in) {
        final List<IResource> out = new ArrayList<IResource>();
        for (final IResource i : in) {
            final IProject prj = i.getProject();
            if (!out.contains(prj)) {
                out.add(prj);
            }
        }
        return out;
    }
    
}
