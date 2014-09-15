/**
 * Copyright 2010-2012 by PHP-maven.org
 * 
 * This file is part of pear-java.
 *
 * pear-java is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * pear-java is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with pear-java.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.phpmaven.pear.library.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.codehaus.plexus.util.xml.XmlStreamReader;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.phpmaven.pear.library.ICategory;
import org.phpmaven.pear.library.IMaintainer;
import org.phpmaven.pear.library.IPackage;
import org.phpmaven.pear.library.IPackageVersion;
import org.phpmaven.pear.library.IPearChannel;
import org.phpmaven.pear.library.IPearUtility;
import org.phpmaven.pear.library.IRestBaseUrl;
import org.phpmaven.pear.library.IRestServer;
import org.phpmaven.pear.library.IServer;
import org.phpmaven.pear.library.ISoapFunction;
import org.phpmaven.pear.library.ISoapServer;
import org.phpmaven.pear.library.IXmlRpcFunction;
import org.phpmaven.pear.library.IXmlRpcServer;
import org.phpmaven.phpexec.library.PhpCoreException;
import org.phpmaven.phpexec.library.PhpException;

/**
 * Implementation of a pear channel.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 0.1.0
 */
public class PearChannel implements IPearChannel {

    /**
     * The channel name.
     */
    private String name;
    
    /**
     * The channel alias.
     */
    private String alias;
    
    /**
     * The summary.
     */
    private String summary;

    /**
     * The validation package.
     */
    private IPackageVersion validationPackage;

    /**
     * The primary server.
     */
    private IServer primaryServer;

    /**
     * List of mirrors.
     */
    private List<IServer> mirrors = new ArrayList<IServer>();

    /**
     * The pear utility.
     */
    private IPearUtility pearUtility;
    
    /** known packages. */
    private Map<String, IPackage> knownPackages;
    
    /** installed packages. */
    private List<IPackage> installedPackages;

    /**
     * The categories.
     */
    private List<ICategory> categories;
    
    /**
     * The maintainers.
     */
    private List<IMaintainer> maintainers;
    
    /**
     * The uri we used to read/intialize the channel.
     */
    private String uri;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSuggestedAlias() {
        return this.alias;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSuggestedAlias(String suggestedAlias) {
        this.alias = suggestedAlias;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSummary() {
        return this.summary;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPackageVersion getValidationPackage() {
        return this.validationPackage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValidationPackage(IPackageVersion version) {
        this.validationPackage = version;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IServer getPrimaryServer() {
        return this.primaryServer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPrimaryServer(IServer server) {
        this.primaryServer = server;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<IServer> getMirrors() {
        return this.mirrors;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addMirror(IServer server) {
        this.mirrors.add(server);
    }

    /**
     * Initializes the channel.
     * @param ut utility
     * @param n channel name
     * @param a alias
     * @param s summary
     * @throws PhpException thrown on initialization errors.
     */
    public void initialize(PearUtility ut, String n, String a, String s) throws PhpException {
        if (this.pearUtility != null) {
            throw new IllegalStateException("Must not be called twice.");
        }
        this.pearUtility = ut;
        this.uri = n;
        this.setName(n);
        this.alias = a;
        this.summary = s;
        this.installedPackages = new ArrayList<IPackage>();
        this.knownPackages = new HashMap<String, IPackage>();
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public void initialize(IPearUtility utility, String channelName) throws PhpCoreException {
        if (this.pearUtility != null) {
            throw new IllegalStateException("Must not be called twice.");
        }
        
        try {
            final String channelXml = Helper.getTextFileContents(channelName, "channel.xml");
            final Xpp3Dom dom = Xpp3DomBuilder.build(
                    new XmlStreamReader(new ByteArrayInputStream(channelXml.getBytes())));

            this.pearUtility = utility;
            this.uri = channelName;
            this.setName(channelName);
            
            for (final Xpp3Dom child : dom.getChildren()) {
                if ("name".equals(child.getName())) {
                    this.setName(child.getValue());
                } else if ("suggestedalias".equals(child.getName())) {
                    this.alias = child.getValue();
                } else if ("summary".equals(child.getName())) {
                    this.summary = child.getValue();
                } else if ("validatepackage".equals(child.getName())) {
                    // TODO
//                    this.validationPackage = x;
                } else if ("servers".equals(child.getName())) {
                    this.parseServer(child);
                } else {
                    throw new PhpCoreException("Problems reading channel.xml. Unknown node: " + child.getName());
                }
            }
        } catch (IOException ex) {
            throw new PhpCoreException("Problems reading channel.xml", ex);
        } catch (XmlPullParserException ex) {
            throw new PhpCoreException("Problems reading channel.xml", ex);
        }
    }

    /**
     * Parses the server dom node and read the server configuration of a channel.
     * @param domNode dom node.
     * @throws PhpCoreException thrown on xml errors.
     */
    private void parseServer(Xpp3Dom domNode) throws PhpCoreException {
        for (final Xpp3Dom child : domNode.getChildren()) {
            if ("primary".equals(child.getName())) {
                this.primaryServer = this.createServer(child);
            } else if ("mirror".equals(child.getName())) {
                this.mirrors.add(this.createServer(child));
            } else {
                throw new PhpCoreException("Problems reading channel.xml. Unknown node: " + child.getName());
            }
        }
    }

    /**
     * Parses the server dom node and read the server configuration of a channel.
     * @param domNode dom node.
     * @return the server configuration.
     */
    private IServer createServer(Xpp3Dom domNode) {
        final IServer result = this.getPearUtility().createServer();
        
        final String strPort = domNode.getAttribute("port");
        if (strPort != null) {
            result.setPort(Integer.parseInt(strPort));
        }
        final String strSsl = domNode.getAttribute("ssl");
        if (strSsl != null) {
            result.setSsl("yes".equals(strSsl));
        }
        final String strServer = domNode.getAttribute("server");
        if (strServer != null) {
            result.setServerName(strServer);
        }
        
        final Xpp3Dom xmlRpcNode = domNode.getChild("xmlrpc");
        if (xmlRpcNode != null) {
            final IXmlRpcServer xmlRpcServer = this.getPearUtility().createXmlRpcServer();
            final String path = xmlRpcNode.getAttribute("path");
            if (path != null) {
                xmlRpcServer.setPath(path);
            }
            
            for (final Xpp3Dom functionNode : xmlRpcNode.getChildren("function")) {
                final IXmlRpcFunction function = this.getPearUtility().createXmlRpcServerFunction();
                function.setVersion(functionNode.getAttribute("version"));
                function.setFunctionName(function.getVersion());
                xmlRpcServer.addFunction(function);
            }
            result.setXmlRpc(xmlRpcServer);
        }
        
        final Xpp3Dom restNode = domNode.getChild("rest");
        if (restNode != null) {
            final IRestServer restServer = this.getPearUtility().createRestServer();
            for (final Xpp3Dom baseUrlNode : restNode.getChildren("baseurl")) {
                final IRestBaseUrl url = this.getPearUtility().createRestBaseUrl();
                url.setRestVersion(baseUrlNode.getAttribute("type"));
                url.setBaseUrl(baseUrlNode.getValue());
                restServer.addBaseUrl(url);
            }
            result.setRest(restServer);
        }
        
        final Xpp3Dom soapNode = domNode.getChild("soap");
        if (soapNode != null) {
            final ISoapServer soapServer = this.getPearUtility().createSoapServer();

            final String path = soapNode.getAttribute("path");
            if (path != null) {
                soapServer.setPath(path);
            }
            
            for (final Xpp3Dom functionNode : soapNode.getChildren("function")) {
                final ISoapFunction function = this.getPearUtility().createSoapFunction();
                function.setVersion(functionNode.getAttribute("version"));
                function.setFunctionName(function.getVersion());
                soapServer.addFunction(function);
            }
            result.setSoap(soapServer);
        }
        
        return result;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getRestUrl(String version) {
        IRestBaseUrl newest = null;
        for (final IRestBaseUrl url : this.primaryServer.getRest().getBaseUrls()) {
            if (version.equals(url.getRestVersion())) {
                return this.toAbsoluteUri(url.getBaseUrl());
            }
            if (newest == null) {
                newest = url;
            } else if (newest.getRestVersion().compareTo(url.getRestVersion()) < 0) {
                newest = url;
            }
        }
        return this.toAbsoluteUri(newest.getBaseUrl());
    }
    
    private String toAbsoluteUri(String localUri) {
        URI url;
        try {
            url = new URI(localUri);
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
        if (url.getScheme() != null) {
            return localUri;
        }
        if (localUri.startsWith("/")) {
            final int indexOf = this.uri.indexOf("/");
            if (indexOf == -1) {
                return this.uri + localUri;
            }
            return this.uri.substring(0, indexOf) + localUri;
        }
        return this.uri + "/" + localUri;
    }
    
    /**
     * Initializes the local packages.
     * 
     * @param ignoreUnresolvablePackages true to ignore unresolvable packages.
     * @param doNotReadInstalled true to not read local installed packages.
     * 
     * @throws PhpException thrown on php execution errors.
     */
    public void initializePackages(boolean ignoreUnresolvablePackages, boolean doNotReadInstalled)
        throws PhpException {
        if (this.installedPackages == null) {
            final List<IPackage> installed = new ArrayList<IPackage>();
            final Map<String, IPackage> allPackages = new HashMap<String, IPackage>();
            
            try {
                final String channelXml = Helper.getTextFileContents(
                        this.getRestUrl(REST_1_0), "p/packages.xml");
                final Xpp3Dom dom = Xpp3DomBuilder.build(
                        new XmlStreamReader(new ByteArrayInputStream(channelXml.getBytes())));
                
                for (final Xpp3Dom child : dom.getChildren("p")) {
                    final IPackage pkg = this.getPearUtility().createPackage();
                    pkg.setPackageName(child.getValue());
                    pkg.initialize(this.pearUtility, this);
                    if (ignoreUnresolvablePackages) {
                        // try to resolve. ignore package on errors.
                        try {
                            pkg.getKnownVersions();
                        } catch (PhpException ex) {
                            // ignore this package
                            continue;
                        }
                    }
                    allPackages.put(child.getValue(), pkg);
                }
            } catch (IOException ex) {
                throw new PhpCoreException("Problems reading packages.xml", ex);
            } catch (XmlPullParserException ex) {
                throw new PhpCoreException("Problems reading packages.xml", ex);
            }
            
            if (!doNotReadInstalled) {
                readInstalled(installed, allPackages);
            }
            
            this.knownPackages = allPackages;
            this.installedPackages = installed;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reinitializeInstalledPackages() throws PhpException {
        final List<IPackage> installed = new ArrayList<IPackage>();
        for (final IPackage pkg : installed) {
            pkg.setInstalledVersion(null);
        }
        readInstalled(installed, this.knownPackages);
    }

    /**
     * Reads the installed packages.
     * @param installed installed packages.
     * @param allPackages all packages.
     * @throws PhpException php exception
     */
    private void readInstalled(final List<IPackage> installed, final Map<String, IPackage> allPackages)
        throws PhpException {
        final String res = this.pearUtility.executePearCmd("list -c " + this.getName());
        final StringTokenizer tokenizer = new StringTokenizer(res, "\n");
        if (tokenizer.nextToken().trim().startsWith("INSTALLED PACKAGES")) {
            // skip header
            tokenizer.nextToken();
            tokenizer.nextToken();
            while (tokenizer.hasMoreTokens()) {
                final String t = tokenizer.nextToken().trim();
                final int indexOf = t.indexOf(" ");
                final String n = t.substring(0, indexOf);
                final IPackage pkg = allPackages.get(n);
                if (pkg == null) {
                    throw new PhpCoreException(
                            "Installed package " + n +
                            " not found in packages.xml of channel " + this.getName());
                }
                installed.add(pkg);
                final String t2 = t.substring(indexOf).trim();
                final String pearVersion = t2.substring(0, t2.indexOf(" ")).trim();
                pkg.setInstalledVersion(pkg.getVersion(pearVersion));
            }
        }
    }
    
    /**
     * Initialize the packages.
     * @throws PhpException thrown if php execution failes.
     */
    private void initPackages() throws PhpException {
        this.initializePackages(false, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<IPackage> getKnownPackages() throws PhpException {
        this.initPackages();
        return this.knownPackages.values();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<IPackage> getInstalledPackages() throws PhpException {
        this.initPackages();
        return this.installedPackages;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPackage getPackage(String n) throws PhpException {
        this.initPackages();
        return this.knownPackages.get(n);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPackage(IPackage pkg) throws PhpException {
        this.initPackages();
        this.knownPackages.put(pkg.getPackageName(), pkg);
        if (pkg.getInstalledVersion() != null) {
            this.installedPackages.add(pkg);
        }
    }
    
    /**
     * Initializes the categories.
     * @throws PhpException thrown if the php execution fails.
     */
    private void initCategories() throws PhpException {
        if (this.categories == null) {
            final List<ICategory> cats = new ArrayList<ICategory>();
            
            try {
                final String channelXml = Helper.getTextFileContents(
                        this.getRestUrl(REST_1_1), "c/categories.xml");
                final Xpp3Dom dom = Xpp3DomBuilder.build(
                        new XmlStreamReader(new ByteArrayInputStream(channelXml.getBytes())));
                
                for (final Xpp3Dom child : dom.getChildren("c")) {
                	final ICategory cat = this.getPearUtility().createCategory();
                    cat.setName(child.getValue());
                    cat.setHRef(child.getAttribute("href"));
                    cats.add(cat);
                }
            } catch (IOException ex) {
                throw new PhpCoreException("Problems reading categories.xml", ex);
            } catch (XmlPullParserException ex) {
                throw new PhpCoreException("Problems reading categories.xml", ex);
            }
            
            this.categories = cats;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<ICategory> getCategories() throws PhpException {
        this.initCategories();
        return this.categories;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addCategory(ICategory category) throws PhpException {
        this.initCategories();
        this.categories.add(category);
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
                        this.getRestUrl(REST_1_1), "m/allmaintainers.xml");
                final Xpp3Dom dom = Xpp3DomBuilder.build(
                        new XmlStreamReader(new ByteArrayInputStream(channelXml.getBytes())));
                
                for (final Xpp3Dom child : dom.getChildren("h")) {
                    final IMaintainer mt = this.getPearUtility().createMaintainer();
                    mt.setName(child.getValue());
                    ms.add(mt);
                }
            } catch (IOException ex) {
                throw new PhpCoreException("Problems reading allmaintainers.xml", ex);
            } catch (XmlPullParserException ex) {
                throw new PhpCoreException("Problems reading allmaintainers.xml", ex);
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

    @Override
    public IPearUtility getPearUtility() {
        return this.pearUtility;
    }

}
