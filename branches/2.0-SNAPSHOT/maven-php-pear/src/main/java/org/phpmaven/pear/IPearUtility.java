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

import org.apache.maven.plugin.logging.Log;
import org.phpmaven.exec.PhpException;


/**
 * The pear utility.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
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
     * @param logger the logger.
     */
    void configure(File installDir, Log logger);
    
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
     * Adds a pear channel.
     * 
     * @param channel channel.xml file.
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
     * Converts a maven version to a pear version.
     * @param src maven version
     * @return pear version
     */
    String convertMavenVersionToPearVersion(String src);
    
    /**
     * Converts a pear version to a maven version.
     * @param src pear version.
     * @return maven version.
     */
    String convertPearVersionToMavenVersion(String src);
    
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
     * Installs given artifact from repository.
     * @param groupId maven group id.
     * @param artifactId maven artifact id.
     * @param version maven version.
     * @throws PhpException the php exception is thrown on installation errors.
     */
    void installFromMavenRepository(final String groupId, final String artifactId, final String version)
        throws PhpException;
    
    /**
     * Returns true if given Package is a core package; core packages are automatically
     * installed while pear itself is installed. 
     * @param groupId group id
     * @param artifactId artifact id
     * @return true if this is a core package.
     */
    boolean isMavenCorePackage(final String groupId, final String artifactId);
    
    /**
     * Returns true if given Package is a core package; core packages are automatically
     * installed while pear itself is installed. 
     * @param channel pear channel
     * @param pkg pear package name
     * @return true if this is a core package.
     */
    boolean isPearCorePackage(final String channel, final String pkg);

}
