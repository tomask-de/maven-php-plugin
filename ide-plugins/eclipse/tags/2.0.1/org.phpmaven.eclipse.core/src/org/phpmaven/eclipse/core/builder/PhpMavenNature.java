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

package org.phpmaven.eclipse.core.builder;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.phpmaven.eclipse.core.PhpmavenCorePlugin;

/**
 * The php maven nature
 * 
 * @author Martin Eisengardt
 */
public class PhpMavenNature implements IProjectNature {
    
    /** The project */
    private IProject project;
    
    /**
     * @see org.eclipse.core.resources.IProjectNature#configure()
     */
    @Override
    public void configure() throws CoreException {
        final IProjectDescription desc = this.project.getDescription();
        final ICommand[] commands = desc.getBuildSpec();
        
        for (int i = 0; i < commands.length; ++i) {
            if (commands[i].getBuilderName().equals(PhpmavenCorePlugin.PHPMAVEN_BUILDER_ID)) {
                return;
            }
        }
        
        final ICommand[] newCommands = new ICommand[commands.length + 1];
        System.arraycopy(commands, 0, newCommands, 0, commands.length);
        final ICommand command = desc.newCommand();
        command.setBuilderName(PhpmavenCorePlugin.PHPMAVEN_BUILDER_ID);
        newCommands[newCommands.length - 1] = command;
        desc.setBuildSpec(newCommands);
        this.project.setDescription(desc, null);
    }
    
    /**
     * @see org.eclipse.core.resources.IProjectNature#deconfigure()
     */
    @Override
    public void deconfigure() throws CoreException {
        final IProjectDescription description = this.getProject().getDescription();
        final ICommand[] commands = description.getBuildSpec();
        for (int i = 0; i < commands.length; ++i) {
            if (commands[i].getBuilderName().equals(PhpmavenCorePlugin.PHPMAVEN_BUILDER_ID)) {
                final ICommand[] newCommands = new ICommand[commands.length - 1];
                System.arraycopy(commands, 0, newCommands, 0, i);
                System.arraycopy(commands, i + 1, newCommands, i, commands.length - i - 1);
                description.setBuildSpec(newCommands);
                this.project.setDescription(description, null);
                return;
            }
        }
    }
    
    /**
     * @see org.eclipse.core.resources.IProjectNature#getProject()
     */
    @Override
    public IProject getProject() {
        return this.project;
    }
    
    /**
     * @see org.eclipse.core.resources.IProjectNature#setProject(org.eclipse.core.resources.IProject)
     */
    @Override
    public void setProject(final IProject project) {
        this.project = project;
    }
    
}
