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

package org.phpmaven.eclipse.ui.menus;

import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.phpmaven.eclipse.core.MavenPhpUtils;
import org.phpmaven.eclipse.ui.PhpmavenUiPlugin;

/**
 * adds the php-maven nature
 * 
 * @author Martin Eisengardt
 */
public class AddNatureAction implements IObjectActionDelegate {
    
    /** the selection */
    private ISelection selection;
    
    /**
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    @Override
    public void run(final IAction action) {
        if (this.selection instanceof IStructuredSelection) {
            for (@SuppressWarnings("unchecked")
            final Iterator<Object> it = ((IStructuredSelection) this.selection).iterator(); it.hasNext();) {
                final Object element = it.next();
                IProject project = null;
                if (element instanceof IProject) {
                    project = (IProject) element;
                } else if (element instanceof IAdaptable) {
                    project = (IProject) ((IAdaptable) element).getAdapter(IProject.class);
                }
                if (project != null) {
                    this.toggleNature(project);
                }
            }
        }
    }
    
    /**
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
     *      org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public void selectionChanged(final IAction action, final ISelection sel) {
        this.selection = sel;
    }
    
    /**
     * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction,
     *      org.eclipse.ui.IWorkbenchPart)
     */
    @Override
    public void setActivePart(final IAction action, final IWorkbenchPart targetPart) {
        // empty
    }
    
    /**
     * Toggles sample nature on a project
     * 
     * @param project
     *            to have sample nature added or removed
     */
    private void toggleNature(final IProject project) {
        try {
            if (MavenPhpUtils.isPhpmavenProject(project)) {
                // this is already a php-maven project
                final MessageBox box = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.ICON_ERROR | SWT.ABORT);
                box.setText(Messages.AddNatureAction_ErrorTitle);
                box.setMessage(Messages.AddNatureAction_AlreadyPhpMavenProject);
                box.open();
                return;
            }
            
            // check for pom.xml.
            if (MavenPhpUtils.hasPomXml(project)) {
                final IMavenProjectFacade facade = MavenPhpUtils.fetchProjectFacade(project);
                if (!"php".equals(facade.getMavenProject().getPackaging())) { //$NON-NLS-1$
                    final MessageBox box = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.ICON_ERROR | SWT.ABORT);
                    box.setText(Messages.AddNatureAction_ErrorTitle);
                    box.setMessage(Messages.AddNatureAction_InvalidPackaging);
                    box.open();
                    return;
                }
            }
            else {
                // create it
                MavenPhpUtils.createPhpmavenPomXml(project, "org.mydomain.sample", project.getName(), "0.0.1-SNAPSHOT"); //$NON-NLS-1$ //$NON-NLS-2$
            }

            // add maven nature
            if (!MavenPhpUtils.isMavenProject(project)) {
                // no maven project, so we have a pom.xml?
                MavenPhpUtils.addMavenNature(project);
            }
            
            // add php nature
            if (!MavenPhpUtils.isPhpmavenProject(project)) {
                // no php project.
                MavenPhpUtils.addPhpNature(project);
            }
            
            // add phpmaven nature
            MavenPhpUtils.addPhpMavenNature(project);
        } catch (final CoreException e) {
            PhpmavenUiPlugin.logError("Error while toggle PHP-Maven nature", e); //$NON-NLS-1$
        }
    }
    
}
