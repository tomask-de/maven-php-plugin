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
 * A pear channel.
 * 
 * <p>
 * See <a href="http://pear.php.net/manual/en/guide.migrating.channels.xml.php">Channel.xml description</a>.
 * </p>
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 0.1.0
 */
public interface IPearChannel {
    
    /** rest version 1.0. */
    String REST_1_0 = "REST1.0";
    
    /** rest version 1.1. */
    String REST_1_1 = "REST1.1";
    
    /** rest version 1.2. */
    String REST_1_2 = "REST1.2";
    
    /** rest version 1.3. */
    String REST_1_3 = "REST1.3";
    
    /**
     * Returns the name of the channel.
     * @return channel name.
     */
    String getName();
    
    /**
     * Sets the name of the channel.
     * @param name channel name.
     */
    void setName(String name);
    
    /**
     * Returns the suggested channel alias.
     * @return suggested channel alias.
     */
    String getSuggestedAlias();
    
    /**
     * Sets the suggested channel alias.
     * @param suggestedAlias channel alias.
     */
    void setSuggestedAlias(String suggestedAlias);
    
    /**
     * Returns a summary about the channel.
     * @return summary.
     */
    String getSummary();
    
    /**
     * Sets the summary about the channel.
     * @param summary the summary.
     */
    void setSummary(String summary);
    
    /**
     * Returns the validation package.
     * @return validation package.
     */
    IPackageVersion getValidationPackage();
    
    /**
     * Sets the validation package.
     * @param version validation package version.
     */
    void setValidationPackage(IPackageVersion version);
    
    /**
     * Returns the primary server.
     * @return primary server.
     */
    IServer getPrimaryServer();
    
    /**
     * Sets the primary server.
     * @param server primary server.
     */
    void setPrimaryServer(IServer server);
    
    /**
     * Returns the mirrors.
     * @return iterable for mirrors.
     */
    Iterable<IServer> getMirrors();
    
    /**
     * Adds a new mirror.
     * @param server mirror.
     */
    void addMirror(IServer server);

    /**
     * Initializes the channel.
     * 
     * <p>
     * Implementors should throw an IllegalStateException if this method is called twice.
     * </p>
     * 
     * @param pearUtility the pear utility.
     * @param channelName the channel name.
     * 
     * @throws PhpException thrown on php execution errors.
     */
    void initialize(IPearUtility pearUtility, String channelName) throws PhpException;
    
    /**
     * Returns the rest url that matches the rest version; if the version is not found the
     * newest rest version is returned.
     * @param version version number.
     * @return rest url.
     */
    String getRestUrl(String version);
    
    /**
     * Initializes the local packages.
     * 
     * @param ignoreUnresolvablePackages true to ignore unresolvable packages.
     * @param doNotReadInstalled true to not read local installed packages.
     * 
     * @throws PhpException thrown on php execution errors.
     */
    void initializePackages(boolean ignoreUnresolvablePackages, boolean doNotReadInstalled) throws PhpException;
    
    /**
     * Reinitialize the known packages.
     * @throws PhpException thrown on php errors
     */
    void reinitializeInstalledPackages() throws PhpException;
    
    /**
     * Returns the known packages.
     * 
     * @return Iterable of the known packages.
     * 
     * @throws PhpException thrown on php execution errors.
     */
    Iterable<IPackage> getKnownPackages() throws PhpException;
    
    /**
     * Returns the installed packages.
     * 
     * @return Iterable of the known packages.
     * 
     * @throws PhpException thrown on php execution errors.
     */
    Iterable<IPackage> getInstalledPackages() throws PhpException;
    
    /**
     * Returns the package with given name.
     * 
     * @param name Package with given name.
     * 
     * @return The package or {@code null} if the package is unknown.
     * 
     * @throws PhpException thrown on php execution erorrs.
     */
    IPackage getPackage(String name) throws PhpException;
    
    /**
     * Adds a new package.
     * 
     * @param pkg The package.
     * 
     * @throws PhpException thrown on php execution errors.
     */
    void addPackage(IPackage pkg) throws PhpException;
    
    /**
     * Returns the categories.
     * @return categories
     * @throws PhpException thrown on php execution errors.
     */
    Iterable<ICategory> getCategories() throws PhpException;
    
    /**
     * Adds a new category.
     * @param category category to be added.
     * @throws PhpException thrown on php execution errors.
     */
    void addCategory(ICategory category) throws PhpException;
    
    /**
     * Returns the list of maintainers.
     * @return maintainers.
     * @throws PhpException thrown on php execution errors.
     */
    Iterable<IMaintainer> getMaintainers() throws PhpException;
    
    /**
     * Adds a channel maintainer.
     * @param maintainer maintainer to be added.
     * @throws PhpException thrown on php execution errors.
     */
    void addMaintainer(IMaintainer maintainer) throws PhpException;

    /**
     * Returns the pear utility.
     * @return pear utility.
     */
    IPearUtility getPearUtility();
    
}
