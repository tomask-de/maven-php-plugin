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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.monitor.logging.DefaultLog;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.codehaus.plexus.util.IOUtil;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.pear.IMavenPearVersion;
import org.phpmaven.pear.IPearConfiguration;
import org.phpmaven.pear.library.IPackage;
import org.phpmaven.pear.library.IPackageVersion;
import org.phpmaven.pear.library.IPearChannel;
import org.phpmaven.pear.library.IPearUtility;
import org.phpmaven.test.AbstractTestCase;

import com.google.common.base.Preconditions;

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
     * Tests if the packages can be installed and the files can be found.
     * @throws Exception exception
     */
    public void testPackageInstallation() throws Exception {
        final IPearChannel channel = getChannel(true);
        final IPackage pkg = channel.getPackage("Validate_AT");
        final IPackageVersion version = pkg.getVersion("0.5.2");
        assertNull(pkg.getInstalledVersion());
        version.install();
        assertEquals("0.5.2", ((IMavenPearVersion)pkg.getInstalledVersion().getVersion()).getMavenVersion());
        
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
        assertEquals("0.10-beta-1", ((IMavenPearVersion)version.getVersion()).getMavenVersion());
        
        pkg = channel.getPackage("Auth");
        version = pkg.getVersion("1.5.0RC1");
        assertEquals("1.5.0RC1", version.getVersion().getPearVersion());
        assertEquals("1.5.0-RC1", ((IMavenPearVersion)version.getVersion()).getMavenVersion());
        
        pkg = channel.getPackage("Auth");
        version = pkg.getVersion("1.3.0r3");
        assertEquals("1.3.0r3", version.getVersion().getPearVersion());
        assertEquals("1.3.0-r3", ((IMavenPearVersion)version.getVersion()).getMavenVersion());
        
        pkg = channel.getPackage("Auth_PrefManager2");
        version = pkg.getVersion("2.0.0dev1");
        assertEquals("2.0.0dev1", version.getVersion().getPearVersion());
        assertEquals("2.0.0-dev1", ((IMavenPearVersion)version.getVersion()).getMavenVersion());
        
        pkg = channel.getPackage("Benchmark");
        version = pkg.getVersion("1.2.2beta1");
        assertEquals("1.2.2beta1", version.getVersion().getPearVersion());
        assertEquals("1.2.2-beta-1", ((IMavenPearVersion)version.getVersion()).getMavenVersion());
        
        pkg = channel.getPackage("CodeGen_MySQL_UDF");
        version = pkg.getVersion("0.9.7dev");
        assertEquals("0.9.7dev", version.getVersion().getPearVersion());
        assertEquals("0.9.7-dev", ((IMavenPearVersion)version.getVersion()).getMavenVersion());
        
        pkg = channel.getPackage("Console_ProgressBar");
        version = pkg.getVersion("0.5.2beta");
        assertEquals("0.5.2beta", version.getVersion().getPearVersion());
        assertEquals("0.5.2-beta", ((IMavenPearVersion)version.getVersion()).getMavenVersion());
        
        pkg = channel.getPackage("DB");
        version = pkg.getVersion("1.4b1");
        assertEquals("1.4b1", version.getVersion().getPearVersion());
        assertEquals("1.4-beta-1", ((IMavenPearVersion)version.getVersion()).getMavenVersion());
        
        pkg = channel.getPackage("Date");
        version = pkg.getVersion("1.5.0a1");
        assertEquals("1.5.0a1", version.getVersion().getPearVersion());
        assertEquals("1.5.0-alpha-1", ((IMavenPearVersion)version.getVersion()).getMavenVersion());
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
     * Unpacks a zip file.
     *
     * @param log Logging
     * @param zipFile the zip file
     * @param destDir the destination directory
     * @throws IOException if something goes wrong
     */
    private void unzip(Log log, File zipFile, File destDir) throws IOException {
        Preconditions.checkNotNull(zipFile, "ZipFile");

        final ZipFile zip = new ZipFile(zipFile);
        log.debug("unzip " + zipFile.getAbsolutePath());

        final Enumeration<? extends ZipEntry> items = zip.entries();
        while (items.hasMoreElements()) {
            final ZipEntry entry = items.nextElement();
            unpackZipEntry(entry, zip.getInputStream(entry), destDir);
        }
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
        unzip(logger, pearZip, session.getCurrentProject().getBasedir());
        return session;
    }

    /**
     * Unpacks a single zip entry.
     *
     * @param zipEntry the zip entry
     * @param zipEntryInputStream the source stream of the entry
     * @param destDir the destination directory
     * @throws IOException if something goes wrong
     */
    private void unpackZipEntry(ZipEntry zipEntry, InputStream zipEntryInputStream, File destDir)
        throws IOException {

        Preconditions.checkNotNull(zipEntry, "ZipEntry");
        Preconditions.checkNotNull(zipEntryInputStream, "ZipEntryInputStream");
        Preconditions.checkNotNull(destDir, "Destination Directory");
        Preconditions.checkArgument(!destDir.exists() || destDir.isDirectory(), "Destination Directory");

        // final name
        final File destFile = new File(destDir, zipEntry.getName());

        // already there
        if (destFile.exists()) {
            return;
        }

        // just a directory to create
        if (zipEntry.isDirectory()) {
            destFile.mkdirs();
            return;
        } else {
            // ensure parent dir exists
            destFile.getParentFile().mkdirs();
        }

        OutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(destFile));
            IOUtil.copy(new BufferedInputStream(zipEntryInputStream), out);
        } finally {
            if (out != null) out.close();
        }
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