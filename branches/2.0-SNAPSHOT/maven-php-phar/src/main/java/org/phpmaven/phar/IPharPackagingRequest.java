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

package org.phpmaven.phar;

import java.io.File;

import org.phpmaven.core.IComponentFactory;

/**
 * Interface for a packaging request to create a phar file.
 * 
 * <p>
 * Create an instance via {@link IComponentFactory}.
 * </p>
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public interface IPharPackagingRequest {

    /**
     * Returns the packager php template.
     * 
     * <p>
     * This template is used to create a common php file that creates the phar.
     * This is a template. The following variables/phrases will be replaced.
     * </p>
     * 
     * <table broder="1">
     * <tr><th>name</th><th>description</th></tr>
     * <tr>
     *   <td>$:{pharfilepath}</td>
     *   <td>The target directory to create the phar file. Masked to be used in a
     *       PHP string.</td>
     * </tr>
     * <tr>
     *   <td>$:{pharfilename}</td>
     *   <td>The phar filename (without any path info).</td>
     * </tr>
     * <tr>
     *   <td>$:{pharcontents}</td>
     *   <td>TODO</td>
     * </tr>
     * <tr>
     *   <td>$:{pharcompression}</td>
     *   <td>TODO</td>
     * </tr>
     * <tr>
     *   <td>$:{pharstub}</td>
     *   <td>TODO</td>
     * </tr>   
     * </table>
     * 
     * @return packager php template.
     */
    String getPackagePhpTemplate();

//    /**
//     * Sets the packager php template.
//     * @param packagePhpTemplate packager php template.
//     */
//    public void setPackagePhpTemplate(String packagePhpTemplate) {
//        this.packagePhpTemplate = packagePhpTemplate;
//    }
//
//    /**
//     * Returns the packager php directory template.
//     * @return packager php directory template
//     */
//    public String getPackagePhpDirectoryTemplate() {
//        return packagePhpDirectoryTemplate;
//    }
//
//    /**
//     * Sets the packager php directory template.
//     * @param packagePhpDirectoryTemplate packager php directory template.
//     */
//    public void setPackagePhpDirectoryTemplate(
//            String packagePhpDirectoryTemplate) {
//        this.packagePhpDirectoryTemplate = packagePhpDirectoryTemplate;
//    }
//
//    /**
//     * Returns the php file stub.
//     * 
//     * @return php file stub
//     */
//    public String getStub() {
//        return stub;
//    }
//
//    /**
//     * Sets the php file stub.
//     * 
//     * @param stub
//     *            php file stub
//     */
//    public void setStub(String stub) {
//        this.stub = stub;
//    }
//
//    /**
//     * Adds a directory to be packed.
//     * 
//     * @param directory
//     *            directory to pack
//     * @param baseDirectory
//     *            base directory for building relative entries
//     */
//    public void addDirectory(File directory, File baseDirectory) {
//        final PharDirectory entry = new PharDirectory();
//        entry.setDirectory(directory);
//        entry.setBaseDirectory(baseDirectory);
//        this.entries.add(entry);
//    }
//
//    /**
//     * Adds a file to be packed.
//     * 
//     * @param file file to pack
//     * @param localName the local name (relative path)
//     */
//    public void addFile(File file, String localName) {
//        final PharFile entry = new PharFile();
//        entry.setFile(file);
//        entry.setLocalName(localName);
//        this.entries.add(entry);
//    }
//
//    /**
//     * Returns the entries for packing.
//     * @return entries to be packed
//     */
//    public Iterable<PharEntry> getEntries() {
//        return this.entries;
//    }
//
//    /**
//     * Returns the compression method.
//     * @return compression method
//     */
//    public CompressionMethod getFileCompression() {
//        return fileCompression;
//    }
//
//    /**
//     * Sets the compression mode.
//     * @param fileCompression file compression mode
//     */
//    public void setFileCompression(CompressionMethod fileCompression) {
//        this.fileCompression = fileCompression;
//    }
//
//    /**
//     * Returns the target directory.
//     * @return target directory
//     */
//    public File getTargetDirectory() {
//        return targetDirectory;
//    }
//
//    /**
//     * Sets the target directory.
//     * @param targetDirectory target directory
//     */
//    public void setTargetDirectory(File targetDirectory) {
//        this.targetDirectory = targetDirectory;
//    }
//
//    /**
//     * Returns the filename.
//     * @return filename of phar file
//     */
//    public String getFilename() {
//        return filename;
//    }
//
//    /**
//     * Sets the filename.
//     * @param filename filename of phar file
//     */
//    public void setFilename(String filename) {
//        this.filename = filename;
//    }
//
//    /**
//     * Sets compression flag.
//     * @param compressed true to use compression
//     */
//    public void setCompressed(boolean compressed) {
//        this.isCompressed = compressed;
//    }
//
//    /**
//     * Returns the compression flag.
//     * @return true to compress
//     */
//    public boolean isCompressed() {
//        return isCompressed;
//    }

    
}
