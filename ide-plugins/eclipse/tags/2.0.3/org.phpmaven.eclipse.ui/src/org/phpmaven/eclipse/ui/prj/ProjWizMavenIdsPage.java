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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.m2e.core.internal.index.IndexedArtifactFile;
import org.eclipse.m2e.core.ui.internal.dialogs.MavenRepositorySearchDialog;
import org.eclipse.m2e.core.ui.internal.wizards.MavenParentComponent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Project wizard page (maven IDs page)
 * 
 * @author mepeisen
 */
@SuppressWarnings("restriction")
public class ProjWizMavenIdsPage extends WizardPage implements IPrjWizardPage {
    
    /** the group id text. */
    private Text groupText;
    
    /** the artifact id text. */
    private Text artifactIdText;
    
    /** the version text. */
    private Text versionText;
    
    /** the name text */
    private Text nameText;
    
    /** the description text */
    private Text descriptionText;

    /** the maven parent component */
    private MavenParentComponent parentComponent;
    
    /** the listeners. */
    private List<PropertyChangeListener> listeners = new ArrayList<PropertyChangeListener>();

    /** */
    private String groupId;

    /** */
    private String artifactId;

    /** */
    private String version;

    /** */
    private String projectName;

    /** */
    private String projectDescription;

    /** */
    private boolean hasParentProject;

    /** */
    private String parentGroupId;

    /** */
    private String parentArtifactId;

    /** */
    private String parentVersion;

    /**
     * Constructor.
     */
    public ProjWizMavenIdsPage() {
        super("MavenIds"); //$NON-NLS-1$
        setPageComplete(false);
        setTitle(Messages.ProjWizMavenIdsPage_Title);
        setDescription(Messages.ProjWizMavenIdsPage_Description);
        
        // TODO Archetype parameters...
        // TODO http://git.eclipse.org/c/m2e/m2e-core.git/tree/org.eclipse.m2e.core.ui/src/org/eclipse/m2e/core/ui/internal/wizards/MavenProjectWizardArchetypeParametersPage.java
    }

    @Override
    public void createControl(Composite parent) {
        final NewProjectWizardSettings settings = NewProjectWizardSettings.instance();
        
        initializeDialogUnits(parent);
        
        final Composite container = new Composite(parent, SWT.NULL);
        container.setLayout(new GridLayout(1, false));
        
        final Composite header = new Composite(container, SWT.NULL);
        GridLayoutFactory.fillDefaults().applyTo(header);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(header);

        Composite fieldsContainer = new Composite(header, SWT.NULL);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(fieldsContainer);

        int numColumns = 2; // 1 for label, 2 for text
        GridLayoutFactory.fillDefaults().numColumns(numColumns).applyTo(fieldsContainer);
        
        // Group id
        final Label labelModuleId = new Label(fieldsContainer, SWT.NULL);
        labelModuleId.setText(Messages.ProjWizMavenIdsPage_LabelGroupID);

        final Composite groupIdContainer = new Composite(fieldsContainer, SWT.NULL);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(groupIdContainer);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(groupIdContainer);

        this.groupText = new Text(groupIdContainer, SWT.SINGLE | SWT.BORDER);
        final String[] groupIds = settings.getLastGroupIds();
        this.groupText.setText(groupIds.length == 0 ? "org.mydomain" : groupIds[groupIds.length]); //$NON-NLS-1$

        this.groupText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                textChanged();
            }
        });
        GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(this.groupText);

        // artifact id
        final Label labelArtifactId = new Label(fieldsContainer, SWT.NULL);
        labelArtifactId.setText(Messages.ProjWizMavenIdsPage_LabelArtifactID);

        final Composite artifactIdContainer = new Composite(fieldsContainer, SWT.NULL);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(artifactIdContainer);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(artifactIdContainer);

        this.artifactIdText = new Text(artifactIdContainer, SWT.SINGLE | SWT.BORDER);

        this.artifactIdText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                textChanged();
            }
        });
        GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(this.artifactIdText);

        // version number
        final Label labelVersion = new Label(fieldsContainer, SWT.NULL);
        labelVersion.setText(Messages.ProjWizMavenIdsPage_LabelVersion);

        final Composite versionContainer = new Composite(fieldsContainer, SWT.NULL);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(versionContainer);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(versionContainer);

        this.versionText = new Text(versionContainer, SWT.SINGLE | SWT.BORDER);
        final String[] versions = settings.getVersions();
        this.versionText.setText(versions.length == 0 ? "0.1.0-SNAPSHOT" : versions[versions.length]); //$NON-NLS-1$

        this.versionText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                textChanged();
            }
        });
        GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(this.versionText);

        // name text
        final Label labelName = new Label(fieldsContainer, SWT.NULL);
        labelName.setText(Messages.ProjWizMavenIdsPage_LabelName);

        final Composite nameContainer = new Composite(fieldsContainer, SWT.NULL);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(nameContainer);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(nameContainer);

        this.nameText = new Text(nameContainer, SWT.SINGLE | SWT.BORDER);
        this.nameText.setText(Messages.ProjWizMavenIdsPage_DefaultName);
        GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(this.nameText);

        // description text
        final Label labelDescription = new Label(fieldsContainer, SWT.NULL);
        labelDescription.setText(Messages.ProjWizMavenIdsPage_LabelDescription);

        final Composite descriptionContainer = new Composite(fieldsContainer, SWT.NULL);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(descriptionContainer);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(descriptionContainer);

        this.descriptionText = new Text(descriptionContainer, SWT.SINGLE | SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(this.descriptionText);
        
        // parent
        this.parentComponent = new MavenParentComponent(container, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(this.parentComponent);
        this.parentComponent.addModifyListener(new ModifyListener() {
            
            @Override
            public void modifyText(ModifyEvent e) {
                textChanged();
            }
        });
        this.parentComponent.addBrowseButtonListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
              MavenRepositorySearchDialog dialog = MavenRepositorySearchDialog.createSearchParentDialog(getShell(), 
                  "", null, null);  //$NON-NLS-1$
              
              if(dialog.open() == Window.OK) {
                IndexedArtifactFile indexedArtifactFile = (IndexedArtifactFile) dialog.getFirstResult();
                if(indexedArtifactFile != null) {
                  ProjWizMavenIdsPage.this.parentComponent.setValues(indexedArtifactFile.group, indexedArtifactFile.artifact,
                      indexedArtifactFile.version);
                }
              }
            }
          });

        Dialog.applyDialogFont(container);
        setControl(container);
        
        this.textChanged(); // for initial validation
    }

    /**
     * probes if the wizard can be finished.
     */
    private void textChanged() {
        this.groupId = this.groupText.getText() == null ? null : this.groupText.getText().trim();
        this.artifactId = this.artifactIdText.getText() == null ? null : this.artifactIdText.getText().trim();
        this.version = this.versionText.getText() == null ? null : this.versionText.getText().trim();
        
        // group validating
        if (this.groupId == null || this.groupId.length() == 0) {
            this.setErrorMessage(Messages.ProjWizMavenIdsPage_Error_MissingGroupID);
            this.setPageComplete(false);
        } else if (this.groupId.contains(" ")) { //$NON-NLS-1$
            this.setErrorMessage(Messages.ProjWizMavenIdsPage_Error_GroupIDWithSpaces);
            this.setPageComplete(false);
        } else if (!ResourcesPlugin.getWorkspace().validateName(this.groupId, IResource.PROJECT).isOK()) {
            this.setErrorMessage(Messages.ProjWizMavenIdsPage_Error_InvalidGroupID);
            this.setPageComplete(false);
        } else if (!this.groupId.matches("[A-Za-z0-9_\\-.]+")) { //$NON-NLS-1$
            this.setErrorMessage(Messages.ProjWizMavenIdsPage_Error_InvalidGroupID);
            this.setPageComplete(false);
            
        // artifact validating
        } else if (this.artifactId == null || this.artifactId.length() == 0) {
            this.setErrorMessage(Messages.ProjWizMavenIdsPage_Error_MissingArtifactId);
            this.setPageComplete(false);
        } else if (this.artifactId.contains(" ")) { //$NON-NLS-1$
            this.setErrorMessage(Messages.ProjWizMavenIdsPage_Error_ArtifactIDWithSpaces);
            this.setPageComplete(false);
        } else if (!ResourcesPlugin.getWorkspace().validateName(this.artifactId, IResource.PROJECT).isOK()) {
            this.setErrorMessage(Messages.ProjWizMavenIdsPage_Error_InvalidArtifactID);
            this.setPageComplete(false);
        } else if (!this.artifactId.matches("[A-Za-z0-9_\\-.]+")) { //$NON-NLS-1$
            this.setErrorMessage(Messages.ProjWizMavenIdsPage_Error_InvalidArtifactID);
            this.setPageComplete(false);
            
        // version validating
        } else if (this.version == null || this.version.length() == 0) {
            this.setErrorMessage(Messages.ProjWizMavenIdsPage_Error_MissingVersion);
            this.setPageComplete(false);
        } else if (!this.version.matches("[0-9]+(\\.[0-9]+){0,2}(-.*)?")) { //$NON-NLS-1$
            this.setErrorMessage(Messages.ProjWizMavenIdsPage_Error_InvalidVersion);
            this.setPageComplete(false);
            
        // parent validation
        } else if (!this.parentComponent.validate()) {
            this.setErrorMessage(Messages.ProjWizMavenIdsPage_Error_InvalidParent);
            this.setPageComplete(false);

        // everything ok
        } else {
            this.setErrorMessage(null);
            this.setPageComplete(true);
        }

        this.projectName = this.nameText.getText();
        this.projectDescription = this.descriptionText.getText();
        this.hasParentProject = this.parentComponent.getGroupIdCombo().getText() == null || this.parentComponent.getGroupIdCombo().getText().length() == 0;
        this.parentGroupId = this.parentComponent.getGroupIdCombo().getText();
        this.parentArtifactId = this.parentComponent.getArtifactIdCombo().getText();
        this.parentVersion = this.parentComponent.getVersionCombo().getText();

        for (final PropertyChangeListener listener : this.listeners) {
            listener.propertyChange(new PropertyChangeEvent(this, "model", null, null)); //$NON-NLS-1$
        }
    }
    
    /**
     * Returns the version number.
     * @return version
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * Returns the artifact id.
     * @return artifact id.
     */
    public String getArtifactId() {
        return this.artifactId;
    }

    /**
     * Returns the group id.
     * @return group id.
     */
    public String getGroupId() {
        return this.groupId;
    }
    
    /**
     * Returns the project name.
     * @return project name.
     */
    public String getProjectName() {
        return this.projectName;
    }
    
    /**
     * Returns the project description.
     * @return description.
     */
    public String getProjectDescription() {
        return this.projectDescription;
    }
    
    /**
     * Returns true if there is a parent project.
     * @return true if there is a parent project.
     */
    public boolean hasParentProject() {
        return this.hasParentProject;
    }
    
    /**
     * Returns the parent projects group id.
     * @return parent project group id.
     */
    public String getParentGroupId() {
        return this.parentGroupId;
    }
    
    /**
     * Returns the parent project artifact id.
     * @return parent project artifact id.
     */
    public String getParentArtifactId() {
        return this.parentArtifactId;
    }
    
    /**
     * Returns the parent project version.
     * @return parent project version.
     */
    public String getParentVersion() {
        return this.parentVersion;
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
        /*return ResourcesPlugin.getWorkspace().getRoot()
                .getProject(fNameGroup.getName());*/
        return null;
    }

    /**
     * Adds a property changes listener.
     * @param listener
     */
    public void addPropertyChangedListener(PropertyChangeListener listener) {
        this.listeners.add(listener);
    }
    
}
