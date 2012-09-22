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

package org.phpmaven.plugin.build;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.phpmaven.phar.IPharPackager;
import org.phpmaven.phar.IPharPackagerConfiguration;
import org.phpmaven.phar.IPharPackagingRequest;
import org.phpmaven.phar.PharDirectory;
import org.phpmaven.phar.PharEntry;
import org.phpmaven.phar.PharEntry.EntryType;
import org.phpmaven.phpexec.library.PhpException;


/**
 * pack a phar file using the contents of the library.
 *
 * @author Martin Eisengardt
 */
public abstract class AbstractPharMojo extends AbstractMojo {
    
    /**
     * The target directory to be used.
     * 
     * <p>
     * Defaults to "${project.build.directory}".
     * </p>
     * 
     * @parameter expression="${project.build.directory}"
     * @required
     * @readonly
     */
    private File targetDirectory;
    
    /**
     * The directory containing generated test classes of the project being tested. This will be included at the
     * beginning of the test classpath.
     * 
     * @parameter default-value="${project.build.testOutputDirectory}"
     * @readonly
     */
    private File targetTestClassesDirectory;
    
    /**
     * The directory containing generated classes of the project being tested. This will be included after the test
     * classes in the test classpath.
     * 
     * @parameter default-value="${project.build.outputDirectory}"
     * @readonly
     */
    private File targetClassesDirectory;
    
    /**
     * The contents of the packed file.
     * 
     * @parameter
     * @optional
     */
    private PharContentEntry[] entries;
    
    /**
     * The final phar filename.
     * 
     * @parameter
     * @optional
     */
    private String pharFilename;
    
    /**
     * The phar packager configuration.
     * 
     * @see IPharPackagerConfiguration
     * @parameter
     * @optional
     */
    protected Xpp3Dom pharPackagerConfig;
    
    /**
     * @parameter expression="${largePharFix}" default-value="true"
     * @optional
     */
    protected Boolean largeFileFix;
    
    /**
     * The phar content entry used by configuration.
     */
    public static final class PharContentEntry {
        
        /**
         * The file (directory or file).
         */
        private File file;
        
        /**
         * The relative path.
         */
        private String relPath;

        /**
         * Returns the file.
         * 
         * @return the file
         */
        public File getFile() {
            return file;
        }

        /**
         * Sets the file.
         * 
         * @param file file
         */
        public void setFile(File file) {
            this.file = file;
        }

        /**
         * Returns the relative path.
         * 
         * @return relative path
         */
        public String getRelPath() {
            return relPath;
        }

        /**
         * Sets the relative path.
         * 
         * @param relPath relative path
         */
        public void setRelPath(String relPath) {
            this.relPath = relPath;
        }
        
    }
    
    /**
     * Where the sources should get copied to.
     *
     * @return where the jar inclusion directory is
     */
    public File getTargetClassesDirectory() {
        return this.targetClassesDirectory;
    }

    /**
     * The target directory where to copy the test sources to.
     *
     * @return where the test-jar inclusion directory is
     */
    public File getTargetTestClassesDirectory() {
        return this.targetTestClassesDirectory;
    }
    
    /**
     * Returns the default phar entries to be used for packaging if there was no alternative configuration.
     * 
     * @return default phar entries
     */
    protected abstract PharContentEntry[] getDefaultEntries();
    
    /**
     * Returns the default filename for the phar file.
     * 
     * @return default filename
     */
    protected abstract String getDefaultFilename();

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info(
                "\n-------------------------------------------------------\n" +
                "P A C K A G E    P H A R\n" +
                "-------------------------------------------------------");
        
        try {
            // calculate the filename
            String filename = this.pharFilename;
            if (filename == null) {
                filename = this.getDefaultFilename();
            }
            getLog().info("phar filename: " + filename);
            
            // skip if the phar already exists
            final File targetFile = new File(this.getTargetDirectory(), filename);
            if (targetFile.exists()) {
                getLog().warn("phar already exists. skipping. Use clean first to re-create the phar.");
                this.getProject().getArtifact().setFile(new File(this.getTargetDirectory(), filename));
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
            request.setFilename(filename);
            request.setTargetDirectory(this.getTargetDirectory());
            if (this.entries == null || this.entries.length == 0) {
                this.entries = this.getDefaultEntries();
            }
            for (final PharContentEntry entry : entries) {
                if (entry.getFile().exists()) {
                    if (entry.getFile().isFile()) {
                        request.addFile(entry.getRelPath(), entry.getFile());
                    } else {
                        final File relPath = new File(entry.getRelPath());
                        if (!relPath.equals(entry.getFile()) && !isParent(entry.getFile(), relPath)) {
                            throw new MojoExecutionException(
                                    "Cannot package phar: " +
                                    entry.getFile().getAbsolutePath() +
                                    " not within relative path " +
                                    relPath.getAbsoluteFile());
                        }
                        request.addDirectory(
                                entry.getFile().getCanonicalPath().substring(relPath.getCanonicalPath().length()),
                                entry.getFile());
                    }
                } else {
                    getLog().debug("Phar entry " + entry.getFile().getAbsolutePath() + " does not exist. skipping.");
                }
            }
            request.setTargetDirectory(this.getTargetDirectory());
            
            if (this.largeFileFix) {
                largeFileFix(request);
            }
            
            // package
            packager.packagePhar(request, getLog());
            
            // assign to artifact
            this.getProject().getArtifact().setFile(new File(this.getTargetDirectory(), filename));
            
        } catch (PlexusConfigurationException ex) {
            throw new MojoExecutionException("failed creating the phar packager.", ex);
        } catch (ComponentLookupException ex) {
            throw new MojoExecutionException("failed creating the phar packager.", ex);
        } catch (PhpException ex) {
            throw new MojoExecutionException("failed creating the phar.", ex);
        } catch (IOException ex) {
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
            getLog().debug("Phar file count exceeds LIMIT. Applying large file fix.");
            request.setLargePhar(true);
        }
    }

    /**
     * Returns true if child is a child of parent; platform independent.
     * 
     * @param possibleChild
     * @param parent
     * @return true if parent is a (deep) parent of possibleChild
     */
    private boolean isParent(File possibleChild, File parent) {
        final File parent2 = possibleChild.getParentFile();
        if (parent2 != null) {
            if (possibleChild.equals(parent)) {
                return true;
            }
            return this.isParent(parent2, parent);
        }
        return false;
    }

    public File getTargetDirectory() {
        return targetDirectory;
    }

    
}
