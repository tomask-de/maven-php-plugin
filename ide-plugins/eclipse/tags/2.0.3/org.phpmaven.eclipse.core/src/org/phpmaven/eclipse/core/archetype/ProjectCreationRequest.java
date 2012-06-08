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

package org.phpmaven.eclipse.core.archetype;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * A project creation request.
 * @author mepeisen
 */
public class ProjectCreationRequest {
    /** the project group id. */
    private String groupId;
    /** the project artifact id. */
    private String artifactId;
    /** the project version. */
    private String version;
    /** the progress monitor. */
    private IProgressMonitor monitor = new NullProgressMonitor();
    
    /** the parent group id. */
    private String parentGroupId;
    /** the parent artifact id. */
    private String parentArtifactId;
    /** the parent version. */
    private String parentVersion;
    
    /** the pom project name. */
    private String pomProjectName;
    /** the pom project description. */
    private String pomProjectDescription;
    
    /** the name of the project. */
    private String projectName;
    
    /** the project location. */
    private IPath projectLocation;
    
    /**
     * @return the projectLocation
     */
    public IPath getProjectLocation() {
        return this.projectLocation;
    }
    /**
     * @param projectLocation the projectLocation to set
     */
    public void setProjectLocation(IPath projectLocation) {
        this.projectLocation = projectLocation;
    }
    /**
     * @return the parentGroupId
     */
    public String getParentGroupId() {
        return this.parentGroupId;
    }
    /**
     * @param parentGroupId the parentGroupId to set
     */
    public void setParentGroupId(String parentGroupId) {
        this.parentGroupId = parentGroupId;
    }
    /**
     * @return the parentArtifactId
     */
    public String getParentArtifactId() {
        return this.parentArtifactId;
    }
    /**
     * @param parentArtifactId the parentArtifactId to set
     */
    public void setParentArtifactId(String parentArtifactId) {
        this.parentArtifactId = parentArtifactId;
    }
    /**
     * @return the parentVersion
     */
    public String getParentVersion() {
        return this.parentVersion;
    }
    /**
     * @param parentVersion the parentVersion to set
     */
    public void setParentVersion(String parentVersion) {
        this.parentVersion = parentVersion;
    }
    /**
     * @return the pomProjectName
     */
    public String getPomProjectName() {
        return this.pomProjectName;
    }
    /**
     * @param pomProjectName the pomProjectName to set
     */
    public void setPomProjectName(String pomProjectName) {
        this.pomProjectName = pomProjectName;
    }
    /**
     * @return the pomProjectDescription
     */
    public String getPomProjectDescription() {
        return this.pomProjectDescription;
    }
    /**
     * @param pomProjectDescription the pomProjectDescription to set
     */
    public void setPomProjectDescription(String pomProjectDescription) {
        this.pomProjectDescription = pomProjectDescription;
    }
    /**
     * @return the projectName
     */
    public String getProjectName() {
        return this.projectName;
    }
    /**
     * @param projectName the projectName to set
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    /**
     * @return the groupId
     */
    public String getGroupId() {
        return this.groupId;
    }
    /**
     * @param groupId the groupId to set
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    /**
     * @return the artifactId
     */
    public String getArtifactId() {
        return this.artifactId;
    }
    /**
     * @param artifactId the artifactId to set
     */
    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }
    /**
     * @return the version
     */
    public String getVersion() {
        return this.version;
    }
    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }
    /**
     * @return the monitor
     */
    public IProgressMonitor getMonitor() {
        return this.monitor;
    }
    /**
     * @param monitor the monitor to set
     */
    public void setMonitor(IProgressMonitor monitor) {
        this.monitor = monitor;
    }
    
}