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

package org.phpmaven.plugin.phar;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.exec.PhpException;
import org.phpmaven.phar.IPharPackagerConfiguration;
import org.phpmaven.phar.IPharPackagingRequest;
import org.phpmaven.plugin.build.FileHelper;

/**
 * Mojo to convert a zip/jar to a phar and vice-versa.
 * 
 * @author mepeisen
 * @requiresProject false
 * @goal convert-phar
 */
public class ConvertPharMojo extends AbstractMojo {
    
    /**
     * The source file to be converted (the file extension should be zip/jar/phar)
     * @parameter expression="${from}"
     * @required
     */
    private File from;

    /**
     * A target file that will be created (the file extension should be zip/jar/phar)
     * @parameter expression="${to}"
     * @required
     */
    private File to;
    
    /**
     * A temporary directory to be used. If not specified the directory "./temp" is used.
     * The directory is deleted after the file was converted.
     * @parameter expression="${temp}"
     */
    private File temp;
    
    /**
     * The Maven session.
     *
     * @parameter expression="${session}"
     * @readonly
     * @required
     */
    private MavenSession session;
    
    /**
     * The configuration factory.
     * @component
     * @required
     */
    protected IComponentFactory factory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (!this.from.exists()) {
            throw new MojoExecutionException("source file " + this.from + " does not exist");
        }
        if (this.from.isDirectory()) {
            throw new MojoExecutionException("source file " + this.from + " is a directory");
        }
        if (this.to.isDirectory()) {
            throw new MojoExecutionException("target file " + this.to + " is a directory");
        }
        
        if (!this.to.getParentFile().exists()) {
            this.to.getParentFile().mkdirs();
        }
        if (this.to.exists()) {
            this.to.delete();
        }

        try {
            if (this.temp == null) {
                this.temp = new File("temp").getAbsoluteFile();
            }
            if (this.temp.exists()) {
                FileUtils.deleteDirectory(this.temp);
            }
            this.temp.mkdirs();
            
            final File tmpSnippet = File.createTempFile("snippet", ".php");
            tmpSnippet.deleteOnExit();
            
            final Xpp3Dom configNode = new Xpp3Dom("configuration");
            final Xpp3Dom execConfigNode = new Xpp3Dom("executableConfig");
            final Xpp3Dom fileNode = new Xpp3Dom("temporaryScriptFile");
            fileNode.setValue(tmpSnippet.getAbsolutePath());
            execConfigNode.addChild(fileNode);
            configNode.addChild(execConfigNode);
        
            final IPharPackagerConfiguration config = this.factory.lookup(
                IPharPackagerConfiguration.class, configNode, this.session);
            
            // extract
            if (this.from.getName().endsWith(".zip")) {
                this.getLog().info("Extracting zip: " + this.from);
                FileHelper.unzip(this.getLog(), this.from, this.temp);
            } else if (this.from.getName().endsWith(".jar")) {
                this.getLog().info("Extracting jar: " + this.from);
                FileHelper.unjar(this.getLog(), this.from, this.temp);
            } else if (this.from.getName().endsWith(".phar")) {
                this.getLog().info("Extracting phar: " + this.from);
                config.getPharPackager().extractPharTo(this.from, this.temp, this.getLog());
            } else {
                throw new MojoExecutionException("File format of " + this.from + " not supported");
            }
            
            // packing

            if (this.to.getName().endsWith(".zip")) {
                this.getLog().info("Packing zip: " + this.to);
                ZipOutputStream target = new ZipOutputStream(new FileOutputStream(this.to)); 
                add(this.temp.getAbsolutePath().length(), this.temp, target); 
                target.close();
            } else if (this.to.getName().endsWith(".jar")) {
                this.getLog().info("Packing jar: " + this.to);
                final Manifest manifest = new Manifest();
                manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
                JarOutputStream target = new JarOutputStream(new FileOutputStream(this.to), manifest); 
                add(this.temp.getAbsolutePath().length(), this.temp, target); 
                target.close();
            } else if (this.to.getName().endsWith(".phar")) {
                this.getLog().info("Packing phar: " + this.to);
                final IPharPackagingRequest request = factory.lookup(
                        IPharPackagingRequest.class,
                        IComponentFactory.EMPTY_CONFIG,
                        this.session);
                request.addDirectory("/", this.temp.getAbsoluteFile());
                request.setTargetDirectory(this.to.getParentFile());
                request.setFilename(this.to.getName());
                config.getPharPackager().packagePhar(request, this.getLog());
            } else {
                throw new MojoExecutionException("File format of " + this.to + " not supported");
            }
            
            // deleting temp
            this.getLog().info("Deleting temporary directory: " + this.temp);
        } catch (ComponentLookupException ex) {
            throw new MojoExecutionException("failed executing convert-phar", ex);
        } catch (PlexusConfigurationException ex) {
            throw new MojoExecutionException("failed executing convert-phar", ex);
        } catch (PhpException ex) {
            throw new MojoExecutionException("failed executing convert-phar", ex);
        } catch (IOException ex) {
            throw new MojoExecutionException("failed executing convert-phar", ex);
        }
    }
    
    private void add(int relLength, File source, ZipOutputStream target) throws IOException
    {
        BufferedInputStream in = null;
        try
        {
            if (source.isDirectory())
            {
                String name = source.getPath().substring(relLength).replace("\\", "/");
                if (!name.isEmpty())
                {
                    if (!name.endsWith("/"))
                        name += "/";
                    ZipEntry entry = new ZipEntry(name);
                    entry.setTime(source.lastModified());
                    target.putNextEntry(entry);
                    target.closeEntry();
                }
                for (File nestedFile: source.listFiles())
                    add(relLength, nestedFile, target);
                return;
            }
            
            ZipEntry entry = new ZipEntry(source.getPath().substring(relLength).replace("\\", "/"));
            entry.setTime(source.lastModified());
            target.putNextEntry(entry);
            in = new BufferedInputStream(new FileInputStream(source));
            
            byte[] buffer = new byte[1024];
            while (true)
            {
                int count = in.read(buffer);
                if (count == -1)
                    break;
                target.write(buffer, 0, count);
            }
            target.closeEntry(); 
        } 
        finally 
        { 
            if (in != null) 
                in.close(); 
        } 
    }
    
    private void add(int relLength, File source, JarOutputStream target) throws IOException
    {
        BufferedInputStream in = null;
        try
        {
            if (source.isDirectory())
            {
                String name = source.getPath().substring(relLength).replace("\\", "/");
                if (!name.isEmpty())
                {
                    if (!name.endsWith("/"))
                        name += "/";
                    JarEntry entry = new JarEntry(name);
                    entry.setTime(source.lastModified());
                    target.putNextEntry(entry);
                    target.closeEntry();
                }
                for (File nestedFile: source.listFiles())
                    add(relLength, nestedFile, target);
                return;
            }
            
            JarEntry entry = new JarEntry(source.getPath().substring(relLength).replace("\\", "/"));
            entry.setTime(source.lastModified());
            target.putNextEntry(entry);
            in = new BufferedInputStream(new FileInputStream(source));
            
            byte[] buffer = new byte[1024];
            while (true)
            {
                int count = in.read(buffer);
                if (count == -1)
                    break;
                target.write(buffer, 0, count);
            }
            target.closeEntry(); 
        } 
        finally 
        { 
            if (in != null) 
                in.close(); 
        } 
    }
    
}
