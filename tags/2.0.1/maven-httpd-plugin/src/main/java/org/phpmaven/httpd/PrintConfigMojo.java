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
package org.phpmaven.httpd;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.phpmaven.httpd.control.IApacheConfig;
import org.phpmaven.httpd.control.IApacheService;

/**
 * Prints the apache httpd configuration to the standard output.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.1
 * 
 * @goal print-config
 * @execute phase="compile"
 * @requiresDependencyResolution test
 */
public class PrintConfigMojo extends AbstractApacheMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            final IApacheService service = this.getService();
            final IApacheConfig config = this.getParsedConfig(service);
            
            this.getLog().info("Apache httpd.conf:\n\n" + config.toString());
        } catch (CommandLineException ex) {
            throw new MojoExecutionException("Errors invoking apache on command line", ex);
        } catch (ComponentLookupException ex) {
            throw new MojoExecutionException("Errors invoking apache on command line", ex);
        } catch (PlexusConfigurationException ex) {
            throw new MojoExecutionException("Errors invoking apache on command line", ex);
        }
    }

}
