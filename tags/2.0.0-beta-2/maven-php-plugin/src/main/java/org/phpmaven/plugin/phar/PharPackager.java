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
package org.phpmaven.plugin.phar;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.phpmaven.plugin.php.IPhpExecution;
import org.phpmaven.plugin.php.PhpException;

/**
 * A phar helper to compress the contents of a phar.
 * 
 * @author Martin Eisengardt
 */
public class PharPackager {

    /**
     * Available compression methods.
     */
    public static enum CompressionMethod {
        /** No compression. */
        COMPRESSION_NONE,
        /** Gzip compression. */
        COMPRESSION_GZIP
    }

    /**
     * The default stub file.
     */
    protected static final String DEFAULT_STUB = 
            "<?php " +
            "die('Unable to execute this phar'); " +
            "__HALT_COMPILER(); ?>";

    /**
     * The default package file.
     */
    private static final String DEFAULT_PACKAGE_DIRECTORY_PHP = 
            "\n$phar->buildFromIterator(new RecursiveIteratorIterator(new RecursiveDirectoryIterator(\n" +
            "    realpath('$:{pkgdir}'))),\n" +
            "    realpath('$:{pkgbasedir}'));\n";

    /**
     * The default package file.
     */
    private static final String DEFAULT_PACKAGE_PHP = "<?php\n" + 
            "if (file_exists('$:{pharfilepath}'.DIRECTORY_SEPARATOR.'$:{pharfilename}')) " +
            "unlink('$:{pharfilepath}'.DIRECTORY_SEPARATOR.'$:{pharfilename}');\n" + 
            "$phar = new Phar('$:{pharfilepath}'.DIRECTORY_SEPARATOR.'$:{pharfilename}', 0, '$:{pharfilename}');\n" + 
            "$phar->startBuffering();\n" + 
            "$:{pharcontents}" + 
            "$:{pharcompression}" + 
            "$phar->setStub('$:{pharstub}');\n" + 
            "$phar->stopBuffering();\n"; 

    /**
     * The file compression.
     */
    private CompressionMethod fileCompression = CompressionMethod.COMPRESSION_GZIP;

    /**
     * The target directory.
     */
    private File targetDirectory;

    /**
     * The phar file name.
     */
    private String filename;

    /**
     * The build directory (for temporary build files).
     */
    private File buildDirectory;

    /**
     * The build file name.
     */
    private String packagePhpFilename;

    /**
     * Template for the package php file.
     */
    private String packagePhpTemplate;

    /**
     * Template for the package php file.
     */
    private String packagePhpDirectoryTemplate;

    /**
     * Is the phar compressed?
     */
    private boolean isCompressed = true;

    /**
     * The phar entries.
     */
    private List<PharEntry> entries = new ArrayList<PharEntry>();

    /**
     * The file stub.
     */
    private String stub;

    /**
     * Returns the build directory.
     * @return build directory
     */
    public File getBuildDirectory() {
        return buildDirectory;
    }

    /**
     * Sets the build directory.
     * @param buildDirectory build directory
     */
    public void setBuildDirectory(File buildDirectory) {
        this.buildDirectory = buildDirectory;
    }

    /**
     * Returns the packager php script filename.
     * @return the package php script filename
     */
    public String getPackagePhpFilename() {
        return packagePhpFilename;
    }

    /**
     * Sets the packager php script filename.
     * @param packagePhpFilename packager php script filename.
     */
    public void setPackagePhpFilename(String packagePhpFilename) {
        this.packagePhpFilename = packagePhpFilename;
    }

    /**
     * Returns the packager php template.
     * @return packager php template.
     */
    public String getPackagePhpTemplate() {
        return packagePhpTemplate;
    }

    /**
     * Sets the packager php template.
     * @param packagePhpTemplate packager php template.
     */
    public void setPackagePhpTemplate(String packagePhpTemplate) {
        this.packagePhpTemplate = packagePhpTemplate;
    }

    /**
     * Returns the packager php directory template.
     * @return packager php directory template
     */
    public String getPackagePhpDirectoryTemplate() {
        return packagePhpDirectoryTemplate;
    }

    /**
     * Sets the packager php directory template.
     * @param packagePhpDirectoryTemplate packager php directory template.
     */
    public void setPackagePhpDirectoryTemplate(
            String packagePhpDirectoryTemplate) {
        this.packagePhpDirectoryTemplate = packagePhpDirectoryTemplate;
    }

    /**
     * Returns the php file stub.
     * 
     * @return php file stub
     */
    public String getStub() {
        return stub;
    }

    /**
     * Sets the php file stub.
     * 
     * @param stub
     *            php file stub
     */
    public void setStub(String stub) {
        this.stub = stub;
    }

    /**
     * Adds a directory to be packed.
     * 
     * @param directory
     *            directory to pack
     * @param baseDirectory
     *            base directory for building relative entries
     */
    public void addDirectory(File directory, File baseDirectory) {
        final PharDirectory entry = new PharDirectory();
        entry.setDirectory(directory);
        entry.setBaseDirectory(baseDirectory);
        this.entries.add(entry);
    }

    /**
     * Adds a file to be packed.
     * 
     * @param file file to pack
     * @param localName the local name (relative path)
     */
    public void addFile(File file, String localName) {
        final PharFile entry = new PharFile();
        entry.setFile(file);
        entry.setLocalName(localName);
        this.entries.add(entry);
    }

    /**
     * Returns the entries for packing.
     * @return entries to be packed
     */
    public Iterable<PharEntry> getEntries() {
        return this.entries;
    }

    /**
     * Returns the compression method.
     * @return compression method
     */
    public CompressionMethod getFileCompression() {
        return fileCompression;
    }

    /**
     * Sets the compression mode.
     * @param fileCompression file compression mode
     */
    public void setFileCompression(CompressionMethod fileCompression) {
        this.fileCompression = fileCompression;
    }

    /**
     * Returns the target directory.
     * @return target directory
     */
    public File getTargetDirectory() {
        return targetDirectory;
    }

    /**
     * Sets the target directory.
     * @param targetDirectory target directory
     */
    public void setTargetDirectory(File targetDirectory) {
        this.targetDirectory = targetDirectory;
    }

    /**
     * Returns the filename.
     * @return filename of phar file
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Sets the filename.
     * @param filename filename of phar file
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Executes the packaging.
     * @param exec mojo for execution helping
     * @throws MojoExecutionException thrown if there were problems while packing
     * @throws PhpException thrown if there were problems invoking the packager script
     */
    public void execute(IPhpExecution exec) throws MojoExecutionException, PhpException {
        // create package php file
        final File phpFile = new File(this.buildDirectory, this.packagePhpFilename);
        FileWriter w = null;
        try {
            w = new FileWriter(phpFile);
            final StringBuilder contents = new StringBuilder();
            for (final PharEntry entry : this.getEntries()) {
                if (entry instanceof PharDirectory) {
                    final PharDirectory dir = (PharDirectory) entry;
                    final String maskedDir = dir.getDirectory().getAbsolutePath().replace("\\", "\\\\");
                    final String maskedBasePath = dir.getBaseDirectory().getAbsolutePath().replace("\\", "\\\\");
                    
                    final String templateToBeUsed =
                        this.packagePhpDirectoryTemplate == null ?
                        DEFAULT_PACKAGE_DIRECTORY_PHP :
                        this.packagePhpDirectoryTemplate;
                    
                    contents.append(
                            templateToBeUsed.replace("$:{pkgdir}", maskedDir).
                            replace("$:{pkgbasedir}", maskedBasePath));  
                } else {
                    final PharFile file = (PharFile) entry;
                    final String fileMasked = file.getFile().getAbsolutePath().replace("\\", "\\\\");
                    final String fileLocalNameMasked = file.getLocalName().replace("\\", "\\\\");
                    contents.append(
                            "$phar->addFile(").append(
                            "'").append(
                            fileMasked).append(
                            "', ").append(
                            "'").append(
                            fileLocalNameMasked).append(
                            "');\n");    
                }
            }
            final String templateToUse =
                    this.packagePhpTemplate == null ?
                    DEFAULT_PACKAGE_PHP : this.packagePhpTemplate;
            final String targetMasked = this.getTargetDirectory().getAbsolutePath().replace("\\", "\\\\");
            final String stubToUse = this.getStub() == null ? DEFAULT_STUB : this.getStub();
            w.write(templateToUse.replace(
                    "$:{pharfilepath}", targetMasked).replace(
                    "$:{pharfilename}", this.getFilename()).replace(
                    "$:{pharcontents}", contents.toString()).replace(
                    // XXX: May we need to set a compression template????
                    "$:{pharcompression}", this.isCompressed() ? "$phar->compressFiles(Phar::GZ);\n" : "").replace(
                    "$:{pharstub}", stubToUse.replace("'", "\\'"))   
            );
        } catch (IOException e) {
            throw new MojoExecutionException(
                    "Error creating file " + phpFile, e); 
        } finally {
            if (w != null) {
                try {
                    w.close();
                /*CHECKSTYLE:OFF*/
                } catch (IOException e) {
                /*CHECKSTYLE:ON*/
                    // ignore
                }
            }
        }
        
        // XXX: respect build directory (set working path)
        
        final String commandLine = exec.defaultIncludePath(null) +
            " -d phar.readonly=0 \"" + phpFile.getAbsolutePath() + "\"";
        exec.execute(commandLine, phpFile);
    }

    /**
     * Sets compression flag.
     * @param compressed true to use compression
     */
    public void setCompressed(boolean compressed) {
        this.isCompressed = compressed;
    }

    /**
     * Returns the compression flag.
     * @return true to compress
     */
    public boolean isCompressed() {
        return isCompressed;
    }

    /**
     * Sets the phar config.
     * @param pharConfig phar configuration
     */
    public void setConfig(PharConfig pharConfig) {
        if (pharConfig != null) {
            if (pharConfig.getPackagePhpCompressed() != null)
                this.setCompressed(Boolean.valueOf(pharConfig.getPackagePhpCompressed()));
            if (pharConfig.getPackagePhpContent() != null)
                this.setPackagePhpTemplate(pharConfig.getPackagePhpContent());
            if (pharConfig.getPackagePhpDirectory() != null)
                this.setPackagePhpDirectoryTemplate(pharConfig.getPackagePhpDirectory());
            if (pharConfig.getPharStub() != null)
                this.setStub(pharConfig.getPharStub());
        }
    }

}
