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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.plexus.util.xml.XmlStreamReader;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.phpmaven.exec.PhpCoreException;
import org.phpmaven.exec.PhpException;
import org.phpmaven.pear.IDependency;
import org.phpmaven.pear.IDependency.DependencyType;
import org.phpmaven.pear.IMaintainer;
import org.phpmaven.pear.IPackage;
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
     * The maintainers.
     */
    private List<IMaintainer> maintainers;

    /**
     * The file contents.
     */
    private Map<String, List<String>> fileContents;
    
    /**
     * The name of the extension if this is a php extension.
     * @return name of the extension or {@code null} for php modules.
     */
    private String providesExtension;

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
                    } else if ("v".equals(child.getName())) {
                        // TODO version
                    } else if ("st".equals(child.getName())) {
                        this.stability = child.getValue();
                    } else if ("l".equals(child.getName())) {
                        this.license = child.getValue();
                    } else if ("p".equals(child.getName())) {
                        this.name = child.getValue();
                    } else if ("c".equals(child.getName())) {
                        // TODO channel
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
                        this.packageXml = child.getAttribute("xlink:href") != null ?
                                child.getAttribute("xlink:href") :
                                    child.getValue();
                        if (this.packageXml.indexOf("/") == -1) {
                            this.packageXml = this.pearChannel.getRestUrl(IPearChannel.REST_1_3) +
                                    "/r/" + this.getPackageName().toLowerCase() + "/" + this.packageXml;
                        }
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
            final List<IMaintainer> maintain = new ArrayList<IMaintainer>();
            final Map<String, List<String>> files = new HashMap<String, List<String>>();
            try {
                final String channelXml = Helper.getTextFileContents(this.packageXml);
                // TODO http://pear.php.net/manual/en/guide.developers.package2.intro.php
                final Xpp3Dom dom = Xpp3DomBuilder.build(
                        new XmlStreamReader(new ByteArrayInputStream(channelXml.getBytes())));
                
                processPackageXml(required, optional, maintain, files, dom);
            } catch (IOException ex) {
                throw new PhpCoreException("Problems reading version.xml", ex);
            } catch (XmlPullParserException ex) {
                throw new PhpCoreException("Problems reading version.xml", ex);
            }
            this.requiredDeps = required;
            this.optionalDeps = optional;
            this.maintainers = maintain;
            this.fileContents = files;
        }
    }

    private void processPackageXml(
            final List<IDependency> required, final List<IDependency> optional, 
            final List<IMaintainer> maintain, final Map<String, List<String>> files, 
            final Xpp3Dom dom)
        throws PhpException {
        for (final Xpp3Dom child : dom.getChildren()) {
            if ("name".equals(child.getName())) {
                // skip
            } else if ("channel".equals(child.getName())) {
                // skip
            } else if ("extends".equals(child.getName())) {
                // TODO
            } else if ("summary".equals(child.getName())) {
                // TODO summary already read from initializeData()
            } else if ("description".equals(child.getName())) {
                // TODO description already read from initializeData()
            } else if ("lead".equals(child.getName())) {
                fetchLead(maintain, child);
            } else if ("developer".equals(child.getName())) {
                fetchDeveloper(maintain, child);
            } else if ("contributor".equals(child.getName())) {
                fetchContributor(maintain, child);
            } else if ("helper".equals(child.getName())) {
                fetchHelper(maintain, child);
            } else if ("maintainers".equals(child.getName())) {
                fetchMaintainers(maintain, child);
            } else if ("date".equals(child.getName())) {
                // TODO
            } else if ("release".equals(child.getName())) {
                fetchRelease(required, optional, files, child);
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
                processContents(files, child);
            } else if ("compatible".equals(child.getName())) {
                // TODO
            } else if ("dependencies".equals(child.getName())) {
                processDeps(required, optional, child);
            } else if ("usesrole".equals(child.getName())) {
                // TODO
            } else if ("phprelease".equals(child.getName())) {
                // TODO
            } else if ("changelog".equals(child.getName())) {
                // TODO
            } else if ("extsrcrelease".equals(child.getName())) {
                // TODO
            } else if ("providesextension".equals(child.getName())) {
                this.providesExtension = child.getValue();
            } else if ("changelog".equals(child.getName())) {
                // TODO
            } else {
                throw new PhpCoreException("Unknown name in package.xml: " + child.getName());
            }
        }
    }

    private void fetchRelease(
            final List<IDependency> required,
            final List<IDependency> optional,
            final Map<String, List<String>> files,
            final Xpp3Dom child)
        throws PhpCoreException {
        for (final Xpp3Dom rchild : child.getChildren()) {
            if ("version".equals(rchild.getName())) {
                // TODO
            } else if ("date".equals(rchild.getName())) {
                // TODO
            } else if ("license".equals(rchild.getName())) {
                // TODO
            } else if ("state".equals(rchild.getName())) {
                // TODO
            } else if ("notes".equals(rchild.getName())) {
                // TODO
            } else if ("provides".equals(rchild.getName())) {
                // TODO
            } else if ("filelist".equals(rchild.getName())) {
                processContents(files, rchild);
            } else if ("deps".equals(rchild.getName())) {
                fetchOldDeps(required, optional, rchild);
            } else {
                throw new PhpCoreException("Unknown name in package.xml: " + rchild.getName());
            }
        }
    }

    /**
     * Fetches a file tag.
     * @param files files
     * @param dom the dom
     * @param baseInstallDir the base install dir
     * @param defaultRole the default role
     * @param namePrefix
     */
    private void fetchFile(
            Map<String, List<String>> files, Xpp3Dom dom, String baseInstallDir,
            String defaultRole, String namePrefix)
        throws PhpCoreException {
        // TODO parse children:
        // <file baseinstalldir="HTML/QuickForm" md5sum="dd09365612fce002f74e0a3489f3b0de" name="advmultiselect.php" role="php">
        //    <tasks:replace from="@package_version@" to="version" type="package-info" />
        // </file>
        final String role = dom.getAttribute("role") == null ? defaultRole : dom.getAttribute("role");
        final String fname = dom.getAttribute("name");
        final String dir = dom.getAttribute("baseinstalldir");
        final String basedir = dir == null ? (baseInstallDir == null ? "" : baseInstallDir + "/") : dir + "/";
        final String path = namePrefix + basedir + fname;
        if (role == null) {
            throw new PhpCoreException("Unknown file role: " + fname);
        }
        List<String> filesList = files.get(role);
        if (filesList == null) {
            filesList = new ArrayList<String>();
            files.put(role, filesList);
        }
        filesList.add(path);
    }

    /**
     * Fetches a dir tag.
     * @param files files
     * @param dom the dom
     * @param baseInstallDir the base install dir
     * @param defaultRole the default role
     * @param namePrefix
     */
    private void fetchDir(
            Map<String, List<String>> files, Xpp3Dom dom, String baseInstallDir,
            String defaultRole, String namePrefix)
        throws PhpCoreException {
        final String role = dom.getAttribute("role") == null ? defaultRole : dom.getAttribute("role");
        final String fname = dom.getAttribute("name");
        final String dir = dom.getAttribute("baseinstalldir");
        final String basedir = dir == null ? baseInstallDir : dir;
        for (final Xpp3Dom fchild : dom.getChildren()) {
            if ("dir".equals(fchild.getName())) {
                fetchDir(files, fchild, basedir, role, namePrefix + fname + "/");
            } else if ("file".equals(fchild.getName())) {
                fetchFile(files, fchild, basedir, role, namePrefix + fname + "/");
            } else {
                throw new PhpCoreException("Unknown name in package.xml: " + fchild.getName());
            }
        }
    }

    private void fetchMaintainers(final List<IMaintainer> maintain, final Xpp3Dom child) throws PhpCoreException {
        for (final Xpp3Dom mchild : child.getChildren()) {
            final IMaintainer maintainer = new Maintainer();
            if ("maintainer".equals(mchild.getName())) {
                for (final Xpp3Dom mmchild : mchild.getChildren()) {
                    fetchMaintainer(maintainer, mmchild);
                }
            } else {
                throw new PhpCoreException("Unknown name in package.xml: " + mchild.getName());
            }
            maintain.add(maintainer);
        }
    }

    private void fetchHelper(final List<IMaintainer> maintain, final Xpp3Dom child) throws PhpCoreException {
        final IMaintainer maintainer = new Maintainer();
        maintainer.setRole("helper");
        for (final Xpp3Dom mmchild : child.getChildren()) {
            fetchMaintainer(maintainer, mmchild);
        }
        maintain.add(maintainer);
    }

    private void fetchContributor(final List<IMaintainer> maintain, final Xpp3Dom child) throws PhpCoreException {
        final IMaintainer maintainer = new Maintainer();
        maintainer.setRole("constributor");
        for (final Xpp3Dom mmchild : child.getChildren()) {
            fetchMaintainer(maintainer, mmchild);
        }
        maintain.add(maintainer);
    }

    private void fetchDeveloper(final List<IMaintainer> maintain, final Xpp3Dom child) throws PhpCoreException {
        final IMaintainer maintainer = new Maintainer();
        maintainer.setRole("developer");
        for (final Xpp3Dom mmchild : child.getChildren()) {
            fetchMaintainer(maintainer, mmchild);
        }
        maintain.add(maintainer);
    }

    private void fetchLead(final List<IMaintainer> maintain, final Xpp3Dom child) throws PhpCoreException {
        final IMaintainer maintainer = new Maintainer();
        maintainer.setRole("lead");
        for (final Xpp3Dom mmchild : child.getChildren()) {
            fetchMaintainer(maintainer, mmchild);
        }
        maintain.add(maintainer);
    }

    /**
     * processes the file contents node.
     * @param files files
     * @param child child dom node
     * @throws PhpCoreException 
     */
    private void processContents(Map<String, List<String>> files, Xpp3Dom child) throws PhpCoreException {
        for (final Xpp3Dom fchild : child.getChildren()) {
            if ("file".equals(fchild.getName())) {
                this.fetchFile(files, fchild, null, null, "");
            } else if ("dir".equals(fchild.getName())) {
                this.fetchDir(files, fchild, null, null, "");
            } else {
                throw new PhpCoreException("Unknown name in package.xml: " + fchild.getName());
            }
        }
    }

    private void processDeps(final List<IDependency> required,
            final List<IDependency> optional, final Xpp3Dom child)
            throws PhpException, PhpCoreException {
        for (final Xpp3Dom depChild : child.getChildren()) {
            if ("optional".equals(depChild.getName())) {
                this.initializeDeps(optional, depChild);
            } else if ("required".equals(depChild.getName())) {
                this.initializeDeps(required, depChild);
            } else if ("group".equals(depChild.getName())) {
                // TODO better support for group dependencies.
                this.initializeDeps(optional, depChild);
            } else {
                throw new PhpCoreException(
                        "Unknown name in package.xml->dependencies: " + depChild.getName());
            }
        }
    }

    private void fetchMaintainer(final IMaintainer maintainer,
            final Xpp3Dom mmchild) throws PhpCoreException {
        if ("user".equals(mmchild.getName())) {
            maintainer.setNick(mmchild.getValue());
        } else if ("name".equals(mmchild.getName())) {
            maintainer.setName(mmchild.getValue());
        } else if ("email".equals(mmchild.getName())) {
            maintainer.setEMail(mmchild.getValue());
        } else if ("url".equals(mmchild.getName())) {
            maintainer.setUrl(mmchild.getValue());
        } else if ("role".equals(mmchild.getName())) {
            maintainer.setRole(mmchild.getValue());
        } else if ("active".equals(mmchild.getName())) {
            maintainer.setActive("1".equals(mmchild.getValue())
                    || "yes".equalsIgnoreCase(mmchild.getValue()));
        } else {
            throw new PhpCoreException("Unknown name in package.xml: " + mmchild.getName());
        }
    }

    private void fetchOldDeps(final List<IDependency> required, final List<IDependency> optional, final Xpp3Dom rchild) throws PhpCoreException {
        for (final Xpp3Dom dchild : rchild.getChildren()) {
            if ("dep".equals(dchild.getName())) {
                // TODO
                // <dep type="pkg" rel="ge" version="1.0.0">Foo</dep>
                // <dep type="pkg" rel="le" version="1.9.0">Foo</dep> 
                // <package>
                //   <name>Foo</name>
                //   <channel>pear.php.net</channel>
                //   <min>1.0.0</min>
                //   <max>1.9.0</max>
                // </package> 
                final Set<String> packages = new HashSet<String>();
                final boolean isOptional = "yes".equalsIgnoreCase(dchild.getAttribute("optional"));
                final String type = dchild.getAttribute("type");
                final String rel = dchild.getAttribute("rel");
                final String version = dchild.getAttribute("version");
                final String pkg = dchild.getValue();
                final IDependency dep = new Dependency();
                if (isOptional) {
                    optional.add(dep);
                } else {
                    required.add(dep);
                }
                dep.setChannelName(this.pearChannel.getName());
                dep.setPackageName(pkg);
                if (type.equals("php")) {
                    dep.setType(DependencyType.PHP);
                } else if (type.equals("pkg")) {
                    dep.setType(DependencyType.PACKAGE);
                } else if (type.equals("ext")) {
                    dep.setType(DependencyType.PHP_EXTENSION);
                } else {
                    throw new PhpCoreException("Unknown type in package.xml: " + type);
                }
                if ("has".equals(rel)) {
                    dep.setMin(version);
                    dep.setMax(version);
                    dep.setMinExcluded(false);
                    dep.setMaxExcluded(false);
                } else if ("eq".equals(rel)) {
                    dep.setMin(version);
                    dep.setMax(version);
                    dep.setMinExcluded(false);
                    dep.setMaxExcluded(false);
                } else if ("ge".equals(rel)) {
                    dep.setMin(version);
                    dep.setMinExcluded(false);
                } else if ("gt".equals(rel)) {
                    dep.setMin(version);
                    dep.setMinExcluded(true);
                } else if ("le".equals(rel)) {
                    dep.setMax(version);
                    dep.setMaxExcluded(false);
                } else if ("lt".equals(rel)) {
                    dep.setMax(version);
                    dep.setMaxExcluded(true);
                } else if ("not".equals(rel)) {
                    // TODO
                    throw new IllegalStateException("Not is currently not supported");
                } else {
                    throw new PhpCoreException("Unknown rel in package.xml: " + rel);
                }
            } else {
                throw new PhpCoreException("Unknown name in package.xml: " + dchild.getName());
            }
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
                final IDependency dep = new Dependency();
                dep.setType(DependencyType.PEARINSTALLER);
                fetchDep(child, dep);
                depsArray.add(dep);
            } else if ("php".equals(child.getName())) {
                final IDependency dep = new Dependency();
                dep.setType(DependencyType.PHP);
                fetchDep(child, dep);
                depsArray.add(dep);
            } else if ("subpackage".equals(child.getName())) {
                final IDependency dep = new Dependency();
                dep.setType(DependencyType.SUBPACKAGE);
                fetchDep(child, dep);
                depsArray.add(dep);
            } else if ("package".equals(child.getName())) {
                final IDependency dep = new Dependency();
                dep.setType(DependencyType.PACKAGE);
                fetchDep(child, dep);
                depsArray.add(dep);
            } else if ("extension".equals(child.getName())) {
                final IDependency dep = new Dependency();
                dep.setType(DependencyType.PHP_EXTENSION);
                fetchDep(child, dep);
                depsArray.add(dep);
            } else {
                throw new PhpCoreException("Unknown name in package.xml->dependencies: " + child.getName());
            }
        }
    }

    private void fetchDep(final Xpp3Dom child, final IDependency dep)
            throws PhpCoreException {
        for (final Xpp3Dom dchild : child.getChildren()) {
            if ("name".equals(dchild.getName())) {
                dep.setPackageName(dchild.getValue());
            } else if ("channel".equals(dchild.getName())) {
                dep.setChannelName(dchild.getValue());
            } else if ("min".equals(dchild.getName())) {
                dep.setMin(dchild.getValue());
            } else if ("max".equals(dchild.getName())) {
                dep.setMax(dchild.getValue());
            } else if ("providesextension".equals(dchild.getName())) {
                // TODO  
            } else if ("recommended".equals(dchild.getName())) {
                // TODO
            } else if ("conflicts".equals(dchild.getName())) {
                // TODO
            } else if ("exclude".equals(dchild.getName())) {
                // TODO assert that min/max equals child.getValue()
                dep.setMaxExcluded(true);
                dep.setMinExcluded(true);
            } else {
                throw new PhpCoreException("Unknown name in package.xml->dependencies: " + dchild.getName());
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<IMaintainer> getMaintainers() throws PhpException {
        this.initializeExtendedData();
        return this.maintainers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addMaintainer(IMaintainer maintainer) throws PhpException {
        this.initializeExtendedData();
        this.maintainers.add(maintainer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void install() throws PhpException {
        this.pearChannel.getPackage(this.getPackageName()).install(this, true, true, true);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public Iterable<String> getPhpFiles() throws PhpException {
        this.initializeExtendedData();
        if (this.fileContents.containsKey("php")) {
            return this.fileContents.get("php");
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String providesExtension() throws PhpException {
        this.initializeExtendedData();
        return this.providesExtension;
    }

    @Override
    public IPackage getPackage() {
        try {
            return this.pearChannel.getPackage(this.name);
        } catch (PhpException ex) {
            // should never happen
            throw new IllegalStateException(ex);
        }
    }

}
