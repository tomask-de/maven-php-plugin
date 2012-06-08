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

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.phpmaven.eclipse.ui.PhpmavenUiPlugin;

/**
 * Settings for the new project wizard.
 * 
 * @author mepeisen
 */
public class NewProjectWizardSettings {
    
    /** key */
    private static final String KEY_LAST_VERSIONS = "NewProjectWizardSettings.lastVersions"; //$NON-NLS-1$

    /** key */
    private static final String KEY_LAST_ARTIFACT_IDS = "NewProjectWizardSettings.lastArtifactIds"; //$NON-NLS-1$

    /** key */
    private static final String KEY_LAST_GROUP_IDS = "NewProjectWizardSettings.lastGroupIds"; //$NON-NLS-1$

    /**
     * The singleton instance.
     */
    private static NewProjectWizardSettings INSTANCE;

    /**
     * The last entered group ids.
     */
    private String[] lastGroupIds;
    
    /**
     * The last entered artifact ids.
     */
    private String[] lastArtifactIds;
    
    /**
     * The last entered versions.
     */
    private String[] lastVersions;

    /**
     * Hidden constructor.
     */
    private NewProjectWizardSettings() {
        final IDialogSettings settings = PhpmavenUiPlugin.getDefault().getDialogSettings();
        this.lastGroupIds = settings.getArray(KEY_LAST_GROUP_IDS);
        this.lastArtifactIds = settings.getArray(KEY_LAST_ARTIFACT_IDS);
        this.lastVersions = settings.getArray(KEY_LAST_VERSIONS);
        
        if (this.lastGroupIds == null) {
            this.lastGroupIds = new String[0];
        }
        if (this.lastArtifactIds == null) {
            this.lastArtifactIds = new String[0];
        }
        if (this.lastVersions == null) {
            this.lastVersions = new String[0];
        }
    }
    
    /**
     * Returns the singleton instance.
     * @return singleton instance.
     */
    public static NewProjectWizardSettings instance() {
        if (INSTANCE == null) {
            INSTANCE = new NewProjectWizardSettings();
        }
        return INSTANCE;
    }
    
    /**
     * The last group ids.
     * @return group ids.
     */
    public String[] getLastGroupIds() {
        return this.lastGroupIds;
    }
    
    /**
     * The last entered artifact ids.
     * @return artifact ids.
     */
    public String[] getArtifactIds() {
        return this.lastArtifactIds;
    }
    
    /**
     * The last versions.
     * @return versions.
     */
    public String[] getVersions() {
        return this.lastVersions;
    }
    
    /**
     * Adds a new element to given array and sets it to preferences.
     * @param oldArr the old array.
     * @param element element to be added.
     * @param settingsKey the key in the settings.
     * @return the new value
     */
    private String[] addElement(String[] oldArr, String element, String settingsKey) {
        final List<String> elements = Arrays.asList(oldArr);
        if (elements.contains(element)) {
            elements.remove(element);
            elements.add(element);
        } else {
            elements.add(element);
            if (elements.size() > 10) {
                elements.remove(0);
            }
        }
        final IDialogSettings settings = PhpmavenUiPlugin.getDefault().getDialogSettings();
        final String[] elementsArr = elements.toArray(new String[elements.size()]);
        settings.put(settingsKey, elementsArr);
        return elementsArr;
    }
    
    /**
     * Adds a group id.
     * @param groupId group id.
     */
    public void addGroupId(String groupId) {
        this.lastGroupIds = this.addElement(this.lastGroupIds, groupId, KEY_LAST_GROUP_IDS);
    }
    
    /**
     * Adds a artifact id.
     * @param artifactId artifact id.
     */
    public void addArtifactId(String artifactId) {
        this.lastArtifactIds = this.addElement(this.lastArtifactIds, artifactId, KEY_LAST_ARTIFACT_IDS);
    }
    
    /**
     * Adds a version.
     * @param version version.
     */
    public void addVersion(String version) {
        this.lastVersions = this.addElement(this.lastVersions, version, KEY_LAST_VERSIONS);
    }
    
}
