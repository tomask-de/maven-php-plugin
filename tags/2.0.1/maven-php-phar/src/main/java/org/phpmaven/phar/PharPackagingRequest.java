/**
 * Copyright 2010-2012 by PHP-maven.org
 * 
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

package org.phpmaven.phar;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Configuration;
import org.phpmaven.core.BuildPluginConfiguration;
import org.phpmaven.core.ConfigurationParameter;

/**
 * Implementation of a phar packaging request.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
@Component(role = IPharPackagingRequest.class, instantiationStrategy = "per-lookup")
@BuildPluginConfiguration(artifactId = "maven-php-phar", groupId = "org.phpmaven", path = "pharConfig")
public class PharPackagingRequest implements IPharPackagingRequest {

    /**
     * php template.
     */
    @Configuration(name = "packagePhpTemplate", value =
            "class RecursiveDirectoryIterator2 extends RecursiveDirectoryIterator {\n" +
            "  function getChildren() {\n" +
            "    try {\n" +
            "      return parent::getChildren();\n" +
            "    } catch(UnexpectedValueException $e) {\n" +
            "      return new RecursiveArrayIterator(array());\n" +
            "    }\n" +
            "  }\n" +
            "}\n" + 
            "if (file_exists('$:{pharfilepath}'.DIRECTORY_SEPARATOR.'$:{pharfilename}')) " +
            "unlink('$:{pharfilepath}'.DIRECTORY_SEPARATOR.'$:{pharfilename}');\n" + 
            "$phar = new Phar('$:{pharfilepath}'.DIRECTORY_SEPARATOR.'$:{pharfilename}', 0, '$:{pharfilename}');\n" + 
            "$phar->startBuffering();\n" + 
            "$:{pharcontents}\n" + 
            "$:{pharcompression}" + 
            "$phar->setStub('$:{pharstub}');\n" + 
            "$:{pharmetadata}" +
            "$phar->stopBuffering();\n")
    private String packagePhpTemplate;
    
    /**
     * Directory template.
     */
    @Configuration(name = "packagePhpDirectoryTemplate", value =
            "\n$base = realpath('$:{pkgbasedir}');\n" +
            "$ite = new RecursiveDirectoryIterator2($base, FilesystemIterator::SKIP_DOTS);\n" +
            "$len = strlen($base);\n" +
            "foreach (new RecursiveIteratorIterator($ite) as $filename=>$cur) {\n" +
            "  if (substr(realpath($filename), 0, $len) == $base) {\n" +
            "    $phar->addFile($filename, str_replace('\\\\', '/', '$:{pkgdir}'.substr($filename, $len + 1)));" +
            "  }\n" +
            "}\n")
    private String packagePhpDirectoryTemplate;
    
    /**
     * File template.
     */
    @Configuration(name = "packagePhpFileTemplate", value = 
            "$phar->addFile('$:{filebasepath}', '$:{filename}');\n")
    private String packagePhpFileTemplate;
    
    /**
     * The stub.
     */
    @Configuration(name = "stub", value = "die('Unable to execute this phar');")
    private String stub;
    
    /**
     * The phar entries.
     */
    private List<PharEntry> entries = new ArrayList<PharEntry>();
    
    /**
     * The compression method.
     */
    private CompressionMethod compressionMethod = CompressionMethod.COMPRESSION_GZIP;
    
    /**
     * The target directory.
     */
    @ConfigurationParameter(name = "targetDirectory", expression =
            "${project.basedir}/target")
    private File targetDirectory;
    
    /**
     * The filename.
     */
    private String fileName;
    
    /**
     * Flag for compression.
     */
    @Configuration(name = "compressed", value = "true")
    private boolean isCompressed;
    
    /**
     * Flag for forcing large files.
     */
    @Configuration(name = "largeFile", value = "true")
    private boolean largeFile;

    /**
     *  The metadata entries.
     */
    private Map<String, String> metadata = new HashMap<String, String>();

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPackagePhpTemplate() {
        return this.packagePhpTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPackagePhpTemplate(String packagePhpTemplate) {
        this.packagePhpTemplate = packagePhpTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPackagePhpDirectoryTemplate() {
        return this.packagePhpDirectoryTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPackagePhpDirectoryTemplate(
            String packagePhpDirectoryTemplate) {
        this.packagePhpDirectoryTemplate = packagePhpDirectoryTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPackagePhpFileTemplate() {
        return this.packagePhpFileTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPackagePhpFileTemplate(String packagePhpFileTemplate) {
        this.packagePhpFileTemplate = packagePhpFileTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStub() {
        return this.stub;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStub(String stub) {
        this.stub = stub;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addDirectory(String relativePath, File pathToPack) {
        final PharDirectory dir = new PharDirectory();
        dir.setRelativePath(relativePath);
        dir.setPathToPack(pathToPack);
        this.entries.add(dir);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addFile(String localName, File file) {
        final PharFile pharFile = new PharFile();
        pharFile.setFile(file);
        pharFile.setLocalName(localName);
        this.entries.add(pharFile);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<PharEntry> getEntries() {
        return this.entries;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompressionMethod getFileCompression() {
        return this.compressionMethod;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFileCompression(CompressionMethod fileCompression) {
        this.compressionMethod = fileCompression;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getTargetDirectory() {
        return this.targetDirectory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTargetDirectory(File targetDirectory) {
        this.targetDirectory = targetDirectory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFilename() {
        return this.fileName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFilename(String filename) {
        this.fileName = filename;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCompressed(boolean compressed) {
        this.isCompressed = compressed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCompressed() {
        return this.isCompressed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLargePhar() {
        return this.largeFile;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLargePhar(boolean flg) {
        this.largeFile = flg;
    }

    /**
     * {@inheritDoc}
     */
	@Override
	public Map<String, String> getMetadata() {
		return metadata;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public void setMetadata(Map<String,String> metadata) {
		this.metadata = metadata;
	}

}
