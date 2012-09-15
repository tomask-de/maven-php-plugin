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

package org.phpmaven.pear.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.monitor.logging.DefaultLog;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.exec.PhpException;
import org.phpmaven.pear.IMaintainer;
import org.phpmaven.pear.IPackage;
import org.phpmaven.pear.IPackageVersion;
import org.phpmaven.pear.IPearChannel;
import org.phpmaven.pear.IPearConfiguration;
import org.phpmaven.pear.IPearUtility;
import org.phpmaven.plugin.build.FileHelper;
import org.phpmaven.test.AbstractTestCase;

/**
 * test cases for the pear support.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public class BaseTest extends AbstractTestCase {

    /**
     * Tests if the pear utility can be created.
     *
     * @throws Exception thrown on errors
     */
    public void testPUCreation() throws Exception {
        // look up the component factory
        final IComponentFactory factory = lookup(IComponentFactory.class);
        final MavenSession session = getSession();
        final IPearConfiguration pearConfig = factory.lookup(
                IPearConfiguration.class,
                IComponentFactory.EMPTY_CONFIG,
                session);
        // assert that it is not null
        assertNotNull(pearConfig);
        // assert that we are able to create the util
        final DefaultLog logger = new DefaultLog(new ConsoleLogger());
        final IPearUtility util = pearConfig.getUtility(logger);
        assertNotNull(util);
    }

    /**
     * Tests if the channel.xml can be read.
     *
     * @throws Exception thrown on errors
     */
    public void testChannelXml() throws Exception {
        final MavenSession session = getSession();
        final IPearUtility util = getPearUtility(session, false);
        
        final File pearFolder = new File(
                session.getCurrentProject().getBasedir(), 
                "pear.php.net");
        final IPearChannel channel = util.channelDiscoverLocal(pearFolder);
        assertNotNull(channel);
        assertNotNull(channel.getPrimaryServer());
        assertTrue(channel.getMirrors().iterator().hasNext());
        final String restServer = channel.getRestUrl(IPearChannel.REST_1_3);
        assertNotNull(restServer);
        assertEquals("file://" + pearFolder.getAbsolutePath() + "/rest/", restServer);
        assertEquals("pear.php.net", channel.getName());
        assertEquals("pear", channel.getSuggestedAlias());
        assertEquals("PHP Extension and Application Repository", channel.getSummary());
    }
    
    /**
     * Tests if the package infos can be fetched.
     *
     * @throws Exception thrown on errors
     */
    public void testKnownPackages() throws Exception {
        final IPearChannel channel = getChannel(false);
        
        channel.initializePackages(true, true);
        
        final Iterable<IPackage> pkgs = channel.getKnownPackages();
        assertNotNull(pkgs);
    }
    
    /**
     * Tests if the package infos can be fetched.
     *
     * @throws Exception thrown on errors
     */
    public void testChannelMaintainers() throws Exception {
        final IPearChannel channel = getChannel(false);
        
        final Iterable<IMaintainer> maintainers = channel.getMaintainers();
        assertNotNull(maintainers);
    }
    
    /**
     * Tests if the package infos can be fetched.
     *
     * @throws Exception thrown on errors
     */
    public void testInstalledPackages() throws Exception {
        final IPearChannel channel = getChannel(true);
        
        channel.initializePackages(true, false);
        
        final Iterable<IPackage> pkgs = channel.getInstalledPackages();
        assertNotNull(pkgs);
        
        final Iterator<IPackage> iter = pkgs.iterator();
        final IPackage pkgArchiveTar = iter.next();
        final IPackage pkgConsoleGetopt = iter.next();
        final IPackage pkgPEAR = iter.next();
        final IPackage pkgStructuresGraph = iter.next();
        final IPackage pkgXmlUtil = iter.next();
        if (iter.hasNext()) {
            final StringBuffer packages = new StringBuffer();
            packages.append(pkgArchiveTar.getPackageName() + "\n");
            packages.append(pkgConsoleGetopt.getPackageName() + "\n");
            packages.append(pkgPEAR.getPackageName() + "\n");
            packages.append(pkgStructuresGraph.getPackageName() + "\n");
            packages.append(pkgXmlUtil.getPackageName() + "\n");
            while (iter.hasNext()) {
                packages.append(iter.next().getPackageName() + "\n");
            }
            fail("Unexpected installed packages:\n" + packages);
        }
        
        assertEquals("Archive_Tar", pkgArchiveTar.getPackageName());
        assertNotNull(pkgArchiveTar.getInstalledVersion());
        assertEquals("1.3.7", pkgArchiveTar.getInstalledVersion().getVersion().getPearVersion());
        
        assertEquals("Console_Getopt", pkgConsoleGetopt.getPackageName());
        assertNotNull(pkgConsoleGetopt.getInstalledVersion());
        assertEquals("1.3.0", pkgConsoleGetopt.getInstalledVersion().getVersion().getPearVersion());
        
        assertEquals("PEAR", pkgPEAR.getPackageName());
        assertNotNull(pkgPEAR.getInstalledVersion());
        assertEquals("1.9.4", pkgPEAR.getInstalledVersion().getVersion().getPearVersion());
        
        assertEquals("Structures_Graph", pkgStructuresGraph.getPackageName());
        assertNotNull(pkgStructuresGraph.getInstalledVersion());
        assertEquals("1.0.4", pkgStructuresGraph.getInstalledVersion().getVersion().getPearVersion());
        
        assertEquals("XML_Util", pkgXmlUtil.getPackageName());
        assertNotNull(pkgXmlUtil.getInstalledVersion());
        assertEquals("1.2.1", pkgXmlUtil.getInstalledVersion().getVersion().getPearVersion());
    }
    
    /**
     * Tests if the package infos can be fetched.
     *
     * @throws Exception thrown on errors
     */
    public void testKnownPackagesFailed() throws Exception {
        final IPearChannel channel = getChannel(true);
        
        channel.getKnownPackages();
        final IPackage pkg = channel.getPackage("Net_SSH2");
        assertNotNull(pkg);
        
        try {
            // will fail because the package cannot be read
            pkg.getKnownVersions();
            fail("Expected failures because not all packages are available");
        } catch (PhpException ex) {
            // succeeds
        }
    }
    
    /**
     * Tests the versions.
     * 
     * @throws Exception
     */
    public void testVersions() throws Exception {
        final IPearChannel channel = getChannel(false);
        
        channel.initializePackages(true, true);
        
        final IPackage pkg = channel.getPackage("Archive_Tar");
        assertNotNull(pkg.getVersion("1.3.9"));
        assertNotNull(pkg.getVersion("1.3.8"));
        assertNotNull(pkg.getVersion("1.3.7"));
        assertNotNull(pkg.getVersion("1.3.6"));
        assertNotNull(pkg.getVersion("1.3.5"));
        assertNotNull(pkg.getVersion("1.3.4"));
        assertNotNull(pkg.getVersion("1.3.3"));
        assertNotNull(pkg.getVersion("1.3.2"));
        assertNotNull(pkg.getVersion("1.3.1"));
        assertNotNull(pkg.getVersion("1.3.0"));
        assertNotNull(pkg.getVersion("1.2"));
        assertNotNull(pkg.getVersion("1.1"));
        assertNotNull(pkg.getVersion("1.0"));
        assertNotNull(pkg.getVersion("0.10-b1"));
        assertNotNull(pkg.getVersion("0.9"));
        assertNotNull(pkg.getVersion("0.4"));
        assertNotNull(pkg.getVersion("0.3"));
    }
    
    /**
     * Tests the package version data.
     * 
     * @throws Exception
     */
    public void testVersionData() throws Exception {
        final IPearChannel channel = getChannel(false);
        
        channel.initializePackages(true, true);
        
        IPackage pkg = channel.getPackage("Archive_Tar");
        IPackageVersion version = pkg.getVersion("0.3");
        assertEquals("Tar file management class", version.getSummary());
        assertEquals(
                "This class provides handling of tar files in PHP.\n" +
                "It supports creating, listing, extracting and adding to tar files.\n" +
                "Gzip support is available if PHP has the zlib extension built-in or\n" +
                "loaded.", version.getDescription());
        
        pkg = channel.getPackage("PHPUnit");
        version = pkg.getVersion("1.3.2");
        assertEquals(
                "! Changed license from PHP License to BSD Style License.",
                version.getReleaseNotes());
        assertEquals("2005-11-10 00:00:00", version.getReleaseDate());
        // TODO assertEquals("BSD License", version.getLicense());
        assertEquals("stable", version.getStability());
        assertNotNull(version.getMaintainers());
        assertTrue(version.getMaintainers().iterator().hasNext());
    }
    
    /**
     * Tests if the packages can be installed and the files can be found.
     * @throws Exception exception
     */
    public void testPackageInstallation() throws Exception {
        final IPearChannel channel = getChannel(true);
        final IPackage pkg = channel.getPackage("Config");
        final IPackageVersion version = pkg.getVersion("0.3.1");
        assertNull(pkg.getInstalledVersion());
        version.install();
        assertEquals("0.3.1", pkg.getInstalledVersion().getVersion().getMavenVersion());
        
        assertTrue(
                channel.getPearUtility().getPhpDir().getAbsolutePath().startsWith(
                        channel.getPearUtility().getInstallDir().getAbsolutePath()));
        final Iterable<String> files = version.getPhpFiles();
        for (final String fname : files) {
            final File file = new File(channel.getPearUtility().getPhpDir(), fname);
            assertTrue(file.exists());
        }
    }
    
    /**
     * Tests all packages can be read.
     * @throws Exception exception
     */
    public void testAllPackages() throws Exception {
        final IPearChannel channel = getChannel(false);
        channel.initializePackages(true, true);
        // the getters may throw exceptions if a package cannot be read
        for (final IPackage pkg : channel.getKnownPackages()) {
            System.out.println("test pkg " + pkg.getPackageName());
            for (final IPackageVersion version : pkg.getKnownVersions()) {
                // this forces the read of the package
                System.out.println("test version " + 
                    pkg.getPackageName() + "/" + 
                    version.getVersion().getPearVersion());
                try {
                    version.getReleasingDeveloper();
                    version.getMaintainers();
                } catch (PhpException ex) {
                    // we ignore the file not found exception because some of the pear packages
                    // are referred in the pear channel but do not exist. mostly early versions.
                    // all non-FileNotFoundException will be rethrown to let the test case fail
                    if (!(ex.getCause() instanceof FileNotFoundException)) {
                        throw ex;
                    }
                    System.out.println("Package.xml not found... ignoring failure...");
                }
            }
        }
    }
    
    /**
     * Tests the versions.
     * 
     * @throws Exception 
     */
    public void testVersionMapping() throws Exception {
        final IPearChannel channel = getChannel(false);
        
        channel.initializePackages(true, true);
        
        IPackage pkg;
        IPackageVersion version;
        
        pkg = channel.getPackage("Archive_Tar");
        version = pkg.getVersion("0.10-b1");
        assertEquals("0.10-b1", version.getVersion().getPearVersion());
        assertEquals("0.10-beta-1", version.getVersion().getMavenVersion());
        
        pkg = channel.getPackage("Auth");
        version = pkg.getVersion("1.5.0RC1");
        assertEquals("1.5.0RC1", version.getVersion().getPearVersion());
        assertEquals("1.5.0-RC1", version.getVersion().getMavenVersion());
        
        pkg = channel.getPackage("Auth");
        version = pkg.getVersion("1.3.0r3");
        assertEquals("1.3.0r3", version.getVersion().getPearVersion());
        assertEquals("1.3.0-r3", version.getVersion().getMavenVersion());
        
        pkg = channel.getPackage("Auth_PrefManager2");
        version = pkg.getVersion("2.0.0dev1");
        assertEquals("2.0.0dev1", version.getVersion().getPearVersion());
        assertEquals("2.0.0-dev1", version.getVersion().getMavenVersion());
        
        pkg = channel.getPackage("Benchmark");
        version = pkg.getVersion("1.2.2beta1");
        assertEquals("1.2.2beta1", version.getVersion().getPearVersion());
        assertEquals("1.2.2-beta-1", version.getVersion().getMavenVersion());
        
        pkg = channel.getPackage("CodeGen_MySQL_UDF");
        version = pkg.getVersion("0.9.7dev");
        assertEquals("0.9.7dev", version.getVersion().getPearVersion());
        assertEquals("0.9.7-dev", version.getVersion().getMavenVersion());
        
        pkg = channel.getPackage("Console_ProgressBar");
        version = pkg.getVersion("0.5.2beta");
        assertEquals("0.5.2beta", version.getVersion().getPearVersion());
        assertEquals("0.5.2-beta", version.getVersion().getMavenVersion());
        
        pkg = channel.getPackage("DB");
        version = pkg.getVersion("1.4b1");
        assertEquals("1.4b1", version.getVersion().getPearVersion());
        assertEquals("1.4-beta-1", version.getVersion().getMavenVersion());
        
        pkg = channel.getPackage("Date");
        version = pkg.getVersion("1.5.0a1");
        assertEquals("1.5.0a1", version.getVersion().getPearVersion());
        assertEquals("1.5.0-alpha-1", version.getVersion().getMavenVersion());
    }
     
    /**
     * Pear channel.
     * @param install 
     * @return 
     * @throws Exception 
     */
    private IPearChannel getChannel(final boolean install)
        throws Exception {
        final MavenSession session = getSession();
        final IPearUtility util = getPearUtility(session, install);
        
        final File pearFolder = new File(
                session.getCurrentProject().getBasedir(), 
                "pear.php.net");
        final IPearChannel channel = util.channelDiscoverLocal(pearFolder);
        return channel;
    }
    
    /**
     * Gets the maven session.
     * @return gets the maven session.
     * @throws Exception thrown on pear errors.
     */
    private MavenSession getSession() throws Exception {
        // create the execution config
        final MavenSession session = this.createSimpleSession("pear/empty-pom");
        final File pearZip = new File(
                session.getCurrentProject().getBasedir(), 
                "pear.php.net.zip");
        final DefaultLog logger = new DefaultLog(new ConsoleLogger());
        FileHelper.unzip(logger, pearZip, session.getCurrentProject().getBasedir());
        return session;
    }
    
    /**
     * Returns the pear utility.
     * @param session 
     * @param install
     * @return
     * @throws Exception
     */
    private IPearUtility getPearUtility(final MavenSession session, final boolean install)
        throws Exception {
        final IComponentFactory factory = lookup(IComponentFactory.class);
        final IPearConfiguration pearConfig = factory.lookup(
                IPearConfiguration.class,
                IComponentFactory.EMPTY_CONFIG,
                session);

        final DefaultLog logger = new DefaultLog(new ConsoleLogger());
        final IPearUtility util = pearConfig.getUtility(logger);
        
        if (util.isInstalled()) {
            util.uninstall();
        }
        
        if (install) {
            util.installPear(false);
        }
        return util;
    }

}