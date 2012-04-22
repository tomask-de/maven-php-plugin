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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.codehaus.plexus.util.xml.XmlStreamReader;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.phpmaven.exec.PhpCoreException;
import org.phpmaven.exec.PhpException;
import org.phpmaven.pear.IMaintainer;
import org.phpmaven.pear.IPackage;
import org.phpmaven.pear.IPackageVersion;
import org.phpmaven.pear.IPearChannel;
import org.phpmaven.pear.IPearUtility;
import org.phpmaven.pear.IVersion;
import org.sonatype.aether.util.version.GenericVersionScheme;
import org.sonatype.aether.version.InvalidVersionSpecificationException;

/**
 * Package implementation.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public class Package implements IPackage {
    
    /** the generic version scheme. */
    private static final GenericVersionScheme SCHEME = new GenericVersionScheme();
    
    /** package name. */
    private String name;
    
    /** installed version. */
    private IPackageVersion installedVersion;
    
    /**
     * true if the package is initialized.
     */
    private boolean packageInitialized;

    /**
     * The pear utility.
     */
    private IPearUtility pearUtility;

    /**
     * The pear channel.
     */
    private IPearChannel pearChannel;

    /**
     * The license.
     */
    private String license;

    /**
     * Package summary.
     */
    private String summary;
    
    /**
     * Package description.
     */
    private String description;
    
    /**
     * The maintainers.
     */
    private List<IMaintainer> maintainers;
    
    /**
     * The known package versions.
     */
    private Map<String, IPackageVersion> versions;

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
    public IPackageVersion getInstalledVersion() throws PhpException {
        return this.installedVersion;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInstalledVersion(IPackageVersion version) {
        this.installedVersion = version;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(IPearUtility utility, IPearChannel channel) {
        if (this.pearUtility != null) {
            throw new IllegalStateException("Must not be called twice.");
        }
        this.pearUtility = utility;
        this.pearChannel = channel;
    }
    
    /**
     * Initializes this package.
     * @throws PhpException thrown on execution errors.
     */
    private void initializeData() throws PhpException {
        if (!this.packageInitialized) {
            try {
                final String channelXml = Helper.getTextFileContents(
                        this.pearChannel.getRestUrl(IPearChannel.REST_1_0),
                        "p/" + this.getPackageName().toLowerCase() + "/info.xml");
                final Xpp3Dom dom = Xpp3DomBuilder.build(
                        new XmlStreamReader(new ByteArrayInputStream(channelXml.getBytes())));
                
                final Xpp3Dom licenseNode = dom.getChild("l");
                if (licenseNode != null) {
                    this.license = licenseNode.getValue();
                }
                
                final Xpp3Dom summaryNode = dom.getChild("s");
                if (summaryNode != null) {
                    this.summary = summaryNode.getValue();
                }
                
                final Xpp3Dom descriptionNode = dom.getChild("d");
                if (descriptionNode != null) {
                    this.description = descriptionNode.getValue();
                }
            } catch (IOException ex) {
                throw new PhpCoreException("Problems reading info.xml", ex);
            } catch (XmlPullParserException ex) {
                throw new PhpCoreException("Problems reading info.xml", ex);
            }
            this.packageInitialized = true;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void install(IPackageVersion version, boolean forceUninstall,
            boolean forceInstall, boolean ignoreDeps) throws PhpException {
        this.initializeVersions();
        if (!this.versions.containsValue(version)) {
            throw new PhpCoreException(
                    "Only allowed to install known versions. " +
                    version.getVersion().getPearVersion() +
                    " is not known.");
        }
        
        if (this.installedVersion != null) {
            if (version.getVersion() == null
                 || this.installedVersion.getVersion().getPearVersion().equals(
                         version.getVersion().getPearVersion())) {
                // already installed
                return;
            }
            if (forceUninstall) {
                this.uninstall(true);
            }
        }
        
        final String cmd = "install " +
                (ignoreDeps ? "--loose " : "") +
                (forceInstall ? "--force " : "") + 
                "--alldeps " +
                this.pearChannel.getName() + "/" +
                this.getPackageName() + "-" + 
                version.getVersion().getPearVersion();
        final String result = this.pearUtility.executePearCmd(cmd);
        // TODO Parse result
        this.installedVersion = version;
    }

    /**
     * Initializes the versions.
     * @throws PhpException thrown opn php execution errors.
     */
    private void initializeVersions() throws PhpException {
        if (this.versions == null) {
            final Map<String, IPackageVersion> vMap = new TreeMap<String, IPackageVersion>(
                    new PearVersionComparator());
            try {
                Xpp3Dom dom = null;
                try {
                    final String channelXml = Helper.getTextFileContents(
                            this.pearChannel.getRestUrl(IPearChannel.REST_1_3),
                            "r/" + this.getPackageName().toLowerCase() + "/allreleases2.xml");
                    dom = Xpp3DomBuilder.build(
                            new XmlStreamReader(new ByteArrayInputStream(channelXml.getBytes())));
                } catch (IOException ex) {
                    // may be caused by older REST versions; fallback to allreleases.xml
                    final String channelXml = Helper.getTextFileContents(
                            this.pearChannel.getRestUrl(IPearChannel.REST_1_0),
                            "r/" + this.getPackageName().toLowerCase() + "/allreleases.xml");
                    dom = Xpp3DomBuilder.build(
                            new XmlStreamReader(new ByteArrayInputStream(channelXml.getBytes())));
                }
                
                for (final Xpp3Dom child : dom.getChildren("r")) {
                    final String vName = child.getChild("v").getValue();
                    final IPackageVersion pkgVersion = new PackageVersion();
                    pkgVersion.initialize(this.pearUtility, this.pearChannel, this);
                    final IVersion version = new Version();
                    pkgVersion.setVersion(version);
                    pkgVersion.setPackageName(this.getPackageName());
                    version.setPearVersion(vName);
                    
                    final Xpp3Dom stabilityNode = child.getChild("c");
                    if (stabilityNode != null) {
                        pkgVersion.setStability(stabilityNode.getValue());
                    }
                    
                    final Xpp3Dom minPhpNode = child.getChild("m");
                    if (minPhpNode != null) {
                        pkgVersion.setMinPhpVersion(minPhpNode.getValue());
                    }
                    
                    vMap.put(vName, pkgVersion);
                }
                
                final Xpp3Dom licenseNode = dom.getChild("l");
                if (licenseNode != null) {
                    this.license = licenseNode.getValue();
                }
                
                final Xpp3Dom summaryNode = dom.getChild("s");
                if (summaryNode != null) {
                    this.summary = summaryNode.getValue();
                }
                
                final Xpp3Dom descriptionNode = dom.getChild("d");
                if (descriptionNode != null) {
                    this.description = descriptionNode.getValue();
                }
            } catch (IOException ex) {
                throw new PhpCoreException("Problems reading allreleases.xml", ex);
            } catch (XmlPullParserException ex) {
                throw new PhpCoreException("Problems reading allreleases.xml", ex);
            }
            this.versions = vMap;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<IPackageVersion> getKnownVersions() throws PhpException {
        this.initializeVersions();
        return this.versions.values();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void uninstall(boolean ignoreDeps) throws PhpException {
        if (this.installedVersion == null) {
            return;
        }
        
        final String cmd = "uninstall " +
                (ignoreDeps ? "--nodeps " : "") +
                this.pearChannel.getName() + "/" +
                this.getPackageName() + "-" + 
                this.installedVersion.getVersion().getPearVersion();
        final String result = this.pearUtility.executePearCmd(cmd);
        // TODO Parse result
        this.installedVersion = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLicense() throws PhpException {
        this.initializeData();
        return this.license;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLicense(String license) throws PhpException {
        this.initializeData();
        this.license = license;
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
     * Initializes the maintainers.
     * @throws PhpException thrown if the php execution fails.
     */
    private void initMaintainers() throws PhpException {
        if (this.maintainers == null) {
            final List<IMaintainer> ms = new ArrayList<IMaintainer>();
            
            try {
                final String channelXml = Helper.getTextFileContents(
                        this.pearChannel.getRestUrl(IPearChannel.REST_1_2),
                        "p/" + this.getPackageName().toLowerCase() + "/maintainers2.xml");
                final Xpp3Dom dom = Xpp3DomBuilder.build(
                        new XmlStreamReader(new ByteArrayInputStream(channelXml.getBytes())));
                
                for (final Xpp3Dom child : dom.getChildren("m")) {
                    final IMaintainer mt = new Maintainer();
                    mt.setName(child.getChild("h").getValue());
                    mt.setActive("1".equals(child.getChild("a").getValue()) ||
                            "yes".equalsIgnoreCase(child.getChild("a").getValue()));
                    mt.setRole(child.getChild("r").getValue());
                    ms.add(mt);
                }
            } catch (IOException ex) {
                throw new PhpCoreException("Problems reading maintainers2.xml", ex);
            } catch (XmlPullParserException ex) {
                throw new PhpCoreException("Problems reading maintainers2.xml", ex);
            }
            
            this.maintainers = ms;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<IMaintainer> getMaintainers() throws PhpException {
        this.initMaintainers();
        return this.maintainers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addMaintainer(IMaintainer maintainer) throws PhpException {
        this.initMaintainers();
        this.maintainers.add(maintainer);
    }
    
    
    /**
     * Converts a maven version to a pear version.
     * @param src maven version
     * @return pear version
     */
    public static String convertMavenVersionToPearVersion(String src) {
        int pos = 0;
        final StringBuffer sb = new StringBuffer();
        final char[] chars = src.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            final char c = chars[i];
            if (pos == 3) {
                if (c != '-') {
                    sb.append(c);
                }
            } else if (Character.isDigit(c)) {
                sb.append(c);
            } else if (c == '.') {
                sb.append(c);
                pos++;
            } else if (c == '-') {
                pos = 3;
                if ("-alpha".equals(src.substring(i, i + 6))) {
                    sb.append("a");
                    i += 6;
                } else if ("-beta".equals(src.substring(i, i + 5))) {
                    sb.append("b");
                    i += 5;
                }
            } else {
                throw new IllegalStateException("Invalid maven version: " + src);
            }
        }
        return sb.toString();
    }
    
    /**
     * Converts a pear version to a maven version.
     * @param src pear version.
     * @return maven version.
     */
    public static String convertPearVersionToMavenVersion(String src) {
        int pos = 0;
        final StringBuffer sb = new StringBuffer();
        final char[] chars = src.toCharArray();
        boolean startingExtra = true;
        for (int i = 0; i < chars.length; i++) {
            final char c = chars[i];
            if (startingExtra && (c == 'a' || c == 'A')) {
                if (pos != 3) {
                    sb.append("-");
                    pos = 3;
                }
                if (i < chars.length - 4 && "alpha".equalsIgnoreCase(src.substring(i, i + 5))) {
                    i += 4;
                }
                if (i + 1 == chars.length) {
                    sb.append("alpha");
                } else if (Character.isDigit(chars[i + 1])) {
                    sb.append("alpha-");
                } else {
                    sb.append(c);
                }
                startingExtra = false;
            } else if (startingExtra && (c == 'b' || c == 'B')) {
                if (pos != 3) {
                    sb.append("-");
                    pos = 3;
                }
                if (i < chars.length - 3 && "beta".equalsIgnoreCase(src.substring(i, i + 4))) {
                    i += 3;
                }
                if (i + 1 == chars.length) {
                    sb.append("beta");
                } else if (Character.isDigit(chars[i + 1])) {
                    sb.append("beta-");
                } else {
                    sb.append(c);
                }
                startingExtra = false;
            } else if (pos == 3) {
                startingExtra = false;
                sb.append(c);
            } else if (Character.isDigit(c)) {
                sb.append(c);
            } else if (c == '.') {
                sb.append(c);
                pos++;
            } else if (c == '-') {
                sb.append("-");
                pos = 3;
            } else {
                pos = 3;
                sb.append("-");
                sb.append(c);
            }
        }
        return sb.toString();
    }
    
    /**
     * A comparator for pear versions.
     */
    private static final class PearVersionComparator implements Comparator<String> {

        /**
         * {@inheritDoc}
         */
        @Override
        public int compare(String o1, String o2) {
            try {
                final org.sonatype.aether.version.Version v1 =
                        SCHEME.parseVersion(convertPearVersionToMavenVersion(o1));
                final org.sonatype.aether.version.Version v2 =
                        SCHEME.parseVersion(convertPearVersionToMavenVersion(o2));
                return v1.compareTo(v2);
            } catch (InvalidVersionSpecificationException ex) {
                // ignore
                return o1.compareTo(o2);
            } catch (IllegalStateException ex) {
                // ignore
                return o1.compareTo(o2);
            }
        }
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPackageVersion getVersion(String pearVersion) throws PhpException {
        this.initializeVersions();
        return this.versions.get(pearVersion);
    }

    @Override
    public IPearChannel getChannel() {
        return this.pearChannel;
    }

}
