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
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

/**
 * A class to hold php runtime environments being executed
 * TODO migrate
 * 
 * @author mepeisen
 */
public class Php
{

	private Proc proc = new Proc();
	
	/**
	 * The php file being executed
	 */
	private String phpFile;
	
	/**
	 * The alternative ini file (-c file)
	 */
	private File iniFile;
	
	/**
	 * Cli script to be executed
	 */
	private String cliScript;

	/**
	 * Additional ini options (-D key=value)
	 */
	private Properties iniOptions = new Properties();
	
	/**
	 * Command line argument sent to the executed script
	 */
	private List<String> cliArguments = new ArrayList<String>();
	
	/**
	 * Command line argument sent to php itself
	 */
	private List<String> phpArguments = new ArrayList<String>();
	
	/**
	 * The include path list
	 */
	private List<String> includePath = new ArrayList<String>();
	
	/**
	 * The php installation dir to be used
	 */
	private File phpInstallDir;

	/**
	 * Sets the maven log for non wrapped output
	 * @param log
	 */
	public void setLog(Log log)
	{
		this.proc.setLog(log);
	}
	
	/**
	 * The working directory
	 * @return
	 */
	public File getWorkingDirectory()
	{
		return this.proc.getWorkingDirectory();
	}

	/**
	 * The working directory
	 * @param workingDirectory
	 */
	public void setWorkingDirectory(File workingDirectory)
	{
		this.proc.setWorkingDirectory(workingDirectory);
	}

	/**
	 * Adds an environment variable
	 * @param key
	 * @param value
	 */
	public void addEnv(String key, String value)
	{
		this.proc.addEnv(key, value);
	}
	
	/**
	 * True to redirect the output to a string
	 * @return
	 */
	public boolean isRedirectOutputStream()
	{
		return this.proc.isRedirectOutputStream();
	}

	/**
	 * True to redirect the output to a string
	 * @return
	 */
	public void setRedirectOutputStream(boolean redirectOutputStream)
	{
		this.proc.setRedirectOutputStream(redirectOutputStream);
	}

	/**
	 * Redirected console output
	 * @return
	 */
	public String getOutput()
	{
		return this.proc.getOutput();
	}

	/**
	 * The php file to be executed
	 * @return
	 */
	public String getPhpFile()
	{
		return phpFile;
	}

	/**
	 * The php file to be executed
	 * @param phpFile
	 */
	public void setPhpFile(String phpFile)
	{
		this.phpFile = phpFile;
	}

	/**
	 * The ini file
	 * @return
	 */
	public File getIniFile()
	{
		return iniFile;
	}

	/**
	 * The ini file
	 * @param iniFile
	 */
	public void setIniFile(File iniFile)
	{
		this.iniFile = iniFile;
	}

	/**
	 * Adds an ini option
	 * @param key
	 * @param value
	 */
	public void addIniOption(String key, String value)
	{
		this.iniOptions.setProperty(key, value);
	}

	/**
	 * Returns the cli script to be executed
	 * @return
	 */
	public String getCliScript()
	{
		return cliScript;
	}

	/**
	 * Sets the cli script to be executed
	 * @param cliScript
	 */
	public void setCliScript(String cliScript)
	{
		this.cliScript = cliScript;
	}
	
	/**
	 * Adds a cli argument to be sent to the script
	 * @param arg
	 */
	public void addCliArgument(String arg)
	{
		this.cliArguments.add(arg);
	}
	
	/**
	 * Adds a argument to be sent to php itself
	 * @param arg
	 */
	public void addPhpArgument(String arg)
	{
		this.phpArguments.add(arg);
	}
	
	/**
	 * Returns the include path elements
	 * @return
	 */
	public Iterable<String> getIncludes()
	{
		return this.includePath;
	}
	
	/**
	 * Adds a include path directory element
	 * @param path
	 */
	public void addIncludePath(File path)
	{
		this.includePath.add(path.getAbsolutePath());
	}
	
	/**
	 * Adds a include path phar element
	 * @param phar
	 */
	public void addIncludePhar(File phar)
	{
		this.includePath.add("phar://" + phar.getAbsolutePath()); //$NON-NLS-1$
	}
	
	/**
	 * Executes the php command
	 * @throws MojoExecutionException
	 */
	public void execute() throws MojoExecutionException
	{
		this.proc.setCommand(this.phpInstallDir == null ? "php" : new File(this.phpInstallDir, "php").getAbsolutePath()); //$NON-NLS-1$ //$NON-NLS-2$
		for (final Entry<Object, Object> entry : iniOptions.entrySet())
		{
			this.proc.addArgument("-d"); //$NON-NLS-1$
			this.proc.addArgument(entry.getKey() + "=" + entry.getValue()); //$NON-NLS-1$
		}
		if (this.iniFile != null)
		{
			this.proc.addArgument("-i"); //$NON-NLS-1$
			this.proc.addArgument(iniFile.getAbsolutePath());
		}
		
		final StringBuilder beforeScript = new StringBuilder();
		if (this.includePath.size() != 0)
		{
			beforeScript.append("set_include_path("); //$NON-NLS-1$
			for (final String incEntry : this.includePath)
			{
				beforeScript.append("'" + incEntry.replace("\\", "\\\\") + "'.PATH_SEPARATOR."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			}
			beforeScript.append("get_include_path());"); //$NON-NLS-1$
		}
		
		if (beforeScript.length() > 0)
		{
			this.proc.addArgument("-B"); //$NON-NLS-1$
			if (cliScript != null)
			{
				beforeScript.append(cliScript);
			}
			else if (phpFile != null)
			{
				beforeScript.append("require('").append(phpFile.replace("\\", "\\\\")).append("');"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			}
			this.proc.addArgument(beforeScript.toString());
		}
		else if (cliScript != null)
		{
			this.proc.addArgument("-r"); //$NON-NLS-1$
			this.proc.addArgument(cliScript);
		}
		else if (phpFile != null)
		{
			this.proc.addArgument("-f"); //$NON-NLS-1$
			this.proc.addArgument(phpFile);
		}
		
		for (final String arg : phpArguments) this.proc.addArgument(arg);
		
		if (this.cliArguments.size() > 0)
		{
			this.proc.addArgument("--"); //$NON-NLS-1$
			for (final String arg : cliArguments) this.proc.addArgument(arg);
		}
		
		this.proc.execute();
	}

    /**
     * The php installation  dir to be used
     * @param phpInstallDir
     */
	public void setPhpInstallDir(File phpInstallDir)
	{
		this.phpInstallDir = phpInstallDir;
	}

	/**
	 * The php installation dir to be used
	 * @return
	 */
	public File getPhpInstallDir()
	{
		return phpInstallDir;
	}


}
