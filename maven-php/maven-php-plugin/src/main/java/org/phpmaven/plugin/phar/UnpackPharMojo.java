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

import java.io.File;
import java.io.IOException;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.phar.IPharPackagerConfiguration;
import org.phpmaven.phpexec.library.PhpException;

/**
 * Mojo to unpack a phar file.
 * 
 * @author mepeisen
 * @requiresProject false
 * @goal extract-phar
 */
public class UnpackPharMojo extends AbstractMojo {
    
    /**
     * The phar file to be unpacked
     * @parameter expression="${phar}"
     * @required
     */
    private File phar;

    /**
     * The target directory
     * @parameter expression="${target}"
     * @required
     */
    private File target;
    
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
        if (!this.phar.exists()) {
            throw new MojoExecutionException("phar file " + this.phar + " does not exist");
        }
        if (this.phar.isDirectory()) {
            throw new MojoExecutionException("phar file " + this.phar + " is a directory");
        }
        
        if (!this.target.exists()) {
            this.target.mkdirs();
        }
        
        try {
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
            
            config.getPharPackager().extractPharTo(this.phar, this.target, this.getLog());
        } catch (ComponentLookupException ex) {
            throw new MojoExecutionException("failed executing extract-phar", ex);
        } catch (PlexusConfigurationException ex) {
            throw new MojoExecutionException("failed executing extract-phar", ex);
        } catch (PhpException ex) {
            throw new MojoExecutionException("failed executing extract-phar", ex);
        } catch (IOException ex) {
            throw new MojoExecutionException("failed executing extract-phar", ex);
        }
    }
    
}
