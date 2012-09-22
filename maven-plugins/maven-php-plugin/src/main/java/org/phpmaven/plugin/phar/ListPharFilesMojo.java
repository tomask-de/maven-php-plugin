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
 * Mojo to list the contents of a phar file.
 * 
 * @author mepeisen
 * @requiresProject false
 * @goal list-phar-files
 */
public class ListPharFilesMojo extends AbstractMojo {
    
    /**
     * @parameter expression="${phar}"
     * @required
     */
    private File phar;
    
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
            
            final Iterable<String> contents = config.getPharPackager().listFiles(this.phar, this.getLog());
            getLog().info("contents of phar file " + this.phar);
            for (final String f : contents) {
                getLog().info(f);
            }
        } catch (ComponentLookupException ex) {
            throw new MojoExecutionException("failed executing list-phar-files", ex);
        } catch (PlexusConfigurationException ex) {
            throw new MojoExecutionException("failed executing list-phar-files", ex);
        } catch (PhpException ex) {
            throw new MojoExecutionException("failed executing list-phar-files", ex);
        } catch (IOException ex) {
            throw new MojoExecutionException("failed executing list-phar-files", ex);
        }
    }
    
}
