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

package org.phpmaven.pear.library.test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import junit.framework.Assert;

import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.junit.Test;
import org.phpmaven.pear.library.IMaintainer;
import org.phpmaven.pear.library.IPackage;
import org.phpmaven.pear.library.IPackageVersion;
import org.phpmaven.pear.library.IPearChannel;
import org.phpmaven.pear.library.IPearUtility;
import org.phpmaven.pear.library.impl.PearUtility;
import org.phpmaven.phpexec.cli.PhpExecutableConfiguration;
import org.phpmaven.phpexec.library.PhpException;

/**
 * test cases for the pear support.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 0.1.0
 */
public class BaseTest extends AbstractTestCase {

    /**
     * Tests if the channel.xml can be read.
     *
     * @throws Exception thrown on errors
     */
	@Test
    public void testChannelXml() throws Exception {
		unzipPearNet();
        final IPearUtility util = getPearUtility(false);
        
        final File pearFolder = new File("target/pear.php.net").getAbsoluteFile();
        final IPearChannel channel = util.channelDiscoverLocal(pearFolder);
        Assert.assertNotNull(channel);
        Assert.assertNotNull(channel.getPrimaryServer());
        Assert.assertTrue(channel.getMirrors().iterator().hasNext());
        final String restServer = channel.getRestUrl(IPearChannel.REST_1_3);
        Assert.assertNotNull(restServer);
        Assert.assertEquals("file://" + pearFolder.getAbsolutePath() + "/rest/", restServer);
        Assert.assertEquals("pear.php.net", channel.getName());
        Assert.assertEquals("pear", channel.getSuggestedAlias());
        Assert.assertEquals("PHP Extension and Application Repository", channel.getSummary());
    }
    
    /**
     * Tests if the package infos can be fetched.
     *
     * @throws Exception thrown on errors
     */
	@Test
    public void testKnownPackages() throws Exception {
        final IPearChannel channel = getChannel(false);
        
        channel.initializePackages(true, true);
        
        final Iterable<IPackage> pkgs = channel.getKnownPackages();
        Assert.assertNotNull(pkgs);
    }
    
    /**
     * Tests if the package infos can be fetched.
     *
     * @throws Exception thrown on errors
     */
	@Test
    public void testChannelMaintainers() throws Exception {
        final IPearChannel channel = getChannel(false);
        
        final Iterable<IMaintainer> maintainers = channel.getMaintainers();
        Assert.assertNotNull(maintainers);
    }
    
    /**
     * Tests if the package infos can be fetched.
     *
     * @throws Exception thrown on errors
     */
	@Test
    public void testInstalledPackages() throws Exception {
        final IPearChannel channel = getChannel(true);
        
        channel.initializePackages(true, false);
        
        final Iterable<IPackage> pkgs = channel.getInstalledPackages();
        Assert.assertNotNull(pkgs);
        
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
            Assert.fail("Unexpected installed packages:\n" + packages);
        }
        
        Assert.assertEquals("Archive_Tar", pkgArchiveTar.getPackageName());
        Assert.assertNotNull(pkgArchiveTar.getInstalledVersion());
        Assert.assertEquals("1.3.7", pkgArchiveTar.getInstalledVersion().getVersion().getPearVersion());
        
        Assert.assertEquals("Console_Getopt", pkgConsoleGetopt.getPackageName());
        Assert.assertNotNull(pkgConsoleGetopt.getInstalledVersion());
        Assert.assertEquals("1.3.0", pkgConsoleGetopt.getInstalledVersion().getVersion().getPearVersion());
        
        Assert.assertEquals("PEAR", pkgPEAR.getPackageName());
        Assert.assertNotNull(pkgPEAR.getInstalledVersion());
        Assert.assertEquals("1.9.4", pkgPEAR.getInstalledVersion().getVersion().getPearVersion());
        
        Assert.assertEquals("Structures_Graph", pkgStructuresGraph.getPackageName());
        Assert.assertNotNull(pkgStructuresGraph.getInstalledVersion());
        Assert.assertEquals("1.0.4", pkgStructuresGraph.getInstalledVersion().getVersion().getPearVersion());
        
        Assert.assertEquals("XML_Util", pkgXmlUtil.getPackageName());
        Assert.assertNotNull(pkgXmlUtil.getInstalledVersion());
        Assert.assertEquals("1.2.1", pkgXmlUtil.getInstalledVersion().getVersion().getPearVersion());
    }
    
    /**
     * Tests if the package infos can be fetched.
     *
     * @throws Exception thrown on errors
     */
	@Test
    public void testKnownPackagesFailed() throws Exception {
        final IPearChannel channel = getChannel(true);
        
        channel.getKnownPackages();
        final IPackage pkg = channel.getPackage("Net_SSH2");
        Assert.assertNotNull(pkg);
        
        try {
            // will fail because the package cannot be read
            pkg.getKnownVersions();
            Assert.fail("Expected failures because not all packages are available");
        } catch (PhpException ex) {
            // succeeds
        }
    }
    
    /**
     * Tests the versions.
     * 
     * @throws Exception
     */
	@Test
    public void testVersions() throws Exception {
        final IPearChannel channel = getChannel(false);
        
        channel.initializePackages(true, true);
        
        final IPackage pkg = channel.getPackage("Archive_Tar");
        Assert.assertNotNull(pkg.getVersion("1.3.9"));
        Assert.assertNotNull(pkg.getVersion("1.3.8"));
        Assert.assertNotNull(pkg.getVersion("1.3.7"));
        Assert.assertNotNull(pkg.getVersion("1.3.6"));
        Assert.assertNotNull(pkg.getVersion("1.3.5"));
        Assert.assertNotNull(pkg.getVersion("1.3.4"));
        Assert.assertNotNull(pkg.getVersion("1.3.3"));
        Assert.assertNotNull(pkg.getVersion("1.3.2"));
        Assert.assertNotNull(pkg.getVersion("1.3.1"));
        Assert.assertNotNull(pkg.getVersion("1.3.0"));
        Assert.assertNotNull(pkg.getVersion("1.2"));
        Assert.assertNotNull(pkg.getVersion("1.1"));
        Assert.assertNotNull(pkg.getVersion("1.0"));
        Assert.assertNotNull(pkg.getVersion("0.10-b1"));
        Assert.assertNotNull(pkg.getVersion("0.9"));
        Assert.assertNotNull(pkg.getVersion("0.4"));
        Assert.assertNotNull(pkg.getVersion("0.3"));
    }
    
    /**
     * Tests the package version data.
     * 
     * @throws Exception
     */
	@Test
    public void testVersionData() throws Exception {
        final IPearChannel channel = getChannel(false);
        
        channel.initializePackages(true, true);
        
        IPackage pkg = channel.getPackage("Archive_Tar");
        IPackageVersion version = pkg.getVersion("0.3");
        Assert.assertEquals("Tar file management class", version.getSummary());
        Assert.assertEquals(
                "This class provides handling of tar files in PHP.\n" +
                "It supports creating, listing, extracting and adding to tar files.\n" +
                "Gzip support is available if PHP has the zlib extension built-in or\n" +
                "loaded.", version.getDescription());
        
        pkg = channel.getPackage("PHPUnit");
        version = pkg.getVersion("1.3.2");
        Assert.assertEquals(
                "! Changed license from PHP License to BSD Style License.",
                version.getReleaseNotes());
        Assert.assertEquals("2005-11-10 00:00:00", version.getReleaseDate());
        // TODO Assert.assertEquals("BSD License", version.getLicense());
        Assert.assertEquals("stable", version.getStability());
        Assert.assertNotNull(version.getMaintainers());
        Assert.assertTrue(version.getMaintainers().iterator().hasNext());
    }
    
    /**
     * Tests if the packages can be installed and the files can be found.
     * @throws Exception exception
     */
	@Test
    public void testPackageInstallation() throws Exception {
		final IPearUtility util = getPearUtility(true, true);
        final IPearChannel channel = util.lookupChannel("pear");
        final IPackage pkg = channel.getPackage("Validate_AT");
        final IPackageVersion version = pkg.getVersion("0.5.2");
        Assert.assertNull(pkg.getInstalledVersion());
        version.install();
        Assert.assertEquals("0.5.2", pkg.getInstalledVersion().getVersion().getPearVersion());
        
        Assert.assertTrue(
                channel.getPearUtility().getPhpDir().getAbsolutePath().startsWith(
                        channel.getPearUtility().getInstallDir().getAbsolutePath()));
        final Iterable<String> files = version.getPhpFiles();
        for (final String fname : files) {
            final File file = new File(channel.getPearUtility().getPhpDir(), fname);
            Assert.assertTrue(file.exists());
        }
    }
    
    /**
     * Tests all packages can be read.
     * @throws Exception exception
     */
	@Test
    public void testAllPackages() throws Exception {
        final IPearChannel channel = getChannel(false);
        channel.initializePackages(true, true);
        // the getters may throw exceptions if a package cannot be read
        for (final IPackage pkg : channel.getKnownPackages()) {
            // System.out.println("test pkg " + pkg.getPackageName());
            for (final IPackageVersion version : pkg.getKnownVersions()) {
                // this forces the read of the package
                /*System.out.println("test version " + 
                    pkg.getPackageName() + "/" + 
                    version.getVersion().getPearVersion());*/
                try {
                    version.getReleasingDeveloper();
                    version.getMaintainers();
                } catch (PhpException ex) {
                    // we ignore the file not found exception because some of the pear packages
                    // are referred in the pear channel but do not exist. mostly early versions.
                    // all non-FileNotFoundException will be rethrown to let the test case fail
                    if (!(ex.getCause() instanceof FileNotFoundException)) {
                    	Assert.fail("failed analysing package " + pkg.getPackageName() + "/" + 
                                version.getVersion().getPearVersion() + " -> cause: " + ex.getClass().getName() + "/" + ex.toString());
                    }
                    // System.out.println("Package.xml not found... ignoring failure...");
                }
            }
        }
    }
    
    /**
     * Tests the versions.
     * 
     * @throws Exception 
     */
	@Test
    public void testVersionMapping() throws Exception {
        Assert.assertEquals("0.10-beta-1", PackageHelper.convertPearVersionToMavenVersion("0.10-b1"));
        Assert.assertEquals("1.5.0-RC1", PackageHelper.convertPearVersionToMavenVersion("1.5.0RC1"));
        Assert.assertEquals("1.3.0-r3", PackageHelper.convertPearVersionToMavenVersion("1.3.0r3"));
        Assert.assertEquals("2.0.0-dev1", PackageHelper.convertPearVersionToMavenVersion("2.0.0dev1"));
        Assert.assertEquals("1.2.2-beta-1", PackageHelper.convertPearVersionToMavenVersion("1.2.2beta1"));
        Assert.assertEquals("0.9.7-dev", PackageHelper.convertPearVersionToMavenVersion("0.9.7dev"));
        Assert.assertEquals("0.5.2-beta", PackageHelper.convertPearVersionToMavenVersion("0.5.2beta"));
        Assert.assertEquals("1.4-beta-1", PackageHelper.convertPearVersionToMavenVersion("1.4b1"));
        Assert.assertEquals("1.5.0-alpha-1", PackageHelper.convertPearVersionToMavenVersion("1.5.0a1"));
    }
    
    /**
     * Tests the versions.
     * 
     * @throws Exception 
     */
	@Test
    public void testFileLayoutV2() throws Exception {
        final IPearChannel channel = getChannel(false);
        
        channel.initializePackages(true, true);
        
        IPackage pkg;
        IPackageVersion version;
        Iterator<String> dataFiles;
        Iterator<String> docFiles;
        Iterator<String> phpFiles;
        
        pkg = channel.getPackage("HTML_QuickForm2");
        version = pkg.getVersion("0.3.0");
        dataFiles = version.getFiles(IPackageVersion.FILE_ROLE_DATA).iterator();
        Assert.assertEquals("HTML_QuickForm2/data/quickform.css", dataFiles.next());
        Assert.assertFalse(dataFiles.hasNext());

        pkg = channel.getPackage("Net_DNSBL");
        version = pkg.getVersion("1.3.6");
        docFiles = version.getFiles(IPackageVersion.FILE_ROLE_DOC).iterator();
        Assert.assertEquals("Net_DNSBL/examples/check_dnsbl", docFiles.next());
        Assert.assertFalse(docFiles.hasNext());

        pkg = channel.getPackage("HTML_QuickForm2");
        version = pkg.getVersion("0.4.0");
        dataFiles = version.getFiles(IPackageVersion.FILE_ROLE_DATA).iterator();
        Assert.assertEquals("HTML_QuickForm2/quickform.css", dataFiles.next());
        Assert.assertFalse(dataFiles.hasNext());
        docFiles = version.getFiles(IPackageVersion.FILE_ROLE_DOC).iterator();
        while (docFiles.hasNext()) {
            Assert.assertTrue(docFiles.next().startsWith("HTML_QuickForm2/examples/"));
        }
        
        pkg = channel.getPackage("pearweb_channelxml");
        version = pkg.getVersion("1.13.0");
        dataFiles = version.getFiles(IPackageVersion.FILE_ROLE_WWW).iterator();
        while (dataFiles.hasNext()) {
            Assert.assertTrue(dataFiles.next().startsWith("public_html/"));
        }
        
        pkg = channel.getPackage("Genealogy_Gedcom");
        version = pkg.getVersion("1.0.1");
        phpFiles = version.getFiles(IPackageVersion.FILE_ROLE_PHP).iterator();
        while (phpFiles.hasNext()) {
            final String file = phpFiles.next();
            Assert.assertTrue(file.startsWith("Genealogy/Gedcom/") || "Genealogy/Gedcom.php".equals(file));
        }
        
        pkg = channel.getPackage("Crypt_Xtea");
        version = pkg.getVersion("1.1.0RC1");
        phpFiles = version.getFiles(IPackageVersion.FILE_ROLE_PHP).iterator();
        Assert.assertEquals("Crypt/Xtea.php", phpFiles.next());
        Assert.assertFalse(phpFiles.hasNext());
        docFiles = version.getFiles(IPackageVersion.FILE_ROLE_DOC).iterator();
        Assert.assertEquals("Crypt_Xtea/README", docFiles.next());
        Assert.assertFalse(docFiles.hasNext());
        
        pkg = channel.getPackage("Structures_BibTex");
        version = pkg.getVersion("0.1.0");
        phpFiles = version.getFiles(IPackageVersion.FILE_ROLE_PHP).iterator();
        Assert.assertEquals("Structures/BibTex.php", phpFiles.next());
        Assert.assertFalse(phpFiles.hasNext());
        docFiles = version.getFiles(IPackageVersion.FILE_ROLE_DOC).iterator();
        Assert.assertEquals("Structures_BibTex/examples/Structures_BibTex_example.php", docFiles.next());
        Assert.assertFalse(docFiles.hasNext());
        
        pkg = channel.getPackage("PHP_CodeSniffer");
        version = pkg.getVersion("0.0.5");
        phpFiles = version.getFiles(IPackageVersion.FILE_ROLE_PHP).iterator();
        while (phpFiles.hasNext()) {
            final String file = phpFiles.next();
            Assert.assertTrue(file.startsWith("PHP/CodeSniffer/") || "PHP/CodeSniffer.php".equals(file));
        }
    }
     
    /**
     * Pear channel.
     * @param install 
     * @return 
     * @throws Exception 
     */
    private IPearChannel getChannel(final boolean install)
        throws Exception {
        unzipPearNet();
        final IPearUtility util = getPearUtility(install);
        
        final File pearFolder = new File("target/pear.php.net").getAbsoluteFile();
        final IPearChannel channel = util.channelDiscoverLocal(pearFolder);
        return channel;
    }
    
    private void unzipPearNet() throws IOException {
    	final File target = new File("target/pear.php.net");
    	if (!target.exists()) {
    		target.mkdirs();
        	final File pearZip = new File(
                    "target/test-classes/org/phpmaven/pear/library/test/pear.php.net.zip");
        	unzip(pearZip, target.getParentFile());
    	}
    }
    
    /**
     * Unpacks a zip file.
     *
     * @param zipFile the zip file
     * @param destDir the destination directory
     * @throws java.io.IOException if something goes wrong
     */
    private void unzip(File zipFile, File destDir) throws IOException {
        final ZipFile zip = new ZipFile(zipFile);

        final Enumeration<? extends ZipEntry> items = zip.entries();
        while (items.hasMoreElements()) {
            final ZipEntry entry = items.nextElement();
            unpackZipEntry(entry, zip.getInputStream(entry), destDir);
        }
    }

    /**
     * Unpacks a single zip entry.
     *
     * @param zipEntry the zip entry
     * @param zipEntryInputStream the source stream of the entry
     * @param destDir the destination directory
     * @throws java.io.IOException if something goes wrong
     */
    private void unpackZipEntry(ZipEntry zipEntry, InputStream zipEntryInputStream, File destDir)
        throws IOException {
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
     * @param install
     * @return
     * @throws Exception
     */
	private IPearUtility getPearUtility(final boolean install)
        throws Exception {
		return this.getPearUtility(install, false);
    }
    
    /**
     * Returns the pear utility.
     * @param install
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
	private IPearUtility getPearUtility(final boolean install, final boolean upgrade)
        throws Exception {
		final File testDir = new File("target/test").getAbsoluteFile();
		FileUtils.deleteDirectory(testDir);
		testDir.mkdirs();
        final IPearUtility util = new PearUtility();
        util.configure(testDir, new PhpExecutableConfiguration(), Collections.EMPTY_LIST);
        
        if (util.isInstalled()) {
            util.uninstall();
        }
        
        if (install) {
            util.installPear(upgrade);
        }
        return util;
    }

}