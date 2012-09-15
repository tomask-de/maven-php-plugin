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

package org.phpmaven.pear;

import java.io.File;

import org.phpmaven.exec.PhpException;

/**
 * A single package version.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public interface IPackageVersion {
    
    /**
     * File role: php script files.
     */
    String FILE_ROLE_PHP = "php";
    
    /**
     * File role: doc files.
     */
    String FILE_ROLE_DOC = "doc";
    
    /**
     * File role: www files.
     */
    String FILE_ROLE_WWW = "www";
    
    /**
     * File role: data files.
     */
    String FILE_ROLE_DATA = "data";
    
    /**
     * Pear packager version.
     */
    enum PearPkgVersion {
        /** version 1 pear package. */
        PKG_V1,
        /** version 2 pear package. */
        PKG_V2
    }
    
    /**
     * Returns the packager version.
     * @return packager version.
     * @throws PhpException thrown on errors.
     */
    PearPkgVersion getPackagerVersion() throws PhpException;
    
    /**
     * Returns the package name.
     * @return package name.
     */
    String getPackageName();
    
    /**
     * Sets the package name.
     * @param name package name.
     */
    void setPackageName(String name);

    /**
     * Get the version.
     * @return pear version.
     */
    IVersion getVersion();
    
    /**
     * Sets the version.
     * @param version pear version.
     */
    void setVersion(IVersion version);
    
    /**
     * Returns the stability.
     * @return stability.
     */
    String getStability();
    
    /**
     * Sets the stability.
     * @param stability stability.
     */
    void setStability(String stability);
    
    /**
     * Returns the minimum php version.
     * @return minimum php version.
     */
    String getMinPhpVersion();
    
    /**
     * Sets the minimum php version.
     * @param version minimum php version.
     */
    void setMinPhpVersion(String version);
    
    /**
     * Returns the releasing developer.
     * @return releasing developer nickname.
     * @throws PhpException thrown on php execution errors.
     */
    String getReleasingDeveloper() throws PhpException;
    
    /**
     * Sets the releasing developer.
     * @param developerNick releasing developer.
     * @throws PhpException 
     */
    void setReleasingDeveloper(String developerNick) throws PhpException;
    
    /**
     * Returns the summary.
     * @return summary.
     * @throws PhpException 
     */
    String getSummary() throws PhpException;
    
    /**
     * Sets the summary.
     * @param summary Summary.
     * @throws PhpException 
     */
    void setSummary(String summary) throws PhpException;
    
    /**
     * Returns the description.
     * @return description.
     * @throws PhpException 
     */
    String getDescription() throws PhpException;
    
    /**
     * Sets the description.
     * @param description description.
     * @throws PhpException 
     */
    void setDescription(String description) throws PhpException;
    
    /**
     * Returns the release date.
     * @return release date.
     * @throws PhpException 
     */
    String getReleaseDate() throws PhpException;
    
    /**
     * Sets the release date.
     * @param date release date.
     * @throws PhpException 
     */
    void setReleaseDate(String date) throws PhpException;
    
    /**
     * Returns the release notes.
     * @return release notes.
     * @throws PhpException 
     */
    String getReleaseNotes() throws PhpException;
    
    /**
     * Sets the release notes.
     * @param notes release notes.
     * @throws PhpException 
     */
    void setReleaseNotes(String notes) throws PhpException;
    
    /**
     * Returns the file size in bytes.
     * @return file size.
     * @throws PhpException 
     */
    int getFileSize() throws PhpException;
    
    /**
     * Sets the file size.
     * @param bytes file size.
     * @throws PhpException 
     */
    void setFileSize(int bytes) throws PhpException;
    
    /**
     * Returns the url to download.
     * @return url.
     * @throws PhpException 
     */
    String getUrl() throws PhpException;
    
    /**
     * Sets the url.
     * @param url download url.
     * @throws PhpException 
     */
    void setUrl(String url) throws PhpException;
    
    /**
     * Returns the api version.
     * @return api version
     * @throws PhpException 
     */
    IVersion getApiVersion() throws PhpException;
    
    /**
     * Sets the api version.
     * @param version api version.
     * @throws PhpException 
     */
    void setApiVersion(IVersion version) throws PhpException;
    
    /**
     * Returns the required dependencies.
     * @return required dependencies.
     * @throws PhpException 
     */
    Iterable<IDependency> getRequiredDependencies() throws PhpException;
    
    /**
     * Returns the optional dependencies.
     * @return optional dependencies.
     * @throws PhpException 
     */
    Iterable<IDependency> getOptionalDependencies() throws PhpException;
    
    /**
     * Adds a new dependency.
     * @param dep dependency.
     * @param isOptional true for optional dependencies; false for required dependencies.
     * @throws PhpException 
     */
    void addDependency(IDependency dep, boolean isOptional) throws PhpException;

    /**
     * Initializes this package version; implementors should thrown an IllegalStateExcetpion
     * if this method is called twice.
     * @param pearUtility the pear utility to be used.
     * @param pearChannel the pear channel to be used.
     * @param pearPackage the pear Package to be used.
     */
    void initialize(IPearUtility pearUtility, IPearChannel pearChannel, IPackage pearPackage);
    
    /**
     * Returns the maintainers.
     * @return maintainers.
     * @throws PhpException thrown on php execution errors.
     */
    Iterable<IMaintainer> getMaintainers() throws PhpException;
    
    /**
     * Adds a maintainer.
     * @param maintainer maintainer.
     * @throws PhpException thrown on php execution errors.
     */
    void addMaintainer(IMaintainer maintainer) throws PhpException;

    /**
     * Installs this version.
     * @throws PhpException thrown on exceptions.
     */
    void install() throws PhpException;

    /**
     * Installs this version.
     * @param noUninstall true to not uninstall and instead try to overwrite an already installed package.
     * @throws PhpException thrown on exceptions.
     */
    void install(boolean noUninstall) throws PhpException;

    /**
     * Gets the php files.
     * @return php files.
     * @throws PhpException thrown on execution errors
     */
    Iterable<String> getPhpFiles() throws PhpException;

    /**
     * Gets the php files.
     * @param role the file role
     * @return php files.
     * @throws PhpException thrown on execution errors
     */
    Iterable<String> getFiles(String role) throws PhpException;

    /**
     * Returns the name of the extension this version provides.
     * @return name of the extension or {@code null} if this is a php extension.
     * @throws PhpException thrown on execution errors
     */
    String providesExtension() throws PhpException;

    /**
     * Returns the pear package.
     * @return pear package.
     */
    IPackage getPackage();

    /**
     * Writes the package xml to given file.
     * @param toFile package xml file (target)
     * @throws PhpException thrown on errors
     */
    void writePackageXml(File toFile) throws PhpException;
    
    /**
     * Writes the tgz (original package) to given file.
     * @param toFile tgz file (target)
     * @throws PhpException thrown on errors
     */
    void writeTgz(File toFile) throws PhpException;

}
