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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * Goal to copy the resources and classes to the output folder.
 *
 * @goal process-resources-pear
 * @phase process-resources
 * 
 * TODO migrate
 * 
 * @author mepeisen
 */
public class ProcessResourcesPearMojo extends DefaultMojo
{

	/**
	 * @parameter
	 * @required
	 */
    private List<String> pearChannels = new ArrayList<String>();
    
    /**
     * @parameter
     * @required
     */
    private String pearChannelAlias;
    
    /**
     * @parameter
     * @required
     */
    private String pearPackage;
    
    /**
     * @parameter
     */
    private boolean pearFetchDependencies;
    
    /**
     * @parameter
     */
    private String pearPackageVersion;
    
    /**
     * @inheritDoc
     */
    public void execute() throws MojoExecutionException
    {
        if (this.pearPackageVersion == null) {
            this.pearPackageVersion = this.getMavenProject().getVersion();
        }
        final File checkFile = new File(this.getBuildDirectory(), "pear.check");
        if (checkFile.exists())
        {
            this.getLog().info("File pear.check already exists. Skipping fetching everything from pear. Try cleaning before.");
            return;
        }
        
        final Pear pear = new Pear();
    	pear.setLog(getLog());
        pear.clearCache();
    	final Set<String> installedPearChannels = pear.getPearChannels();
    	for (final String channel : pearChannels)
    	{
    		if (!installedPearChannels.contains(channel))
    		{
    			this.getLog().info("channel discover " + channel); //$NON-NLS-1$
    			pear.channelDiscover(channel);
    		}
    	}
    	
    	pear.forceInstall(this.pearChannelAlias, this.pearPackage, pearPackageVersion);

    	// create the classes dir
        if (!this.getOutputDirectory().exists()) {
            this.getOutputDirectory().mkdirs();
        }
            
    	// fetch files
    	final Set<String> fetchedPackages = new HashSet<String>();
    	this.fetchPackage(pear, fetchedPackages, this.pearChannelAlias, this.pearPackage, this.pearFetchDependencies);
    	
    	try
    	{
    	    if (!checkFile.getParentFile().exists()) {
    	        checkFile.getParentFile().mkdirs();
    	    }
    	    checkFile.createNewFile();
    	}
    	catch (Exception ex)
    	{
    	    throw new MojoExecutionException("Failed creating the check file", ex);
    	}
    }

    /**
     * fetches output files for pear package
     * @param pear
     * @param fetchedPackages
     * @param channel
     * @param pkg
     * @param fetchDependencies
     * @throws MojoExecutionException 
     */
	private void fetchPackage(Pear pear, Set<String> fetchedPackages, String channel, String pkg, boolean fetchDependencies) throws MojoExecutionException
	{
		final String key = channel + "/" + pkg; //$NON-NLS-1$
		final File pearPath = new File(pear.getPhpInstallationDir(), "PEAR").getAbsoluteFile(); //$NON-NLS-1$
		if (!fetchedPackages.contains(key))
		{
			this.getLog().info("copying " + key); //$NON-NLS-1$
			fetchedPackages.add(key);
			for (final String filename : pear.getPhpFiles(channel, pkg))
			{
				final File file = new File(filename);
				try
				{
					final String relativeName = getRelativePath(file);
					if (relativeName.length() == 0)
					{
						throw new MojoExecutionException("Detected file outside pear root: " + pearPath + " <-> " + filename); //$NON-NLS-1$ //$NON-NLS-2$
					}
					copyFile(file, new File(this.getOutputDirectory(), relativeName));
				}
				catch (IOException e)
				{
					throw new MojoExecutionException("Problems copying resource", e); //$NON-NLS-1$
				}
			}
			
			if (this.pearFetchDependencies)
			{
    			for (final PearPackageInfo.PearDependency dep : pear.getPackageInfo(channel, pkg).getRequiredDependencies())
    			{
    				this.fetchPackage(pear, fetchedPackages, dep.getChannelName(), dep.getPkgName(), true);
    			}
			}
		}
	}
	
	private static List<String> getPathList(File f) throws IOException
	{
		List<String> l = new ArrayList<String>();
		File r = f.getCanonicalFile();
		while(r != null)
		{
			l.add(r.getName());
			r = r.getParentFile();
		}
		return l;
	}
	
	private static String getRelativePath(File f) throws IOException
	{
		final List<String> filelist = getPathList(f);
		Collections.reverse(filelist);
		boolean found = false;
		StringBuilder b = new StringBuilder();
		for (final String e : filelist)
		{
			if (found)
			{
				if (b.length() > 0)
				{
					b.append(File.separator);
				}
				b.append(e);
			}
			else if ("PEAR".equals(e)) //$NON-NLS-1$
			{
				found = true;
			}
		}
		
		return b.toString();
	}
    
}
