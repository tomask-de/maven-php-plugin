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

package org.phpmaven.eclipse.ui.prj;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.wizards.NewElementWizard;
import org.eclipse.php.internal.ui.util.PHPPluginImages;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.phpmaven.eclipse.core.archetype.IArchetype;
import org.phpmaven.eclipse.core.archetype.ProjectCreationRequest;

/**
 * New project wizard.
 * 
 * @author Martin Eisengardt
 */
@SuppressWarnings("restriction")
public class NewProjectWizard extends NewElementWizard implements IExecutableExtension {
    
    /** model property for the selected project */
    public static final String SELECTED_PROJECT = "SelectedProject"; //$NON-NLS-1$
    
    /** the wizard id */
    public static final String WIZARD_ID = "org.phpmaven.eclipse.ui.newProjectWizard"; //$NON-NLS-1$

    /** the first wizard page. */
    protected ProjWizArchetypePage fFirstPage;

    /** the second wizard page. */
    protected ProjWizMavenIdsPage fSecondPage;

    /** the third wizard page. */
    private ProjWizProjectPage fThirdPage;
    
    /** the last page. */
    protected IPrjWizardPage fLastPage;

    /** configuration element */
    protected IConfigurationElement fConfigElement;

    /** index of the last page */
    protected int fLastPageIndex = -1;

    /**
     * Constructor
     */
    public NewProjectWizard() {
        setDefaultPageImageDescriptor(PHPPluginImages.DESC_WIZBAN_ADD_PHP_PROJECT);
        setDialogSettings(DLTKUIPlugin.getDefault().getDialogSettings());
        setWindowTitle(Messages.NewProjectWizard_Title);
    }

    @Override
    public void addPages() {
        super.addPages();

        // First page (archetype selection)
        this.fFirstPage = new ProjWizArchetypePage();
        addPage(this.fFirstPage);

        // Second page (Project template)
        this.fSecondPage = new ProjWizMavenIdsPage();
        addPage(this.fSecondPage);

        // Third page (Project details)
        this.fThirdPage = new ProjWizProjectPage(this.fSecondPage);
        addPage(this.fThirdPage);

        this.fLastPage = this.fThirdPage;
    }

    @Override
    protected void finishPage(IProgressMonitor monitor)
            throws InterruptedException, CoreException {
        // fetch selected data
        final IArchetype archetype = this.fFirstPage.getSelectedArchetype();
        final String groupId = this.fSecondPage.getGroupId();
        final String artifactId = this.fSecondPage.getArtifactId();
        final String version = this.fSecondPage.getVersion();
        final String prjPomName = this.fSecondPage.getProjectName();
        final String prjPomDesc = this.fSecondPage.getProjectDescription();
        final String parentGroupId = this.fSecondPage.getParentGroupId();
        final String parentArtifactId = this.fSecondPage.getParentArtifactId();
        final String parentVersion = this.fSecondPage.getParentVersion();
        final String projectName = this.fThirdPage.getProjectName();
        final IPath location = this.fThirdPage.isInWorkspace() ? null : this.fThirdPage.getLocation();
        
        final ProjectCreationRequest request = new ProjectCreationRequest();
        request.setMonitor(monitor);
        request.setArtifactId(artifactId);
        request.setGroupId(groupId);
        request.setVersion(version);
        request.setPomProjectName(prjPomName);
        request.setPomProjectDescription(prjPomDesc);
        request.setParentGroupId(parentGroupId);
        request.setParentArtifactId(parentArtifactId);
        request.setParentVersion(parentVersion);
        request.setProjectName(projectName);
        request.setProjectLocation(location);
        
        archetype.createProject(request);
    }

    @Override
    public boolean performFinish() {
        boolean res = super.performFinish();
        if (res) {
            if (updatePerspective()) {
                BasicNewProjectResourceWizard.updatePerspective(this.fConfigElement);
            }
            selectAndReveal(this.fLastPage.getProjectHandle());
        }
        return res;
    }

    /**
     * Stores the configuration element for the wizard. The config element will
     * be used in <code>performFinish</code> to set the result perspective.
     */
    @Override
    public void setInitializationData(IConfigurationElement cfig,
            String propertyName, Object data) {
        this.fConfigElement = cfig;
    }

    /**
     * Performs a cancel.
     */
    @Override
    public boolean performCancel() {
//        if (!fFirstPage.isExistingLocation())
//            fLastPage.performCancel();
        return super.performCancel();
    }

    /**
     * Returns the created element.
     */
    @Override
    public IModelElement getCreatedElement() {
        return DLTKCore.create(this.fLastPage.getProjectHandle());
    }

    /**
     * Returns the last page index.
     * @return last page index.
     */
    public int getLastPageIndex() {
        return this.fLastPageIndex;
    }

    /**
     * Sets the last page index.
     * @param current last page index.
     */
    public void setLastPageIndex(int current) {
        this.fLastPageIndex = current;
    }

    /**
     * Returns true if the perspective should be changed.
     * @return true to change the perspective on finish.
     */
    protected boolean updatePerspective() {
        return true;
    }
    
}
