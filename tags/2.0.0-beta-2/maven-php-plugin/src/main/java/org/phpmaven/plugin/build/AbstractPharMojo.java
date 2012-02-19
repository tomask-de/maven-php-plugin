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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.phpmaven.plugin.phar.PharConfig;
import org.phpmaven.plugin.phar.PharPackager;
import org.phpmaven.plugin.phar.PharPackager.CompressionMethod;
import org.phpmaven.plugin.php.PhpException;


/**
 * pack a phar file using the contents of the library.
 *
 * @author Martin Eisengardt
 */
public abstract class AbstractPharMojo extends AbstractPhpMojo {
    
    /**
     * The phar configuration.
     * 
     * @parameter
     * @optional
     */
    private PharConfig pharConfig;
    
    /**
     * The php package file name.
     * 
     * @parameter default-value="packagePhar.php"
     * @required
     */
    private String packagePhpFilename;
    
    /**
     * The target directory to be used.
     * 
     * @parameter expression="${project.basedir}/target"
     * @required
     * @readonly
     */
    private File targetDirectory;
    
    /**
     * The final phar filename.
     * 
     * @parameter
     * @optional
     */
    private String pharFilename;
    
    /**
     * The contents of the packed file.
     * 
     * @parameter
     * @optional
     */
    private PharContentEntry[] entries;
    
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
        
        String filename = this.pharFilename;
        if (filename == null) {
            filename = this.getDefaultFilename();
        }
        getLog().info("phar filename: " + filename);
        
        final File targetFile = new File(this.getTargetDirectory(), filename);
        if (targetFile.exists()) {
            getLog().info("phar already exists. skipping. Use clean first to re-create the phar.");
            this.getProject().getArtifact().setFile(new File(this.getTargetDirectory(), filename));
            return;
        }
        
        final PharPackager packager = new PharPackager();
        packager.setFileCompression(CompressionMethod.COMPRESSION_GZIP);
        packager.setFilename(filename);
        packager.setConfig(this.pharConfig);
        packager.setTargetDirectory(this.getTargetDirectory());
        if (this.entries == null || this.entries.length == 0) {
            this.entries = this.getDefaultEntries();
        }
        for (final PharContentEntry entry : entries) {
            if (entry.getFile().exists()) {
                if (entry.getFile().isFile()) {
                    packager.addFile(entry.getFile(), entry.getRelPath());
                } else {
                    final File relPath = new File(entry.getRelPath());
                    if (!relPath.equals(entry.getFile()) && !isParent(entry.getFile(), relPath)) {
                        throw new MojoExecutionException(
                                "Cannot package phar: " +
                                entry.getFile().getAbsolutePath() +
                                " not within relative path " +
                                relPath.getAbsoluteFile());
                    }
                    packager.addDirectory(entry.getFile(), relPath);
                }
            } else {
                getLog().debug("Phar entry " + entry.getFile().getAbsolutePath() + " does not exist. skipping.");
            }
        }
        packager.setBuildDirectory(this.getTargetDirectory());
        packager.setPackagePhpFilename(this.packagePhpFilename);
        try {
            packager.execute(this.getPhpHelper());
        } catch (PhpException ex) {
            throw new MojoExecutionException("Failed packing phar", ex);
        }
        
        this.getProject().getArtifact().setFile(new File(this.getTargetDirectory(), filename));
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
