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
import java.util.Map;

import org.phpmaven.core.IComponentFactory;

/**
 * Interface for a packaging request to create a phar file.
 * 
 * <p>
 * Create an instance via {@link IComponentFactory}.
 * </p>
 * 
 * <p>
 * Available configuration options:
 * </p>
 * 
 * <table border="1">
 * <tr><th>Name</th><th>Command line option</th><th>Property</th><th>Default</th><th>Description</th></tr>
 * <tr>
 *   <td>targetDirectory</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>Alternative target directory. Defaults to "${project.build.directory}".
 *   </td>
 * </tr>
 * <tr>
 *   <td>filename</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>The phar filename. Notice: This is only available for the phar goal.
 *       The package goal will always overwrite this with a filename from
 *       project artifact id and version number.
 *   </td>
 * </tr>
 * <tr>
 *   <td>compressed</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>Flag to enable phar package compression. Defaults to true.
 *   </td>
 * </tr>
 * <tr>
 *   <td>stub</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>The stub to be used for the phar file. The stub is a php script that will be invoked
 *       if someone executes the phar.
 *   </td>
 * </tr>
 * <tr>
 *   <td>packagePhpTemplate</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>PHP template to package the phar. See {@link IPharPackagingRequest#getPackagePhpTemplate()}
 *       for details.
 *   </td>
 * </tr>
 * <tr>
 *   <td>packagePhpDirectoryTemplate</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>PHP template to package a directory. See {@link IPharPackagingRequest#getPackagePhpDirectoryTemplate()}
 *       for details.
 *   </td>
 * </tr>
 * <tr>
 *   <td>packagePhpFileTemplate</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>PHP template to package a file. See {@link IPharPackagingRequest#getPackagePhpFileTemplate()}
 *       for details.
 *   </td>
 * </tr>
 * <tr>
 *   <td>largeFile</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>Flag to control building of large phar files (more than 1000 or 2000 files). if you get into trouble building
 *   the phars that ends with 'BadMethodCallException' on compression set this flag to true. Defaults to false. Note:
 *   Setting this flag to true will overwrite the templates. if you need to use alternative templates and run into
 *   problems you should set 'compressed' to false and use an alternative file template that compresses the single
 *   php files.
 *   </td>
 * </tr>
 * <tr>
 *   <td>metadata</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>List of arbitrary elements that will be added to the phar metadata.<p>Usage: &lt;metadata&gt;&lt;myMetadataKey&gt;Value&lt;/myMetadataKey&gt;&lt/metadata&gt;
 *   </td>
 * </tr>
 * <tr>
 *   <td>alias</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>Optional alias that may be used in phar stream wrapper access for the phar. See &lt;a href="http://php.net/manual/en/phar.setalias.php"&gt;http://php.net/manual/en/phar.setalias.php&lt;/a&gt;.
 *   </td>
 * </tr>
 * </table>
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
     *   <td>Will contain entries for each package content. That will be either
     *   directories or files. See the parameter packagePhpDirectoryTemplate and the
     *   packagePhpFileTemplate for details.</td>
     * </tr>
     * <tr>
     *   <td>$:{pharcompression}</td>
     *   <td>Will contain a php command to compress the files within the created
     *   phar file.</td>
     * </tr>
     * <tr>
     *   <td>$:{pharstub}</td>
     *   <td>Will contain the phar stub; this is a php file that is being invoked
     *   if someone uses php to execute the phar.</td>
     * </tr>   
     * </table>
     * 
     * @return packager php template.
     */
    String getPackagePhpTemplate();

    /**
     * Sets the packager php template.
     * @param packagePhpTemplate packager php template.
     * @see #getPackagePhpTemplate()
     */
    void setPackagePhpTemplate(String packagePhpTemplate);
    
    /**
     * Returns the packager php directory template.
     * 
     * <p>
     * This template is used to create a php code fragment for adding a directory
     * into a phar file. The following variables/phrases will be replaced.
     * </p>
     * 
     * <table broder="1">
     * <tr><th>name</th><th>description</th></tr>
     * <tr>
     *   <td>$:{pkgdir}</td>
     *   <td>The relative path for the directory within the phar file.</td>
     * </tr>
     * <tr>
     *   <td>$:{pkgbasedir}</td>
     *   <td>The absolute path of the directory (the path that needs to be packed).</td>
     * </tr>   
     * </table>
     * 
     * @return packager php directory template
     */
    String getPackagePhpDirectoryTemplate();

    /**
     * Sets the packager php directory template.
     * @param packagePhpDirectoryTemplate packager php directory template.
     * @see #getPackagePhpDirectoryTemplate()
     */
    void setPackagePhpDirectoryTemplate(String packagePhpDirectoryTemplate);
    
    /**
     * Returns the packager php file template.
     * 
     * <p>
     * This template is used to create a php code fragment for adding a single file
     * into a phar file. The following variables/phrases will be replaced.
     * </p>
     * 
     * <table broder="1">
     * <tr><th>name</th><th>description</th></tr>
     * <tr>
     *   <td>$:{filename}</td>
     *   <td>The relative path for the file within the phar file.</td>
     * </tr>
     * <tr>
     *   <td>$:{filebasepath}</td>
     *   <td>The absolute path of the file (the path that needs to be packed).</td>
     * </tr>   
     * </table>
     * 
     * @return packager php file template
     */
    String getPackagePhpFileTemplate();

    /**
     * Sets the packager php file template.
     * @param packagePhpFileTemplate packager php file template.
     * @see #getPackagePhpFileTemplate()
     */
    void setPackagePhpFileTemplate(String packagePhpFileTemplate);
    
    /**
     * Returns true if this is a large phar file; will force to use another kind of compression because of php bugs.
     * @return true to build a large file.
     */
    boolean isLargePhar();
    
    /**
     * Sets the parge file flag.
     * @param largeFile true to build a large phar.
     */
    void setLargePhar(boolean largeFile);

    /**
     * Returns the php file stub.
     * 
     * @return php file stub
     */
    String getStub();

    /**
     * Sets the php file stub.
     * 
     * @param stub
     *            php file stub
     */
    void setStub(String stub);
    

    /**
     * Adds a directory to be packed.
     * 
     * @param relativePath
     *            relative path name inside the phar
     * @param pathToPack
     *            The path that will be packed
     */
    void addDirectory(String relativePath, File pathToPack);
    
    /**
     * Adds a file to be packed.
     * 
     * @param localName the local name (relative path)
     * @param file file to pack
     */
    void addFile(String localName, File file);
    
    /**
     * Returns the entries for packing.
     * @return entries to be packed
     */
    Iterable<PharEntry> getEntries();

    /**
     * Returns the compression method.
     * @return compression method
     */
    CompressionMethod getFileCompression();
    
    /**
     * Sets the compression mode.
     * @param fileCompression file compression mode
     */
    void setFileCompression(CompressionMethod fileCompression);
    
    /**
     * Returns the target directory.
     * @return target directory
     */
    File getTargetDirectory();
    
    /**
     * Sets the target directory.
     * @param targetDirectory target directory
     */
    void setTargetDirectory(File targetDirectory);
    
    /**
     * Returns the filename.
     * @return filename of phar file
     */
    String getFilename();
    
    /**
     * Sets the filename.
     * @param filename filename of phar file
     */
    void setFilename(String filename);
    
    /**
     * Sets compression flag.
     * @param compressed true to use compression
     */
    void setCompressed(boolean compressed);
    
    /**
     * Returns the compression flag.
     * @return true to compress
     */
    boolean isCompressed();

    /**
     * Returns the phar metadata
     * @return the metadata elements
     */
	Map<String, String> getMetadata();

	/**
	 * Sets the phar metadata
	 * @param metadatas the metadata elements
	 */
	void setMetadata(Map<String, String> metadatas);
	
	/**
	 * Returns the phar alias
	 * @return the alias
	 */
	String getAlias();
	
	/**
	 * Sets the phar alias
	 * @param alias the alias
	 */
	void setAlias(String alias);
	
}
