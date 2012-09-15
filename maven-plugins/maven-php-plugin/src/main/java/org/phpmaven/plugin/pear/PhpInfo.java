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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * A class to get various information on php installations
 * 
 * TODO migrate
 * 
 * @author mepeisen
 */
public class PhpInfo
{
	
	/**
	 * The default php info (default installation)
	 */
	private static PhpInfo DEFAULT;
	
	/**
	 * Available installations
	 */
	private static final Map<File, PhpInfo> INSTALLATIONS = new HashMap<File, PhpInfo>();

	/**
	 * The installation dir
	 */
	private File installDir;

	/**
	 * The ini file prolog
	 */
	private static final String INI_FILE_PROLOG = "Loaded Configuration File:"; //$NON-NLS-1$
	
	/**
	 * The alternative ini file
	 */
	private File iniFile;

	/**
	 * All ini options
	 */
	private Map<String, String> iniOptions;
	
	/**
	 * The default include path list
	 */
	private List<String> includePath;

	/**
	 * The php version
	 */
	private String phpVersion;
	
	/**
	 * The PEAR version prolog
	 */
	private static final String PEAR_VERSION_PROLOG = "PEAR Version:"; //$NON-NLS-1$
	
	/**
	 * The pear version
	 */
	private String pearVersion;

	/**
	 * Constructor
	 * 
	 * @param installDir
	 */
	private PhpInfo(final File installDir)
	{
		this.installDir = installDir;
	}
	
	/**
	 * Returns the default php info
	 * 
	 * @return php info
	 * 
	 * @throws MojoExecutionException 
	 */
	public static PhpInfo getDefault() throws MojoExecutionException
	{
		if (DEFAULT == null)
		{
			final String path = System.getenv("PATH"); //$NON-NLS-1$
			final StringTokenizer tokenizer = new StringTokenizer(path, File.pathSeparator);
			while (tokenizer.hasMoreTokens())
			{
				final File dir = new File(tokenizer.nextToken());
				final File exe1 = new File(dir, "php.exe"); //$NON-NLS-1$
				final File exe2 = new File(dir, "php"); //$NON-NLS-1$
				if ((exe1.exists() && exe1.isFile()) || (exe2.exists() && exe2.isFile()))
				{
					final Php php = new Php();
					php.setPhpInstallDir(dir);
		    		php.setCliScript("echo('Hello World!');"); //$NON-NLS-1$
		    		php.setRedirectOutputStream(true);
		    		try
		    		{
		    			php.execute();
		    			// we found it!
		    			DEFAULT = new PhpInfo(dir);
		    			INSTALLATIONS.put(dir, DEFAULT);
		    			return DEFAULT;
		    		}
		    		catch (MojoExecutionException e)
		    		{
		    			// simply ignore
		    		}
				}
			}
			
			throw new MojoExecutionException("Unable to find php executable in path"); //$NON-NLS-1$
		}
		return DEFAULT;
	}
	
	/**
	 * Returns the info for given installation
	 * 
	 * @param installDir
	 * 
	 * @return php info
	 * 
	 * @throws MojoExecutionException 
	 */
	public static PhpInfo get(File installDir) throws MojoExecutionException
	{
		if (!INSTALLATIONS.containsKey(installDir))
		{
			// only check if there is a php exeutable
			// otherwise a mojo execution exception will be thrown
			final Php php = new Php();
			php.setPhpInstallDir(installDir);
    		php.setCliScript("echo('Hello World!');"); //$NON-NLS-1$
    		php.setRedirectOutputStream(true);
    		php.execute();
			INSTALLATIONS.put(installDir, new PhpInfo(installDir));
		}
		return INSTALLATIONS.get(installDir);
	}

	/**
	 * Returns the installation dir
	 * @return
	 */
	public File getInstallDir()
	{
		return installDir;
	}

	/**
	 * Returns the php version string
	 * @return
	 * @throws MojoExecutionException
	 */
	public String getPhpVersion() throws MojoExecutionException
	{
		if (phpVersion == null)
		{
			final Php php = new Php();
			php.setPhpInstallDir(installDir);
			php.setWorkingDirectory(installDir);
    		php.setCliScript("echo(phpversion());"); //$NON-NLS-1$
    		php.setRedirectOutputStream(true);
    		php.execute();
    		phpVersion = php.getOutput().trim();
		}
		return phpVersion;
	}

	/**
	 * Returns the pear version string
	 * @return
	 * @throws MojoExecutionException
	 */
	public String getPearVersion() throws MojoExecutionException
	{
		if (pearVersion == null)
		{
			final Php php = new Php();
			php.setPhpInstallDir(installDir);
			php.setWorkingDirectory(installDir);
    		php.setPhpFile(new File(installDir, "pear").getAbsolutePath()); //$NON-NLS-1$
    		php.addCliArgument("info"); //$NON-NLS-1$
    		php.setRedirectOutputStream(true);
    		php.execute();
    		final StringTokenizer tokenizer = new StringTokenizer(php.getOutput().trim(), "\n"); //$NON-NLS-1$
    		while (tokenizer.hasMoreTokens())
    		{
    			String token = tokenizer.nextToken().trim();
    			if (token.substring(0, PEAR_VERSION_PROLOG.length()).equals(PEAR_VERSION_PROLOG))
    			{
    				phpVersion = token.substring(PEAR_VERSION_PROLOG.length()).trim();
    				return phpVersion;
    			}
    		}
		}
		return phpVersion;
	}
	
	/**
	 * ini file indicating that no file was found
	 */
	private static File INI_DUMMY = new File("foo"); //$NON-NLS-1$

	/**
	 * Returns the ini file
	 * @return the ini file or null if no ini file was found
	 * @throws MojoExecutionException 
	 */
	public File getIniFile() throws MojoExecutionException
	{
		if (iniFile == null)
		{
			final Php php = new Php();
			php.setPhpInstallDir(installDir);
			php.setWorkingDirectory(installDir);
    		php.addPhpArgument("--ini"); //$NON-NLS-1$
    		php.setRedirectOutputStream(true);
    		php.execute();
    		final StringTokenizer tokenizer = new StringTokenizer(php.getOutput().trim(), "\n"); //$NON-NLS-1$
    		while (tokenizer.hasMoreTokens())
    		{
    			String token = tokenizer.nextToken().trim();
    			if (token.substring(0, INI_FILE_PROLOG.length()).equals(INI_FILE_PROLOG))
    			{
    				final String str = token.substring(INI_FILE_PROLOG.length()).trim();
    				final File file = new File(str);
    				if (file.exists())
    				{
    					iniFile = file;
    					return iniFile;
    				}
					iniFile = INI_DUMMY;
					return null;
    			}
    		}
		}
		return iniFile == INI_DUMMY ? null : iniFile;
	}

	/**
	 * Returns the ini options
	 * @return
	 * @throws MojoExecutionException 
	 */
	public Map<String, String> getIniOptions() throws MojoExecutionException
	{
		if (iniOptions == null)
		{
			iniOptions = new HashMap<String, String>();
			final Php php = new Php();
			php.setPhpInstallDir(installDir);
			php.setWorkingDirectory(installDir);
    		php.setCliScript("foreach(ini_get_all() as $k=>$a) echo($k.':'.$a['local_value'].PHP_EOL);"); //$NON-NLS-1$
    		php.setRedirectOutputStream(true);
    		php.execute();
    		final String ini = php.getOutput().trim();
    		final StringTokenizer tokenizer = new StringTokenizer(ini, "\n"); //$NON-NLS-1$
    		while (tokenizer.hasMoreTokens())
    		{
    			final String token = tokenizer.nextToken().trim();
    			final int pos = token.indexOf(':');
    			final String key = token.substring(0, pos);
    			final String value = token.substring(pos + 1);
    			iniOptions.put(key, value);
    		}
		}
		return Collections.unmodifiableMap(iniOptions);
	}

	/**
	 * Returns the include path
	 * @return
	 * @throws MojoExecutionException 
	 */
	public Iterable<String> getIncludePath() throws MojoExecutionException
	{
		if (includePath == null)
		{
			includePath = new ArrayList<String>();
			final Php php = new Php();
			php.setPhpInstallDir(installDir);
			php.setWorkingDirectory(installDir);
    		php.setCliScript("echo(implode(PHP_EOL,explode(PATH_SEPARATOR,get_include_path())));"); //$NON-NLS-1$
    		php.setRedirectOutputStream(true);
    		php.execute();
    		final String paths = php.getOutput().trim();
    		final StringTokenizer tokenizer = new StringTokenizer(paths, "\n"); //$NON-NLS-1$
    		while (tokenizer.hasMoreTokens())
    		{
    			includePath.add(tokenizer.nextToken().trim());
    		}
    	}
		return Collections.unmodifiableList(includePath);
	}
	
}
