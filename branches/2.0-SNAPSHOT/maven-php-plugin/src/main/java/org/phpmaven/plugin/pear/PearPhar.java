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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.phpmaven.phar.IPharPackager;
import org.phpmaven.phar.IPharPackagerConfiguration;
import org.phpmaven.phar.IPharPackagingRequest;
import org.phpmaven.phar.PharDirectory;
import org.phpmaven.phar.PharEntry;
import org.phpmaven.phar.PharEntry.EntryType;
import org.phpmaven.plugin.build.AbstractPhpMojo;
import org.phpmaven.plugin.build.FileHelper;

/**
 * pack a phar file using the contents of the library.
 *
 * @requiresDependencyResolution compile
 * @goal phar-pear
 * @author Martin Eisengardt
 */
public final class PearPhar extends AbstractPhpMojo {
    
    /**
     * The phar packager configuration.
     * 
     * @see IPharPackagerConfiguration
     * @parameter
     * @optional
     */
    protected Xpp3Dom pharPackagerConfig;
    
    /**
     * @parameter expression="${project.basedir}/target/pear-data"
     */
    private File targetDataDir;
    
    /**
     * @parameter expression="${project.basedir}/target/pear-doc"
     */
    private File targetDocDir;
    
    /**
     * @parameter expression="${project.basedir}/target/pear-www"
     */
    private File targetWwwDir;
    
    /**
     * @parameter expression="${project.basedir}/target/${project.artifactId}-${project.version}-package.xml"
     */
    private File packageXmlFile;
    
    /**
     * @parameter expression="${project.basedir}/target/${project.artifactId}-${project.version}-pear.tgz"
     */
    private File tgzFile;
    
    /**
     * @parameter expression="${project.basedir}/target/${project.artifactId}-${project.version}-data.phar"
     */
    private File pharDataFile;
    
    /**
     * @parameter expression="${project.basedir}/target/${project.artifactId}-${project.version}-www.phar"
     */
    private File pharWwwFile;
    
    /**
     * @parameter expression="${project.basedir}/target/${project.artifactId}-${project.version}-doc.phar"
     */
    private File pharDocFile;
    
    /**
     * The maven project helper
     * @component
     * @required
     */
    private MavenProjectHelper projectHelper;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        createDataPhar();
        createDocPhar();
        createWwwPhar();
        
        this.projectHelper.attachArtifact(this.getProject(), "tgz", "pear-tgz", this.tgzFile);
        this.projectHelper.attachArtifact(this.getProject(), "xml", "pear-pkgxml", this.packageXmlFile);
    }

    private void createDataPhar() throws MojoExecutionException {
        getLog().info(
                "\n-------------------------------------------------------\n" +
                "P A C K A G E    D A T A    P H A R\n" +
                "-------------------------------------------------------");
        
        try {
            // calculate the filename
            getLog().info("phar filename: " + this.pharDataFile.getName());
            
            // skip if the phar already exists
            final File targetFile = this.pharDataFile;
            if (targetFile.exists()) {
                getLog().warn("phar already exists. skipping. Use clean first to re-create the phar.");
                this.projectHelper.attachArtifact(this.getProject(), targetFile, "pear-data");
                return;
            }
            
            // skip empty
            if (!this.targetDataDir.exists()) {
                getLog().warn("no data files found. skipping.");
                return;
            }

            // create the packager
            final IPharPackagerConfiguration packagerConfig = this.factory.lookup(
                    IPharPackagerConfiguration.class,
                    this.pharPackagerConfig,
                    this.getSession());
            
            final IPharPackager packager = packagerConfig.getPharPackager();
            
            // create and populate the request
            final IPharPackagingRequest request = this.factory.lookup(
                    IPharPackagingRequest.class,
                    this.pharPackagerConfig,
                    this.getSession());
            
            request.setFileCompression(org.phpmaven.phar.CompressionMethod.COMPRESSION_GZIP);
            request.setFilename(targetFile.getName());
            request.setTargetDirectory(targetFile.getParentFile());
            request.addDirectory(this.targetDataDir.getAbsolutePath(), this.targetDataDir);
            
            largeFileFix(request);
            
            // package
            packager.packagePhar(request, getLog());
            
            // assign to artifact
            if (targetFile.exists()) {
                this.projectHelper.attachArtifact(this.getProject(), targetFile, "pear-data");
            } else {
                getLog().warn("phar for data files not created or empty file. skipping.");
            }
        } catch (PlexusConfigurationException ex) {
            throw new MojoExecutionException("failed creating the phar packager.", ex);
        } catch (ComponentLookupException ex) {
            throw new MojoExecutionException("failed creating the phar packager.", ex);
        } catch (org.phpmaven.exec.PhpException ex) {
            throw new MojoExecutionException("failed creating the phar.", ex);
        }
    }

    private void largeFileFix(final IPharPackagingRequest request) {
        // large file fix
        int fileCount = 0;
        for (final PharEntry entry : request.getEntries()) {
            if (entry.getType() == EntryType.FILE) {
                fileCount++;
            } else {
                fileCount = FileHelper.countFiles(((PharDirectory) entry).getPathToPack());
            }
        }
        if (fileCount > 999) {
            request.setLargePhar(true);
        }
    }

    private void createDocPhar() throws MojoExecutionException {
        getLog().info(
                "\n-------------------------------------------------------\n" +
                "P A C K A G E    D O C    P H A R\n" +
                "-------------------------------------------------------");
        
        try {
            // calculate the filename
            getLog().info("phar filename: " + this.pharDocFile.getName());
            
            // skip if the phar already exists
            final File targetFile = this.pharDocFile;
            if (targetFile.exists()) {
                getLog().warn("phar already exists. skipping. Use clean first to re-create the phar.");
                this.projectHelper.attachArtifact(this.getProject(), targetFile, "pear-doc");
                return;
            }
            
            // skip empty
            if (!this.targetDocDir.exists()) {
                getLog().warn("no doc files found. skipping.");
                return;
            }
            
            // create the packager
            final IPharPackagerConfiguration packagerConfig = this.factory.lookup(
                    IPharPackagerConfiguration.class,
                    this.pharPackagerConfig,
                    this.getSession());
            
            final IPharPackager packager = packagerConfig.getPharPackager();
            
            // create and populate the request
            final IPharPackagingRequest request = this.factory.lookup(
                    IPharPackagingRequest.class,
                    this.pharPackagerConfig,
                    this.getSession());
            
            request.setFileCompression(org.phpmaven.phar.CompressionMethod.COMPRESSION_GZIP);
            request.setFilename(targetFile.getName());
            request.setTargetDirectory(targetFile.getParentFile());
            request.addDirectory(this.targetDocDir.getAbsolutePath(), this.targetDocDir);
            
            largeFileFix(request);
            
            // package
            packager.packagePhar(request, getLog());
            
            // assign to artifact
            if (targetFile.exists()) {
                this.projectHelper.attachArtifact(this.getProject(), targetFile, "pear-doc");
            } else {
                getLog().warn("phar for doc files not created or empty file. skipping.");
            }
        } catch (PlexusConfigurationException ex) {
            throw new MojoExecutionException("failed creating the phar packager.", ex);
        } catch (ComponentLookupException ex) {
            throw new MojoExecutionException("failed creating the phar packager.", ex);
        } catch (org.phpmaven.exec.PhpException ex) {
            throw new MojoExecutionException("failed creating the phar.", ex);
        }
    }

    private void createWwwPhar() throws MojoExecutionException {
        getLog().info(
                "\n-------------------------------------------------------\n" +
                "P A C K A G E    W W W    P H A R\n" +
                "-------------------------------------------------------");
        
        try {
            // calculate the filename
            getLog().info("phar filename: " + this.pharWwwFile.getName());
            
            // skip if the phar already exists
            final File targetFile = this.pharWwwFile;
            if (targetFile.exists()) {
                getLog().warn("phar already exists. skipping. Use clean first to re-create the phar.");
                this.projectHelper.attachArtifact(this.getProject(), targetFile, "pear-www");
                return;
            }
            
            // skip empty
            if (!this.targetWwwDir.exists()) {
                getLog().warn("no www files found. skipping.");
                return;
            }

            // create the packager
            final IPharPackagerConfiguration packagerConfig = this.factory.lookup(
                    IPharPackagerConfiguration.class,
                    this.pharPackagerConfig,
                    this.getSession());
            
            final IPharPackager packager = packagerConfig.getPharPackager();
            
            // create and populate the request
            final IPharPackagingRequest request = this.factory.lookup(
                    IPharPackagingRequest.class,
                    this.pharPackagerConfig,
                    this.getSession());
            
            request.setFileCompression(org.phpmaven.phar.CompressionMethod.COMPRESSION_GZIP);
            request.setFilename(targetFile.getName());
            request.setTargetDirectory(targetFile.getParentFile());
            request.addDirectory(this.targetWwwDir.getAbsolutePath(), this.targetWwwDir);
            
            largeFileFix(request);
            
            // package
            packager.packagePhar(request, getLog());
            
            // assign to artifact
            if (targetFile.exists()) {
                this.projectHelper.attachArtifact(this.getProject(), targetFile, "pear-www");
            } else {
                getLog().warn("phar for www files not created or empty file. skipping.");
            }
        } catch (PlexusConfigurationException ex) {
            throw new MojoExecutionException("failed creating the phar packager.", ex);
        } catch (ComponentLookupException ex) {
            throw new MojoExecutionException("failed creating the phar packager.", ex);
        } catch (org.phpmaven.exec.PhpException ex) {
            throw new MojoExecutionException("failed creating the phar.", ex);
        }
    }

    
}
