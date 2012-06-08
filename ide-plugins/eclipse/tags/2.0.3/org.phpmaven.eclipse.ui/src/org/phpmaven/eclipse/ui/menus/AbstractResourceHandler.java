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
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IOpenable;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.handlers.HandlerUtil;
import org.phpmaven.eclipse.core.MavenPhpUtils;
import org.phpmaven.eclipse.ui.PhpmavenUiPlugin;

/**
 * Menu action to invoke phpmaven commands on selected resource.
 * 
 * @author Martin Eisengardt
 */
abstract class AbstractResourceHandler extends AbstractHandler implements IWorkbenchWindowActionDelegate {
    
    /** the workbench window */
    private IWorkbenchWindow window;
    
    /** the selected resources */
    private IResource[] selectedResources;
    
    @Override
    public void dispose() {
        this.selectedResources = null;
    }
    
    @Override
    public void init(final IWorkbenchWindow win) {
        this.selectedResources = new IResource[0];
        this.window = win;
    }
    
    @Override
    public void selectionChanged(final IAction action, final ISelection selection) {
        List<IResource> resources = new ArrayList<IResource>(1);
        if (selection.isEmpty()) {
            this.addActiveEditorFileToList(resources);
        } else if (selection instanceof ITextSelection) {
            this.addActiveEditorFileToList(resources);
        } else if (selection instanceof IStructuredSelection) {
            final IStructuredSelection structuredSelection = (IStructuredSelection) selection;
            resources = new ArrayList<IResource>(structuredSelection.size());
            final Iterator<?> iterator = structuredSelection.iterator();
            while (iterator.hasNext()) {
                final Object entry = iterator.next();
                addObjectToList(resources, entry);
            }
        } else {
            this.addActiveEditorFileToList(resources);
        }
        
        this.selectedResources = normalizeResources(resources).toArray(new IResource[0]);
    }

    /**
     * Adds an object to given resource list.
     * @param resources
     * @param entry
     */
    private void addObjectToList(List<IResource> resources, final Object entry) {
        try {
            if (entry instanceof IPath) {
                final IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember((IPath) entry);
                if (resource != null) {
                    addObjectToList(resources, resource);
                }
            }
            else if (entry instanceof IResource) {
                addResourceToList(resources, (IResource) entry);
            } else if (entry instanceof ISourceModule) {
                if (((ISourceModule) entry).exists()) {
                    final IFile file = (IFile) ((ISourceModule) entry).getCorrespondingResource();
                    if (MavenPhpUtils.isPhpFile(file)) {
                        addResourceToList(resources, file);
                    }
                }
            } else if (entry instanceof IOpenable) {
                if (((IOpenable) entry).exists()) {
                    addResourceToList(resources, ((IOpenable) entry).getCorrespondingResource());
                }
            } else if (entry instanceof IMember) {
                if (((IMember) entry).exists()) {
                    addResourceToList(resources, ((IMember) entry).getResource());
                }
            } else if (entry instanceof IFileEditorInput) {
                if (((IFileEditorInput) entry).exists()) {
                    addResourceToList(resources, ((IFileEditorInput) entry).getFile());
                }
            } else if (entry instanceof IScriptFolder) {
                if (((IScriptFolder) entry).exists()) {
                    addResourceToList(resources, ((IScriptFolder) entry).getResource());
                }
            }
        } catch (final ModelException e) {
            PhpmavenUiPlugin.logError("Error on selection changed", e); //$NON-NLS-1$
        }
    }
    
    /**
     * Sets the selected resources
     * 
     * @param resources
     *            resources
     */
    public void setSelectedResources(final IResource[] resources) {
        this.selectedResources = resources;
    }
    
    /**
     * Returns the selected resources
     * 
     * @return selected resources
     */
    public IResource[] getSelectedResources() {
        return this.selectedResources;
    }
    
    /**
     * Adds resource to list
     * 
     * @param list
     * @param resource
     */
    private void addResourceToList(final List<IResource> list, final IResource resource) {
        if (resource != null && resource.exists() && !list.contains(resource) && canAddResource(list, resource)) {
            list.add(resource);
        }
    }
    
    /**
     * returns true if given resource can be added.
     * @param list
     * @param resource
     * @return boolean
     */
    protected boolean canAddResource(final List<IResource> list, final IResource resource) {
        return true;
    }
    
    /**
     * normalizes the resources; removes unneeded input.
     * @param in in list
     * @return out list.
     */
    protected List<IResource> normalizeResources(final List<IResource> in) {
        return in;
    }
    
    /**
     * Adds active editor file to list
     * 
     * @param list
     */
    protected void addActiveEditorFileToList(final List<IResource> list) {
        if (this.window != null) {
            final IWorkbenchPage page = this.window.getActivePage();
            if (page != null) {
                final IEditorPart editor = page.getActiveEditor();
                if (editor != null) {
                    final IEditorInput input = editor.getEditorInput();
                    if (input != null && input instanceof IFileEditorInput) {
                        addResourceToList(list, ((IFileEditorInput) input).getFile());
                    }
                }
            }
        }
    }
    
    /**
     * The mutex scheduling rule for the jobs.
     */
    protected final ISchedulingRule rule = createRule();

    /**
     * Creates the rule.
     * @return rule
     */
    protected ISchedulingRule createRule() {
        return new MutexRule() {

            @Override
            public boolean contains(ISchedulingRule schedRule) {
                if (schedRule instanceof IProject) {
                    return true;
                }
                return super.contains(schedRule);
            }
            
        };
    }
    
    @Override
    public void run(final IAction action) {
        final IResource[] resources = this.getSelectedResources();
        if (resources.length > 0) {
            final Job job = createJob(this.selectedResources.clone());
            if (job.getRule() == null) {
                job.setRule(this.rule);
            }
            job.setUser(true);
            job.schedule();
        }
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        final IWorkbenchWindow w = HandlerUtil.getActiveWorkbenchWindowChecked(event);
        final ContainerSelectionDialog dialog = new ContainerSelectionDialog(w.getShell(), null, false, Messages.LifecycleTestHandler_SelectProjectToTest);
        dialog.create();
        if (dialog.open() != Window.CANCEL) {
            final Object[] result = dialog.getResult();
            final List<IResource> list = new ArrayList<IResource>();
            for (final Object obj : result) {
                addObjectToList(list, obj);
            }
            final IResource[] resources = normalizeResources(list).toArray(new IResource[0]);
            if (resources.length > 0) {
                final Job job = createJob(resources);
                if (job.getRule() == null) {
                    job.setRule(this.rule);
                }
                job.setUser(true);
                job.schedule();
            }
        }
        return null;
    }

    /**
     * Creates the job to be executed.
     * @param resources
     * @return job to be executed.
     */
    protected abstract Job createJob(IResource[] resources);
    
}
