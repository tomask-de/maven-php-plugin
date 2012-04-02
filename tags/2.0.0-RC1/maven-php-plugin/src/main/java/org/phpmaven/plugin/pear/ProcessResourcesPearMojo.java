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
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.util.FileUtils;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.exec.PhpException;
import org.phpmaven.pear.IPackage;
import org.phpmaven.pear.IPackageVersion;
import org.phpmaven.pear.IPearChannel;
import org.phpmaven.pear.IPearConfiguration;
import org.phpmaven.pear.IPearUtility;
import org.phpmaven.plugin.build.AbstractPhpMojo;

/**
 * Goal to copy the resources and classes to the output folder.
 *
 * @goal process-resources-pear
 * @phase process-resources
 * 
 * @author mepeisen
 */
public class ProcessResourcesPearMojo extends AbstractPhpMojo
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
    private String pearPackageVersion;
    
    /**
     * @inheritDoc
     */
    public void execute() throws MojoExecutionException
    {
        if (this.pearPackageVersion == null) {
            this.pearPackageVersion = this.getProject().getVersion();
        }
        final File checkFile = new File(this.getProject().getBuild().getDirectory(), "pear.check");
        if (checkFile.exists())
        {
            this.getLog().info("File pear.check already exists. Skipping fetching everything from pear. Try cleaning before.");
            return;
        }
        
        try {
            final IPearConfiguration config = this.factory.lookup(
                    IPearConfiguration.class,
                    IComponentFactory.EMPTY_CONFIG,
                    this.getSession());
            config.setInstallDir(new File(this.getProject().getBuild().getDirectory(), "pear"));
            final IPearUtility utility = config.getUtility(this.getLog());
            if (!utility.isInstalled()) {
                utility.installPear(true);
            }
            for (final String channel : pearChannels) {
                utility.channelDiscover(channel);
            }
            
            final IPearChannel channel = utility.channelDiscover(this.pearChannelAlias);
            final IPackage pkg = channel.getPackage(this.pearPackage);
            final IPackageVersion version = pkg.getVersion(this.pearPackageVersion);
            if ("pear".equals(this.pearChannelAlias) && (
                    "Archive_Tar".equals(this.pearPackage)
                    || "Console_Getopt".equals(this.pearPackage)
                    || "PEAR".equals(this.pearPackage)
                    || "Structures_Graph".equals(this.pearPackage)
                    || "XML_Util".equals(this.pearPackage))) {
                // do not try to uninstall the core packages
                // downgrade pear itself before installing
                utility.executePearCmd("install --force pear/PEAR-1.8.0");
                version.install(true);
            } else {
                version.install();
            }
            
            this.fetchPackage(version);
            
            try {
                if (!checkFile.getParentFile().exists()) {
                    checkFile.getParentFile().mkdirs();
                }
                checkFile.createNewFile();
            } catch (IOException ex) {
                throw new MojoExecutionException("Failed creating the check file", ex);
            }
        } catch (PhpException ex) {
            throw new MojoExecutionException("Failed executing pear", ex);
        } catch (PlexusConfigurationException ex) {
            throw new MojoExecutionException("Failed executing pear", ex);
        } catch (ComponentLookupException ex) {
            throw new MojoExecutionException("Failed executing pear", ex);
        }
    }

    /**
     * @param version
     * @throws MojoExecutionException 
     */
    private void fetchPackage(IPackageVersion version) throws MojoExecutionException {
        this.getLog().info("copying content");
        
        try {
            final IPackage pkg = version.getPackage();
            final IPearChannel channel = pkg.getChannel();
            final IPearUtility utility = channel.getPearUtility();
            final File phpDir = utility.getPhpDir();
            boolean copied = false;
            for (final String relativeName : version.getPhpFiles()) {
                try {
                    // some packages use backslashes. they do not work on linux
                    final File file = new File(phpDir, relativeName.replace("\\", "/"));
                    final File destination = new File(this.getProject().getBuild().getOutputDirectory(), relativeName.replace("\\", "/"));
                    getLog().debug("copying " + file.getAbsolutePath() + " to " + destination.getAbsolutePath());
                    FileUtils.copyFile(file, destination);
                    copied = true;
                } catch (IOException e) {
                    throw new MojoExecutionException("Problems copying resource " + relativeName, e);
                }
            }
            
            if (!copied) {
                // this would cause the build to fail (empty package). let us create a readme.
                final File file = new File(phpDir, "__README.EMPTY.PACKAGE.TXT");
                try {
                    new FileWriter(file).append("this is an empty pear package or import failed.").close();
                } catch (IOException e) {
                    throw new MojoExecutionException("Problems creating __README.EMPTY.PACKAGE.TXT", e);
                }
            }
        } catch (PhpException e) {
            throw new MojoExecutionException("Problems reading php package.", e);
        }
	}
    
}
