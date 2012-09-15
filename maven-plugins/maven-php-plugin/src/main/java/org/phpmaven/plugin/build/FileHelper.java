/**
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

package org.phpmaven.plugin.build;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.wagon.PathUtils;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.exec.PhpException;
import org.phpmaven.phar.IPharPackagerConfiguration;

import com.google.common.base.Preconditions;

/**
 * Static utilities for file handling.
 *
 * @author Christian Wiedemann
 * @author Tobias Sarnowski
 */
public final class FileHelper {

    private FileHelper() {
        // we only have static methods
    }

    /**
     * Copies over a file from the sourceDirectory to the targetDirectory preserving its relative subdirectories.
     *
     * @param sourceDirectory where the main source directory is
     * @param targetDirectory where the target directory is
     * @param sourceFile which file to copy to the target directory
     * @param forceOverwrite if timestamps should be ignored
     * @throws IOException if something goes wrong while copying
     */
    public static void copyToFolder(File sourceDirectory, File targetDirectory, File sourceFile, boolean forceOverwrite)
        throws IOException {

        final String relativeFile = PathUtils.toRelative(
            sourceDirectory.getAbsoluteFile(),
            sourceFile.getAbsolutePath()
        );
        final File targetFile = new File(targetDirectory, relativeFile);

        if (forceOverwrite) {
            FileUtils.copyFile(sourceFile, targetFile);
        } else {
            FileUtils.copyFileIfModified(sourceFile, targetFile);
        }
    }

    /**
     * Unzips all files to the given directory (using jar).
     *
     * @param log Logging
     * @param targetDirectory where to unpack the files to
     * @param elements list of files to unpack
     * @param factory component factory
     * @param session maven session
     * @throws IOException if something goes wrong while copying
     */
    public static void unzipElements(Log log, File targetDirectory, List<String> elements, IComponentFactory factory,
            MavenSession session)
        throws IOException {
        Preconditions.checkArgument(
            !targetDirectory.exists() || targetDirectory.isDirectory(),
            "Destination Directory");

        targetDirectory.mkdirs();
        if (!targetDirectory.exists()) {
            throw new IllegalStateException("Could not create target directory " + targetDirectory.getAbsolutePath());
        }
        
        log.debug(elements.toString());

        for (String element : elements) {
            log.debug("unpacking " + element);
            final File sourceFile = new File(element);
            if (sourceFile.isFile()) {
                final int pos = sourceFile.getName().lastIndexOf('.');
                String extension = sourceFile.getName();
                if (pos != -1) {
                    extension = extension.substring(pos + 1);
                }
                
                if ("jar".equals(extension)) {
                    // for backward compatibility to phpmaven1; there we build jar instead of phar
                    unjar(log, sourceFile, targetDirectory);
                } else if ("phar".equals(extension)) {
                    unphar(log, targetDirectory, factory, session, sourceFile);
                } else if ("zip".equals(extension)) {
                    // although jar and zips are compatible to each other this is a implementation detail of jvm.
                    // we should not depend on it. so let us divide it.
                    unzip(log, sourceFile, targetDirectory);
                } else {
                    throw new IOException("Unknown archive format. Unable to extract " + sourceFile.getAbsolutePath());
                }
            }
        }
    }

    /**
     * Unphar given file to destination directory.
     * 
     * @param log Logging
     * @param targetDirectory where to unpack the files to
     * @param factory component factory
     * @param session maven session
     * @param sourceFile the jar source file
     * @throws IOException if something goes wrong while copying
     */
    public static void unphar(Log log, File targetDirectory, IComponentFactory factory,
            final MavenSession session, final File sourceFile) throws IOException {
        log.debug("unphar " + sourceFile.getAbsolutePath());
        try {
            final IPharPackagerConfiguration config = factory.lookup(
                    IPharPackagerConfiguration.class,
                    IComponentFactory.EMPTY_CONFIG,
                    session);
            
            config.getPharPackager().extractPharTo(sourceFile, targetDirectory, log);
        } catch (ComponentLookupException e) {
            throw new IOException(
                    "Error while execution unphar script. Unable to extract "
                    + sourceFile.getAbsolutePath(), e);
        } catch (PlexusConfigurationException e) {
            throw new IOException(
                    "Error while execution unphar script. Unable to extract "
                    + sourceFile.getAbsolutePath(), e);
        } catch (PhpException e) {
            throw new IOException(
                    "Error while execution unphar script. Unable to extract "
                    + sourceFile.getAbsolutePath(), e);
        }
    }

    /**
     * Unpacks a jar file.
     *
     * @param log Logging
     * @param jarFile the jar file
     * @param destDir the destination directory
     * @throws IOException if something goes wrong
     */
    public static void unjar(Log log, File jarFile, File destDir) throws IOException {
        Preconditions.checkNotNull(jarFile, "JarFile");

        final JarFile jar = new JarFile(jarFile);
        log.debug("unjar " + jarFile.getAbsolutePath());

        final Enumeration<JarEntry> items = jar.entries();
        while (items.hasMoreElements()) {
            final JarEntry entry = items.nextElement();
            unpackJarEntry(entry, jar.getInputStream(entry), destDir);
        }
    }

    /**
     * Unpacks a zip file.
     *
     * @param log Logging
     * @param zipFile the zip file
     * @param destDir the destination directory
     * @throws IOException if something goes wrong
     */
    public static void unzip(Log log, File zipFile, File destDir) throws IOException {
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
     * Unpacks a jar URI.
     *
     * @param jarUri the jar uri
     * @param destDir the destination directory
     * @throws IOException if something goes wrong
     */
    public static void unjar(URI jarUri, File destDir) throws IOException {
        Preconditions.checkNotNull(jarUri, "JarFile");

        unjar(jarUri.toURL().openStream(), destDir);
    }

    /**
     * Unpacks a jar stream.
     *
     * @param inputStream the jar stream
     * @param destDir the destination directory
     * @throws IOException if something goes wrong
     */
    public static void unjar(InputStream inputStream, File destDir) throws IOException {
        Preconditions.checkNotNull(inputStream, "InputStream");

        final JarInputStream jarInputStream = new JarInputStream(inputStream);
        while (true) {
            final JarEntry entry = jarInputStream.getNextJarEntry();
            if (entry == null) {
                break;
            }
            unpackJarEntry(entry, jarInputStream, destDir);
        }
    }

    /**
     * Unpacks a single jar entry.
     *
     * @param jarEntry the jar entry
     * @param jarEntryInputStream the source stream of the entry
     * @param destDir the destination directory
     * @throws IOException if something goes wrong
     */
    public static void unpackJarEntry(JarEntry jarEntry, InputStream jarEntryInputStream, File destDir)
        throws IOException {

        Preconditions.checkNotNull(jarEntry, "JarEntry");
        Preconditions.checkNotNull(jarEntryInputStream, "JarEntryInputStream");
        Preconditions.checkNotNull(destDir, "Destination Directory");
        Preconditions.checkArgument(!destDir.exists() || destDir.isDirectory(), "Destination Directory");

        unpackZipEntry(jarEntry, jarEntryInputStream, destDir);
    }

    /**
     * Unpacks a single zip entry.
     *
     * @param zipEntry the zip entry
     * @param zipEntryInputStream the source stream of the entry
     * @param destDir the destination directory
     * @throws IOException if something goes wrong
     */
    public static void unpackZipEntry(ZipEntry zipEntry, InputStream zipEntryInputStream, File destDir)
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
            out = new FileOutputStream(destFile);
            IOUtil.copy(zipEntryInputStream, out);
        } finally {
            if (out != null) out.close();
        }
    }
   
    /**
     * Reads an url to string; should only be used for non-blocking connections
     * (f.e. jar file contents and other things).
     * 
     * @param url the url to be read
     * @return the results
     * @throws IOException thrown on problems while reading.
     */
    public static String readUrl(final URL url) throws IOException {
        final InputStream stream = url.openStream();
        final BufferedReader in = new BufferedReader(new InputStreamReader(stream));
        String inputLine;
        final StringBuffer result = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            if (result.length() > 0) {
                result.append("\n");
            }
            result.append(inputLine);
        }
        in.close();
        return result.toString();
    }
    
    /**
     * Resolve a list of file wildcard expressions.
     *
     * @param fileList List of strings with filenames/wildcard expressions
     * @param baseDir the base folder to run the wildcards on
     * @param caseSensitiveMatch true if the wildcards should be run case sensitive
     * @return List of matching file names
     */    
    public static String[] getWildcardMatches(String[] fileList, File baseDir, boolean caseSensitiveMatch) {
        final DirectoryScanner scanner = new DirectoryScanner();
        scanner.setIncludes(fileList);
        scanner.setBasedir(baseDir);
        scanner.setCaseSensitive(caseSensitiveMatch);
        scanner.scan();
        return scanner.getIncludedFiles();
   }
}
