/**
 * Copyright 2010-2012 by PHP-maven.org
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.phpmaven.phpdoc.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Configuration;
import org.phpmaven.core.BuildPluginConfiguration;
import org.phpmaven.core.ConfigurationParameter;
import org.phpmaven.phpdoc.IPhpdocEntry;
import org.phpmaven.phpdoc.IPhpdocRequest;

/**
 * Phpdoc request implementation.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
@Component(role = IPhpdocRequest.class, instantiationStrategy = "per-lookup")
@BuildPluginConfiguration(groupId = "org.phpmaven", artifactId = "maven-php-phpdoc", filter = {
        "phpdocService", "phpdocVersion"
        })
public class PhpdocRequest implements IPhpdocRequest {
    
    /**
     * The arguments to be added to phpdoc command line.
     */
    @Configuration(name = "arguments", value = "")
    private String arguments;
    
    /**
     * true to install phpdoc locally.
     */
    @Configuration(name = "installPhpdoc", value = "true")
    private boolean installPhpdoc;
    
    /**
     * The installation folder.
     */
    @ConfigurationParameter(name = "installFolder", expression = "${project.basedir}/target/phpdoc")
    private File installFolder;

    /**
     * Report folder.
     */
    private File reportFolder;

    /**
     * Entries.
     */
    private List<IPhpdocEntry> entries = new ArrayList<IPhpdocEntry>();

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPhpdocArgs() {
        return this.arguments;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPhpdocArgs(String args) {
        this.arguments = args;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getReportFolder() {
        return this.reportFolder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setReportFolder(File folder) {
        this.reportFolder = folder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getInstallFolder() {
        return this.installFolder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInstallFolder(File folder) {
        this.installFolder = folder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getInstallPhpdoc() {
        return this.installPhpdoc;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInstallPhpdoc(boolean install) {
        this.installPhpdoc = install;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addFile(File file) {
        this.entries.add(new PhpdocFileEntry(file));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addFolder(File folder) {
        this.entries.add(new PhpdocFolderEntry(folder));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<IPhpdocEntry> getEntries() {
        return this.entries;
    }

}
