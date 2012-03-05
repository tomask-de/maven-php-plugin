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

package org.phpmaven.pear.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.util.xml.XmlStreamReader;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.phpmaven.exec.PhpCoreException;
import org.phpmaven.exec.PhpException;
import org.phpmaven.pear.IDependency;
import org.phpmaven.pear.IPackageVersion;
import org.phpmaven.pear.IPearChannel;
import org.phpmaven.pear.IPearUtility;
import org.phpmaven.pear.IVersion;

/**
 * Package version implementation.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public class PackageVersion implements IPackageVersion {
    
    /**
     * Name of the package.
     */
    private String name;
    
    /**
     * The version number.
     */
    private IVersion version;
    
    /**
     * version stability.
     */
    private String stability;
    
    /**
     * Minimum php version.
     */
    private String minPhpVersion;
    
    /**
     * The pear utility.
     */
    private IPearUtility pearUtility;
    
    /**
     * The pear channel.
     */
    private IPearChannel pearChannel;

    /**
     * true if this package version is initialized.
     */
    private boolean packageInitialized;

    /**
     * The api version.
     */
    private IVersion apiVersion;

    /**
     * The license.
     */
    private String license;

    /**
     * The releasing developer.
     */
    private String releasingDeveloper;

    /**
     * The summary.
     */
    private String summary;

    /**
     * The description.
     */
    private String description;

    /**
     * Date of release.
     */
    private String dateReleased;

    /**
     * The release notes.
     */
    private String releaseNotes;

    /**
     * File size in bytes.
     */
    private int fileSize;

    /**
     * File download url.
     */
    private String fileUrl;

    /**
     * Link to Package xml.
     */
    private String packageXml;
    
    /**
     * The required dependencies.
     */
    private List<IDependency> requiredDeps;
    
    /**
     * The optional dependencies.
     */
    private List<IDependency> optionalDeps;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPackageName() {
        return this.name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPackageName(String n) {
        this.name = n;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IVersion getVersion() {
        return this.version;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setVersion(IVersion version) {
        this.version = version;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStability() {
        return this.stability;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStability(String stability) {
        this.stability = stability;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMinPhpVersion() {
        return this.minPhpVersion;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMinPhpVersion(String v) {
        this.minPhpVersion = v;
    }
    
    /**
     * Initializes this package.
     * @throws PhpException thrown on execution errors.
     */
    private void initializeData() throws PhpException {
        if (!this.packageInitialized) {
            try {
                Xpp3Dom dom = null;
                try {
                    final String channelXml = Helper.getTextFileContents(
                            this.pearChannel.getRestUrl(IPearChannel.REST_1_3),
                            "r/" + this.getPackageName().toLowerCase() +
                            "/v2." + this.getVersion().getPearVersion() + ".xml");
                    dom = Xpp3DomBuilder.build(
                            new XmlStreamReader(new ByteArrayInputStream(channelXml.getBytes())));
                } catch (IOException ex) {
                    // fall back to rest 1.0
                    final String channelXml = Helper.getTextFileContents(
                            this.pearChannel.getRestUrl(IPearChannel.REST_1_0),
                            "r/" + this.getPackageName().toLowerCase() +
                            "/" + this.getVersion().getPearVersion() + ".xml");
                    dom = Xpp3DomBuilder.build(
                            new XmlStreamReader(new ByteArrayInputStream(channelXml.getBytes())));
                }
                
                for (final Xpp3Dom child : dom.getChildren()) {
                    if ("a".equals(child.getName())) {
                        this.apiVersion = new Version();
                        this.apiVersion.setPearVersion(child.getValue());
                    } else if ("mp".equals(child.getName())) {
                        this.minPhpVersion = child.getValue();
                    } else if ("st".equals(child.getName())) {
                        this.stability = child.getValue();
                    } else if ("l".equals(child.getName())) {
                        this.license = child.getValue();
                    } else if ("m".equals(child.getName())) {
                        this.releasingDeveloper = child.getValue();
                    } else if ("s".equals(child.getName())) {
                        this.summary = child.getValue();
                    } else if ("d".equals(child.getName())) {
                        this.description = child.getValue();
                    } else if ("da".equals(child.getName())) {
                        this.dateReleased = child.getValue();
                    } else if ("n".equals(child.getName())) {
                        this.releaseNotes = child.getValue();
                    } else if ("f".equals(child.getName())) {
                        this.fileSize = Integer.parseInt(child.getValue());
                    } else if ("g".equals(child.getName())) {
                        this.fileUrl = child.getValue();
                    } else if ("x".equals(child.getName())) {
                        this.packageXml = child.getValue();
                    } else {
                        throw new PhpCoreException("Unknown name in version.xml: " + child.getName());
                    }
                }
            } catch (IOException ex) {
                throw new PhpCoreException("Problems reading version.xml", ex);
            } catch (XmlPullParserException ex) {
                throw new PhpCoreException("Problems reading version.xml", ex);
            }
            this.packageInitialized = true;
        }
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public String getReleasingDeveloper() throws PhpException {
        this.initializeData();
        return this.releasingDeveloper;
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public void setReleasingDeveloper(String developerNick) throws PhpException {
        this.initializeData();
        this.releasingDeveloper = developerNick;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSummary() throws PhpException {
        this.initializeData();
        return this.summary;
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public void setSummary(String summary) throws PhpException {
        this.initializeData();
        this.summary = summary;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() throws PhpException {
        this.initializeData();
        return this.description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDescription(String description) throws PhpException {
        this.initializeData();
        this.description = description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getReleaseDate() throws PhpException {
        this.initializeData();
        return this.dateReleased;
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public void setReleaseDate(String date) throws PhpException {
        this.initializeData();
        this.dateReleased = date;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getReleaseNotes() throws PhpException {
        this.initializeData();
        return this.releaseNotes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setReleaseNotes(String notes) throws PhpException {
        this.initializeData();
        this.releaseNotes = notes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getFileSize() throws PhpException {
        this.initializeData();
        return this.fileSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFileSize(int bytes) throws PhpException {
        this.initializeData();
        this.fileSize = bytes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUrl() throws PhpException {
        this.initializeData();
        return this.fileUrl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUrl(String url) throws PhpException {
        this.initializeData();
        this.fileUrl = url;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IVersion getApiVersion() throws PhpException {
        this.initializeData();
        return this.apiVersion;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setApiVersion(IVersion v) throws PhpException {
        this.initializeData();
        this.apiVersion = v;
    }
    
    /**
     * Initializes this packages extended infos.
     * @throws PhpException thrown on execution errors.
     */
    private void initializeExtendedData() throws PhpException {
        if (this.requiredDeps == null) {
            this.initializeData();
            final List<IDependency> required = new ArrayList<IDependency>();
            final List<IDependency> optional = new ArrayList<IDependency>();
            try {
                final String channelXml = Helper.getTextFileContents(this.fileUrl);
                // TODO http://pear.php.net/manual/en/guide.developers.package2.intro.php
                final Xpp3Dom dom = Xpp3DomBuilder.build(
                        new XmlStreamReader(new ByteArrayInputStream(channelXml.getBytes())));
                
                for (final Xpp3Dom child : dom.getChildren()) {
                    if ("name".equals(child.getName())) {
                        // skip
                    } else if ("channel".equals(child.getName())) {
                        // skip
                    } else if ("extends".equals(child.getName())) {
                        // TODO
                    } else if ("summary".equals(child.getName())) {
                        // TODO
                    } else if ("description".equals(child.getName())) {
                        // TODO
                    } else if ("lead".equals(child.getName())) {
                        // TODO
                    } else if ("developer".equals(child.getName())) {
                        // TODO
                    } else if ("contributor".equals(child.getName())) {
                        // TODO
                    } else if ("helper".equals(child.getName())) {
                        // TODO
                    } else if ("maintainers".equals(child.getName())) {
                        // TODO
                    } else if ("date".equals(child.getName())) {
                        // TODO
                    } else if ("time".equals(child.getName())) {
                        // TODO
                    } else if ("version".equals(child.getName())) {
                        // TODO
                    } else if ("stability".equals(child.getName())) {
                        // TODO
                    } else if ("license".equals(child.getName())) {
                        // TODO
                    } else if ("notes".equals(child.getName())) {
                        // TODO
                    } else if ("contents".equals(child.getName())) {
                        // TODO
                    } else if ("compatible".equals(child.getName())) {
                        // TODO
                    } else if ("dependencies".equals(child.getName())) {
                        for (final Xpp3Dom depChild : child.getChildren()) {
                            if ("optional".equals(depChild.getName())) {
                                this.initializeDeps(optional, depChild);
                            } else if ("required".equals(depChild.getName())) {
                                this.initializeDeps(optional, depChild);
                            } else {
                                throw new PhpCoreException(
                                        "Unknown name in package.xml->dependencies: " + depChild.getName());
                            }
                        }
                    } else if ("userrole".equals(child.getName())) {
                        // TODO
                    } else if ("phprelease".equals(child.getName())) {
                        // TODO
                    } else if ("changelog".equals(child.getName())) {
                        // TODO
                    } else {
                        throw new PhpCoreException("Unknown name in package.xml: " + child.getName());
                    }
                }
            } catch (IOException ex) {
                throw new PhpCoreException("Problems reading version.xml", ex);
            } catch (XmlPullParserException ex) {
                throw new PhpCoreException("Problems reading version.xml", ex);
            }
            this.requiredDeps = required;
            this.optionalDeps = optional;
        }
    }

    /**
     * Initialized the dependencies from dom.
     * @param depsArray the array to put the dependencies to.
     * @param dom the dom node.
     * @throws PhpException thrown on execution errors.
     */
    private void initializeDeps(List<IDependency> depsArray, Xpp3Dom dom) throws PhpException {
        for (final Xpp3Dom child : dom.getChildren()) {
            if ("pearinstaller".equals(child.getName())) {
                // TODO
            } else if ("php".equals(child.getName())) {
                // TODO
            } else if ("subpackage".equals(child.getName())) {
                // TODO
            } else if ("package".equals(child.getName())) {
                // TODO
            } else {
                throw new PhpCoreException("Unknown name in package.xml->dependencies: " + child.getName());
            }
        }
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public Iterable<IDependency> getRequiredDependencies() throws PhpException {
        this.initializeExtendedData();
        return this.requiredDeps;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<IDependency> getOptionalDependencies() throws PhpException {
        this.initializeExtendedData();
        return this.optionalDeps;
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public void addDependency(IDependency dep, boolean isOptional) throws PhpException {
        this.initializeExtendedData();
        if (isOptional) {
            this.optionalDeps.add(dep);
        } else {
            this.requiredDeps.add(dep);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(IPearUtility util, IPearChannel channel) {
        if (this.pearUtility != null) {
            throw new IllegalStateException("Must not be called twice.");
        }
        this.pearUtility = util;
        this.pearChannel = channel;
    }

}
