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
import org.phpmaven.plugin.build.AbstractMojo;

/**
 * Goal to copy the resources and classes to the output folder.
 *
 * @goal process-resources-pear
 * @phase process-resources
 * 
 * @author mepeisen
 */
public class ProcessResourcesPearMojo extends AbstractMojo
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
     * @parameter expression="${project.build.directory}/pear-data"
     */
    private File targetDataDir;
    
    /**
     * @parameter expression="${project.build.directory}/pear-doc"
     */
    private File targetDocDir;
    
    /**
     * @parameter expression="${project.build.directory}/pear-www"
     */
    private File targetWwwDir;
    
    /**
     * @parameter expression="${project.build.directory}/${project.artifactId}-${project.version}-package.xml"
     */
    private File packageXmlFile;
    
    /**
     * @parameter expression="${project.build.directory}/${project.artifactId}-${project.version}-pear.tgz"
     */
    private File tgzFile;
    
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
            if (utility.isPearCorePackage(this.pearChannelAlias, this.pearPackage)) {
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
        this.fetchPackage(version, new File(this.getProject().getBuild().getOutputDirectory()), IPackageVersion.FILE_ROLE_PHP);
        this.fetchPackage(version, this.targetDataDir, IPackageVersion.FILE_ROLE_DATA);
        this.fetchPackage(version, this.targetDocDir, IPackageVersion.FILE_ROLE_DOC);
        this.fetchPackage(version, this.targetWwwDir, IPackageVersion.FILE_ROLE_WWW);
        
        this.fetchTgz(version, this.tgzFile);
        
        this.fetchPackageXml(version, this.packageXmlFile);
    }

    /**
     * Fetch tgz
     * @param version
     * @param file
     * @throws MojoExecutionException 
     */
    private void fetchTgz(IPackageVersion version, File file) throws MojoExecutionException {
        this.getLog().info("copying original pear tgz to " + file.getAbsolutePath());
        
        try {
            version.writeTgz(file);
        } catch (PhpException e) {
            throw new MojoExecutionException("Problems reading tar gz.", e);
        }
    }

    /**
     * Fetch package xml
     * @param version
     * @param file
     * @throws MojoExecutionException 
     */
    private void fetchPackageXml(IPackageVersion version, File file) throws MojoExecutionException {
        this.getLog().info("copying original package xml to " + file.getAbsolutePath());
        
        try {
            version.writePackageXml(file);
        } catch (PhpException e) {
            throw new MojoExecutionException("Problems reading package xml.", e);
        }
    }

    /**
     * @param version
     * @param outputDir 
     * @param role 
     * @throws MojoExecutionException 
     */
    private void fetchPackage(IPackageVersion version, File outputDir, String role) throws MojoExecutionException {
        this.getLog().info("copying content for role " + role);
        
        try {
            final IPackage pkg = version.getPackage();
            final IPearChannel channel = pkg.getChannel();
            final IPearUtility utility = channel.getPearUtility();
            File srcDir = null;
            if (IPackageVersion.FILE_ROLE_PHP.equals(role)) {
                srcDir = utility.getPhpDir();
            } else if (IPackageVersion.FILE_ROLE_DATA.equals(role)) {
                srcDir = utility.getDataDir();
            } else if (IPackageVersion.FILE_ROLE_DOC.equals(role)) {
                srcDir = utility.getDocDir();
            } else if (IPackageVersion.FILE_ROLE_WWW.equals(role)) {
                srcDir = utility.getWwwDir();
            } else {
                throw new MojoExecutionException("Unknown role " + role);
            }
            boolean copied = false;
            for (final String relativeName : version.getFiles(role)) {
                try {
                    // some packages use backslashes. they do not work on linux
                    final File file = new File(srcDir, relativeName.replace("\\", "/"));
                    final File destination = new File(outputDir, relativeName.replace("\\", "/"));
                    getLog().debug("copying " + file.getAbsolutePath() + " to " + destination.getAbsolutePath());
                    FileUtils.copyFile(file, destination);
                    copied = true;
                } catch (IOException e) {
                    throw new MojoExecutionException("Problems copying resource " + relativeName, e);
                }
            }
            
            if (!copied && role.equals(IPackageVersion.FILE_ROLE_PHP)) {
                // this would cause the build to fail (empty package). let us create a readme.
                final File file = new File(this.getProject().getBuild().getOutputDirectory(), "__README.EMPTY.PACKAGE.TXT");
                file.getParentFile().mkdirs();
                try {
                    new FileWriter(file).append("this is an empty pear package or import failed.").close();
                } catch (IOException e) {
                    throw new MojoExecutionException("Problems creating __README.EMPTY.PACKAGE.TXT", e);
                }
            }
        } catch (PhpException e) {
            throw new MojoExecutionException("Problems reading package.", e);
        }
    }
    
}
