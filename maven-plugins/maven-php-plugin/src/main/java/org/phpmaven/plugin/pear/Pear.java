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

package org.phpmaven.plugin.pear;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.phpmaven.plugin.php.IPhpExecution;

/**
 * TODO Documentation
 * 
 * @author mepeisen
 */
public class Pear
{

	/**
	 * The installation directory of the php
	 */
	private File phpInstallationDir;
	
	/**
	 * The maven log to be used for non wrapped output
	 */
	private Log log;
	
	/**
	 * php execution helper
	 */
	private IPhpExecution execution;

	/**
	 * The pear installation
	 */
	public Pear()
	{
		// does nothing
	}

	/**
	 * Sets the maven log for non wrapped output
	 * @param log
	 */
	public void setLog(Log log)
	{
		this.log = log;
	}

	public File getPhpInstallationDir() throws MojoExecutionException 
	{
		if (phpInstallationDir == null)
		{
			this.phpInstallationDir = PhpInfo.getDefault().getInstallDir();
		}
		return phpInstallationDir;
	}

	public void setPhpInstallationDir(File phpInstallationDir)
	{
		this.phpInstallationDir = phpInstallationDir;
	}
	
	/**
	 * Performs a channel discover
	 * @param channel
	 * @throws MojoExecutionException 
	 */
	public void channelDiscover(String channel) throws MojoExecutionException
	{
		final Proc proc = getProc();
		proc.addArgument("channel-discover"); //$NON-NLS-1$
		proc.addArgument(channel);
		proc.execute();
		// TODO fetch result and check if the channel was installed.
	}
    
    /**
     * Clears the cache
     * @param channel
     * @param pkg
     * @param version
     * @throws MojoExecutionException
     */
    public void clearCache() throws MojoExecutionException
    {
        final Proc proc = getProc();
        proc.addArgument("clear-cache"); //$NON-NLS-1$
        proc.execute();
    }
    
    /**
     * Installs a pear package
     * @param channel
     * @param pkg
     * @param version
     * @throws MojoExecutionException
     */
    public void install(String channel, String pkg, String version) throws MojoExecutionException
    {
        final Proc proc = getProc();
        proc.addArgument("install"); //$NON-NLS-1$
        proc.addArgument("--alldeps"); //$NON-NLS-1$
        proc.addArgument("--force"); //$NON-NLS-1$
        proc.addArgument("--loose"); //$NON-NLS-1$
        proc.addArgument(channel + "/" + pkg + "-" + version); //$NON-NLS-1$ //$NON-NLS-2$
        proc.execute();
    }
    
    /**
     * Installs a pear package; forces installation (will upgrade/downgrade if possible)
     * @param channel
     * @param pkg
     * @param version
     * @return info
     * @throws MojoExecutionException
     */
    public PearPackageInfo forceInstall(String channel, String pkg, String version) throws MojoExecutionException
    {
        // look if the version is already installed
        PearPackageInfo info = this.getPackageInfo(channel, pkg);
        if (info != null && info.getVersion() != null)
        {
            if (!info.getVersion().equalsIgnoreCase(version))
            {
                this.log.info("version mismatch for pear package " + pkg + ": need " + version + " but version " + info.getVersion() + " is already installed."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                this.log.info("trying to uninstall");
                this.uninstall(channel, pkg);
                this.install(channel, pkg, version);
            }
        }
        else
        {
            // install it
            this.install(channel, pkg, version);
        }

        // security check
        info = this.getPackageInfo(channel, pkg);
        if (info == null || info.getVersion() == null)
        {
            log.info("installation failed. No package info available."); //$NON-NLS-1$
            throw new MojoExecutionException("installation failed. No package info available."); //$NON-NLS-1$
        }
        
        if (!info.getVersion().equalsIgnoreCase(version))
        {
            log.info("uninstall failed. Version mismatch for pear package " + pkg + ": need " + version + " but version " + info.getVersion() + " is already installed."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            throw new MojoExecutionException("uninstall failed. Version mismatch for pear package " + pkg + ": need " + version + " but version " + info.getVersion() + " is already installed."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        }
        
        return info;
    }
    
    /**
     * Installs a pear package
     * @param channel
     * @param pkg
     * @param version
     * @throws MojoExecutionException
     */
    public void uninstall(String channel, String pkg) throws MojoExecutionException
    {
        final Proc proc = getProc();
        proc.addArgument("uninstall"); //$NON-NLS-1$
        proc.addArgument("--nodeps"); //$NON-NLS-1$
        proc.addArgument(channel + "/" + pkg ); //$NON-NLS-1$
        proc.execute();
    }
    
    /**
     * returns a pear package
     * @param channel
     * @param pkg
     * @throws MojoExecutionException
     */
    public PearPackageInfo getPackageInfo(String channel, String pkg) throws MojoExecutionException
    {
        final Proc proc = getProc();
        proc.addArgument("info"); //$NON-NLS-1$
        proc.addArgument(channel + "/" + pkg); //$NON-NLS-1$
        proc.setRedirectOutputStream(true);
        proc.execute();
        return new PearPackageInfo(proc.getOutput(), channel);
    }
	
	/**
	 * Returns a list of php files
	 * @param channel
	 * @param pkg
	 * @return php files
	 * @throws MojoExecutionException
	 */
	public Iterable<String> getPhpFiles(String channel, String pkg) throws MojoExecutionException
	{
		final Proc proc = getProc();
		proc.addArgument("list-files"); //$NON-NLS-1$
		proc.addArgument(channel + "/" + pkg); //$NON-NLS-1$
		proc.setRedirectOutputStream(true);
		proc.execute();
		final String output = proc.getOutput();
		try
		{
    		final List<String> files = new ArrayList<String>();
    		final StringTokenizer tokenizer = new StringTokenizer(output.trim(), "\n"); //$NON-NLS-1$
    		String lastToken = null;
    		tokenizer.nextToken();tokenizer.nextToken();tokenizer.nextToken(); // table headers...
    		while (tokenizer.hasMoreTokens())
    		{
    			String token = tokenizer.nextToken();
    			if (token.startsWith(" ")) // Fixed multi line... //$NON-NLS-1$
    			{
    				lastToken = lastToken + " " + token.trim(); //$NON-NLS-1$
    			}
    			else
    			{
    				if (lastToken != null && lastToken.startsWith("php")) //$NON-NLS-1$
    				{
    					files.add(lastToken.substring(3).trim());
    				}
    				lastToken = token.trim();
    			}
    		}
    		if (lastToken.startsWith("php")) //$NON-NLS-1$
    		{
    			files.add(lastToken.substring(3).trim());
    		}
    		return Collections.unmodifiableList(files);
		}
		catch (Exception ex)
		{
		    this.log.debug("Problems reading pear output:\n" + output);
		    throw new MojoExecutionException("Problems reading pear output.", ex);
		}
	}
	
	/**
	 * Returns a list of pear channels
	 * @return pear files
	 * @throws MojoExecutionException
	 */
	public Set<String> getPearChannels() throws MojoExecutionException
	{
		final Proc proc = getProc();
		proc.addArgument("list-channels"); //$NON-NLS-1$
		proc.setRedirectOutputStream(true);
		proc.execute();
		final String output = proc.getOutput();
		final Set<String> channels = new HashSet<String>();
		final StringTokenizer tokenizer = new StringTokenizer(output.trim(), "\n"); //$NON-NLS-1$
		tokenizer.nextToken();tokenizer.nextToken();tokenizer.nextToken(); // table headers...
		while (tokenizer.hasMoreTokens())
		{
			String token = tokenizer.nextToken().trim();
			channels.add(new StringTokenizer(token, " ").nextToken()); //$NON-NLS-1$
		}
		return Collections.unmodifiableSet(channels);
	}
	
	protected Proc getProc() throws MojoExecutionException
	{
//		final Php php = new Php();
//		php.setPhpInstallDir(getPhpInstallationDir());
//		php.setWorkingDirectory(getPhpInstallationDir());
//		final File pearPath = new File(getPhpInstallationDir(), "PEAR").getAbsoluteFile();
//		php.addIncludePath(pearPath);
//		php.addEnv("PHP_PEAR_INSTALL_DIR", pearPath.getAbsolutePath());
//		php.addEnv("PHP_PEAR_BIN_DIR", getPhpInstallationDir().getAbsolutePath());
//		php.addEnv("PHP_PEAR_PHP_BIN", new File(getPhpInstallationDir(), "php").getAbsolutePath());
//		php.addPhpArgument("-C");
//		php.addIniOption("output_buffering", "1");
//		php.addIniOption("safe_mode", "0");
//		php.addIniOption("open_basedir", "");
//		php.addIniOption("auto_prepend_file", "");
//		php.addIniOption("auto_append_file", "");
//		php.addIniOption("variables_order", "EGPCS");
//		php.addIniOption("register_argc_argv", "On");
//		php.setPhpFile("pearcmd.php");
//		php.setLog(log);
//		return php;
		
		final Proc proc = new Proc();
		if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) //$NON-NLS-1$ //$NON-NLS-2$
		{
			proc.setCommand(new File(getPhpInstallationDir(), "pear.bat").getAbsolutePath()); //$NON-NLS-1$
		}
		else
		{
			proc.setCommand(new File(getPhpInstallationDir(), "pear").getAbsolutePath()); //$NON-NLS-1$
		}
		proc.setWorkingDirectory(getPhpInstallationDir());
		proc.setLog(log);
		return proc;
	}

}
