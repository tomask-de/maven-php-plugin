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

package org.phpmaven.pear.library;

import org.phpmaven.phpexec.library.PhpException;

/**
 * A single pear package.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 0.1.0
 */
public interface IPackage {
	
	/**
	 * Returns the pear utility
	 * @return pear utility
	 */
	IPearUtility getPearUtility();
    
    /**
     * Returns the name of the package.
     * @return package name.
     */
    String getPackageName();
    
    /**
     * Sets the name of the package.
     * @param name package name.
     */
    void setPackageName(String name);
    
    /**
     * Returns the installed version.
     * @return installed version or {@code null} if this package is not installed.
     * @throws PhpException thrown on php execution errors.
     */
    IPackageVersion getInstalledVersion() throws PhpException;
    
    /**
     * Sets the installed version; does not perform any installation.
     * @param version installed version.
     */
    void setInstalledVersion(IPackageVersion version);
    
    /**
     * Returns the version by given pear version number.
     * @param pearVersion pear version number.
     * @return the version or null if the version is not known.
     * @throws PhpException thrown on php execution errors.
     */
    IPackageVersion getVersion(String pearVersion) throws PhpException;
    
    /**
     * Installs a new version.
     * @param version the version to be installed.
     * @param forceUninstall true to force uninstallation of any previous version.
     * @param forceInstall true to force the installation even on dependency errors.
     * @param ignoreDeps true to ingore dependencies.
     * @throws PhpException thrown on execution errors.
     */
    void install(IPackageVersion version, boolean forceUninstall, boolean forceInstall, boolean ignoreDeps)
        throws PhpException;
    
    /**
     * Returns the known versions.
     * @return known package versions.
     * @throws PhpException thrown on php execution errors.
     */
    Iterable<IPackageVersion> getKnownVersions() throws PhpException;
    
    /**
     * Uninstall the given module.
     * @param ignoreDeps true to ignore depependencies.
     * @throws PhpException thrown on execution errors.
     */
    void uninstall(boolean ignoreDeps) throws PhpException;
    
    /**
     * Returns the license.
     * @return license.
     * @throws PhpException thrown on execution errors.
     */
    String getLicense() throws PhpException;
    
    /**
     * Sets the license.
     * @param license License.
     * @throws PhpException thrown on execution errors.
     */
    void setLicense(String license) throws PhpException;
    
    /**
     * Returns the summary.
     * @return summary.
     * @throws PhpException thrown on execution errors.
     */
    String getSummary() throws PhpException;
    
    /**
     * Sets the summary.
     * @param summary summary.
     * @throws PhpException thrown on execution errors.
     */
    void setSummary(String summary) throws PhpException;
    
    /**
     * Returns the description.
     * @return description.
     * @throws PhpException thrown on execution errors.
     */
    String getDescription() throws PhpException;
    
    /**
     * Sets the description.
     * @param description description.
     * @throws PhpException thrown on execution errors.
     */
    void setDescription(String description) throws PhpException;
    
    /**
     * Returns the package maintainers.
     * @return maintainers list.
     * @throws PhpException thrown on execution errors.
     */
    Iterable<IMaintainer> getMaintainers() throws PhpException;
    
    /**
     * Adds a new maintainer.
     * @param maintainer maintainer list.
     * @throws PhpException thrown on execution errors.
     */
    void addMaintainer(IMaintainer maintainer) throws PhpException;
    
    /**
     * Initializes this package with given pear utility and channel.
     * 
     * <p>Implementors should throw an IllegalStateException if this
     * method is called twice.</p>
     * 
     * @param utility utility.
     * @param channel channel.
     */
    void initialize(IPearUtility utility, IPearChannel channel);

    /**
     * Returns the pear channel.
     * @return pear channel.
     */
    IPearChannel getChannel();

}
