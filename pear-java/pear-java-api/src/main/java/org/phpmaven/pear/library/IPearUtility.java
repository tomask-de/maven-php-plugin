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

import java.io.File;

import org.phpmaven.phpexec.library.IPhpExecutableConfiguration;
import org.phpmaven.phpexec.library.PhpException;


/**
 * The pear utility.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 0.1.0
 */
public interface IPearUtility {
    
    /**
     * Configures the pear utility.
     * 
     * <p>
     * Implementors should throw an IllegalStateException if this method is called twice.
     * </p>
     * 
     * @param installDir the install dir.
     * @param config the php executable config to be used.
     * 
     * @throws IllegalStateException thrown if the php executable cannot be created
     */
    void configure(File installDir, IPhpExecutableConfiguration config, Iterable<IPearProxy> proxies);
    
    /**
     * Initialized the known channels and ensures that there is a pear installation at {@link #installDir}.
     * @param readRemote true to read the remote channels
     * @throws PhpException thrown if something is wrong.
     */
    void initChannels(boolean readRemote) throws PhpException;
    
    /**
     * Returns true if pear is installed.
     * 
     * @return true if pear is installed.
     * 
     * @throws PhpException thrown on php execution errors.
     */
    boolean isInstalled() throws PhpException;
    
    /**
     * Installs pear if it is not yet installed.
     * 
     * @param autoUpdatePear true to update pear automatically after installing.
     * 
     * @throws PhpException thrown on php execution errors.
     */
    void installPear(boolean autoUpdatePear) throws PhpException;
    
    /**
     * Deletes the pear installation.
     * 
     * @throws PhpException thrown on php execution errors.
     */
    void uninstall() throws PhpException;
    
    /**
     * Returns the installation directory.
     * 
     * @return installation directory.
     */
    File getInstallDir();
    
    /**
     * Lists the known pear channels.
     * 
     * @return list of known pear channels.
     * 
     * @throws PhpException thrown on php execution errors.
     */
    Iterable<IPearChannel> listKnownChannels() throws PhpException;
    
    /**
     * Adds a pear channel.
     * 
     * @param channelName Host name/ channel name.
     * @return the pear channel.
     * 
     * @throws PhpException thrown on php execution errors.
     */
    IPearChannel channelDiscover(String channelName) throws PhpException;
    
    /**
     * Adds a pear channel (without fetching it remote).
     * 
     * @param channelName Host name.
     * @param alias the channel alias
     * @param summary the summary
     * @return the pear channel.
     * 
     * @throws PhpException thrown on php execution errors.
     */
    IPearChannel channelAdd(String channelName, String alias, String summary) throws PhpException;
    
    /**
     * Adds a pear channel.
     * 
     * @param channel path to channel.xml file.
     * @return the pear channel.
     * 
     * @throws PhpException thrown on php execution errors.
     */
    IPearChannel channelDiscoverLocal(File channel) throws PhpException;
    
    /**
     * Upgrades the installed packages.
     * 
     * @throws PhpException thrown on php execution errors.
     */
    void upgrade() throws PhpException;
    
    /**
     * Lookup a pear channel; does not discover the channel.
     * 
     * @param channelName Host name/ channel name.
     * 
     * @return Discovered channel or {@code null} if the channel is not known.
     * 
     * @throws PhpException thrown on php execution errors.
     */
    IPearChannel lookupChannel(String channelName) throws PhpException;
    
    /**
     * Clear PEAR cache.
     * 
     * @throws PhpException thrown on php execution errors.
     */
    void clearCache() throws PhpException;
    
    /**
     * Executes the pear command.
     * 
     * @param arguments Arguments for pear.
     * 
     * @return The pear output.
     * 
     * @throws PhpException thrown on execution errors.
     */
    String executePearCmd(String arguments) throws PhpException;
    
    /**
     * returns the temp dir.
     * @return temp dir.
     * @throws PhpException thrown on problems while reading the conf.
     */
    File getTempDir() throws PhpException;
    
    /**
     * returns the temp download dir.
     * @return temp download dir.
     * @throws PhpException thrown on problems while reading the conf.
     */
    File getDownloadDir() throws PhpException;
    
    /**
     * returns the bin dir.
     * @return bin dir.
     * @throws PhpException thrown on problems while reading the conf.
     */
    File getBinDir() throws PhpException;
    
    /**
     * returns the php dir.
     * @return php dir.
     * @throws PhpException thrown on problems while reading the conf.
     */
    File getPhpDir() throws PhpException;
    
    /**
     * returns the doc dir.
     * @return doc dir.
     * @throws PhpException thrown on problems while reading the conf.
     */
    File getDocDir() throws PhpException;
    
    /**
     * returns the data dir.
     * @return data dir.
     * @throws PhpException thrown on problems while reading the conf.
     */
    File getDataDir() throws PhpException;
    
    /**
     * returns the cfg dir.
     * @return cfg dir.
     * @throws PhpException thrown on problems while reading the conf.
     */
    File getCfgDir() throws PhpException;
    
    /**
     * returns the www dir.
     * @return www dir.
     * @throws PhpException thrown on problems while reading the conf.
     */
    File getWwwDir() throws PhpException;
    
    /**
     * returns the test dir.
     * @return test dir.
     * @throws PhpException thrown on problems while reading the conf.
     */
    File getTestDir() throws PhpException;
    
    /**
     * Returns true if given Package is a core package; core packages are automatically
     * installed while pear itself is installed. 
     * @param channel pear channel
     * @param pkg pear package name
     * @return true if this is a core package.
     */
    boolean isPearCorePackage(final String channel, final String pkg);

    /**
     * Creates a new empty channel object
     * @return new object
     */
    IPearChannel createChannel();
    
    /**
     * Creates a new empty package version object
     * @return new object
     */
    IPackageVersion createPackageVersion();
    
    /**
     * Creates a new empty version object
     * @return new object
     */
    IVersion createVersion();
    
    /**
     * Creates a new server object
     * @return new object
     */
    IServer createServer();
    
    /**
     * Creates a new package object
     * @return new object
     */
    IPackage createPackage();
    
    /**
     * Creates a new category object
     * @return new object
     */
    ICategory createCategory();
    
    /**
     * Creates a new empty maintainer object
     * @return new object
     */
    IMaintainer createMaintainer();

	/**
	 * Creates a new empty xml rpc server object
	 * @return new object
	 */
	IXmlRpcServer createXmlRpcServer();

	/**
	 * Creates a new empty xml rpc function object
	 * @return new object
	 */
	IXmlRpcFunction createXmlRpcServerFunction();

	/**
	 * Creates a new empty rest server object
	 * @return new object
	 */
	IRestServer createRestServer();

	/**
	 * Creates a new empty rest base url object
	 * @return new object
	 */
	IRestBaseUrl createRestBaseUrl();

	/**
	 * Creates a new empty soap server object
	 * @return new object
	 */
	ISoapServer createSoapServer();

	/**
	 * Creates a new empty soap function object
	 * @return new object
	 */
	ISoapFunction createSoapFunction();

	/**
	 * Creates a new empty dependency object
	 * @return new object
	 */
	IDependency createDependency();
    
}
