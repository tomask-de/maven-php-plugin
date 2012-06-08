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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URI;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.php.internal.ui.wizards.DetectGroup;
import org.eclipse.php.internal.ui.wizards.LocationGroup;
import org.eclipse.php.internal.ui.wizards.NameGroup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * Project wizard page (project details page)
 * 
 * @author mepeisen
 */
@SuppressWarnings("restriction")
public class ProjWizProjectPage extends WizardPage implements IPrjWizardPage, PropertyChangeListener {
    
    /** the project name group. */
    private NameGroup nameGroup;
    
    /** the project location group. */
    private LocationGroup locationGroup;
    
    /** the detect group. */
    private DetectGroup detectGroup;

    /** the page validator */
    private Validator validator;

    /** the second page */
    private ProjWizMavenIdsPage secondPage;

    /**
     * Constructor.
     * @param fSecondPage the second page
     */
    public ProjWizProjectPage(ProjWizMavenIdsPage fSecondPage) {
        super("MavenIds"); //$NON-NLS-1$
        setPageComplete(false);
        setTitle(Messages.ProjWizProjectPage_Title);
        setDescription(Messages.ProjWizProjectPage_Description);
        
        this.secondPage = fSecondPage;
        this.secondPage.addPropertyChangedListener(this);
    }

    @Override
    public void createControl(Composite parent) {
        initializeDialogUnits(parent);

        final Composite container = new Composite(parent, SWT.NULL);
        container.setLayout(new GridLayout(1, false));
        
        this.nameGroup = new NameGroup(container, "", getShell()); //$NON-NLS-1$
        this.locationGroup = new LocationGroup(container, this.nameGroup, getShell());
        this.detectGroup = new DetectGroup(container, this.locationGroup, this.nameGroup);

        this.nameGroup.addObserver(this.locationGroup);
        this.locationGroup.addObserver(this.detectGroup);
        this.nameGroup.notifyObservers();

        // create and connect validator
        this.validator = new Validator();

        this.nameGroup.addObserver(this.validator);
        this.locationGroup.addObserver(this.validator);

        Dialog.applyDialogFont(container);
        setControl(container);
    }
    
    /**
     * Validate this page and show appropriate warnings and error
     * NewWizardMessages.
     */
    public final class Validator implements Observer {
        @Override
        public void update(Observable o, Object arg) {
            final IWorkspace workspace = ResourcesPlugin.getWorkspace();
            final String name = ProjWizProjectPage.this.nameGroup.getName();
            // check whether the project name field is empty
            if (name.length() == 0) {
                setErrorMessage(null);
                setMessage(Messages.ProjWizProjectPage_Error_MissingProjectName);
                setPageComplete(false);
                return;
            }
            // check whether the project name is valid
            final IStatus nameStatus = workspace.validateName(name,
                    IResource.PROJECT);
            if (!nameStatus.isOK()) {
                setErrorMessage(nameStatus.getMessage());
                setPageComplete(false);
                return;
            }
            // check whether project already exists
            final IProject handle = getProjectHandle();

            if (!isInLocalServer()) {
                if (handle.exists()) {
                    setErrorMessage(Messages.ProjWizProjectPage_Error_ProjectExists);
                    setPageComplete(false);
                    return;
                }
            }

            IProject[] projects = ResourcesPlugin.getWorkspace().getRoot()
                    .getProjects();
            String newProjectNameLowerCase = name.toLowerCase();
            for (IProject currentProject : projects) {
                String existingProjectName = currentProject.getName();
                if (existingProjectName.toLowerCase().equals(
                        newProjectNameLowerCase)) {
                    setErrorMessage(Messages.ProjWizProjectPage_Error_ProjectExists);
                    setPageComplete(false);
                    return;
                }
            }

            final String location = ProjWizProjectPage.this.locationGroup.getLocation()
                    .toOSString();
            // check whether location is empty
            if (location.length() == 0) {
                setErrorMessage(null);
                setMessage(Messages.ProjWizProjectPage_Error_EnterLocation);
                setPageComplete(false);
                return;
            }
            // check whether the location is a syntactically correct path
            if (!Path.EMPTY.isValidPath(location)) {
                setErrorMessage(Messages.ProjWizProjectPage_Error_InvalidLocation);
                setPageComplete(false);
                return;
            }
            // check whether the location has the workspace as prefix
            IPath projectPath = Path.fromOSString(location);
            if (!ProjWizProjectPage.this.locationGroup.isInWorkspace()
                    && Platform.getLocation().isPrefixOf(projectPath)) {
                setErrorMessage(Messages.ProjWizProjectPage_Error_LocationIsWorkspace);
                setPageComplete(false);
                return;
            }
            // If we do not place the contents in the workspace validate the
            // location.
            if (!ProjWizProjectPage.this.locationGroup.isInWorkspace()) {
                IEnvironment environment = getEnvironment();
                if (EnvironmentManager.isLocal(environment)) {
                    final IStatus locationStatus = workspace
                            .validateProjectLocation(handle, projectPath);
                    final File file = projectPath.toFile();
                    if (!locationStatus.isOK()) {
                        setErrorMessage(locationStatus.getMessage());
                        setPageComplete(false);
                        return;
                    }

                    if (!canCreate(file)) {
                        setErrorMessage(Messages.ProjWizProjectPage_Error_InvalidLocation);
                        setPageComplete(false);
                        return;
                    }
                }
            }

            setPageComplete(true);
            setErrorMessage(null);
            setMessage(null);
        }
    }

    /**
     * Returns the location URI
     * @return uri
     */
    public URI getLocationURI() {
        IEnvironment environment = getEnvironment();
        return environment.getURI(this.locationGroup.getLocation());
    }

    /**
     * Returns the environment.
     * @return environment
     */
    public IEnvironment getEnvironment() {
        return this.locationGroup.getEnvironment();
    }

    /**
     * Returns true if the location is in workspace
     * @return true if the location is in workspace
     */
    public boolean isInWorkspace() {
        return this.locationGroup.isInWorkspace();
    }

    /**
     * Returns true if the location is in local server.
     * @return true if location is in local server.
     */
    public boolean isInLocalServer() {
        return this.locationGroup.isInLocalServer();
    }

    /**
     * Returns false if the file cannot be created.
     * @param file file to be tested
     * @return false if it cannot be created.
     */
    private boolean canCreate(File file) {
        File file2 = file;
        while (!file2.exists()) {
            file2 = file2.getParentFile();
            if (file2 == null)
                return false;
        }

        return file2.canWrite();
    }
    
    /**
     * Returns the project name.
     * @return name
     */
    public String getProjectName() {
        return this.nameGroup.getName();
    }

    /**
     * Returns the project location.
     * @return project location.
     */
    public IPath getLocation() {
        return this.locationGroup.getLocation();
    }

    /**
     * Creates a project resource handle for the current project name field
     * value.
     * <p>
     * This method does not create the project resource; this is the
     * responsibility of <code>IProject::create</code> invoked by the new
     * project resource wizard.
     * </p>
     * 
     * @return the new project resource handle
     */
    @Override
    public IProject getProjectHandle() {
        return ResourcesPlugin.getWorkspace().getRoot()
                .getProject(this.nameGroup.getName());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final String artifactId = this.secondPage.getArtifactId();
        final String version = this.secondPage.getVersion();
        if (this.nameGroup != null) {
            this.nameGroup.setName(artifactId + "-" + version); //$NON-NLS-1$
        }
    }
    
}
