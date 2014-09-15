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

package org.phpmaven.plugin.php;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.phpmaven.core.ExecutionUtils;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.project.IProjectPhpExecution;

/**
 * Mojo to print the include path of a project
 * 
 * @author mepeisen
 * @goal print-test-include-path
 * @requiresDependencyResolution test
 */
public class PrintTestInclude extends AbstractMojo {
    
    /**
     * The configuration factory.
     * @component
     * @required
     */
    protected IComponentFactory factory;
    
    /**
     * The Maven session.
     *
     * @parameter expression="${session}"
     * @readonly
     * @required
     */
    private MavenSession session;
    
    /**
     * The poutput format
     * @parameter expression="${format}"
     */
    private String format;
    
    /**
     * The file that is used to print the output to
     * @parameter expression="${toFile}"
     */
    private File toFile;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
    	try {
            final IProjectPhpExecution execution = this.factory.lookup(
            		IProjectPhpExecution.class, IComponentFactory.EMPTY_CONFIG, this.session);
            
            if (this.format == null) {
            	this.format = System.getProperty("format");
            }
            if (this.toFile == null) {
            	final String f = System.getProperty("toFile");
            	if (f != null) {
            		this.toFile = new File(f);
            	}
            }
            
            String result = null;
            
            if (this.format == null || "toString".equals(this.format)) {
            	result = String.valueOf(execution.getTestExecutionConfiguration().getIncludePath());
            } else if ("phpstring".equals(this.format)) {
            	final StringBuffer includePath = new StringBuffer();
            	boolean first = true;
            	for (final String path : execution.getTestExecutionConfiguration().getIncludePath()) {
            		if (first) {
            			includePath.append("'");
            		} else {
            			includePath.append(ExecutionUtils.isWindows() ? ";" : ":");
            		}
            		includePath.append(path.replace("\\", "\\\\").replace("'", "\\'"));
            	}
            	includePath.append("'");
            	result = includePath.toString();
            } else if ("plain".equals(this.format)) {
            	final StringBuffer includePath = new StringBuffer();
            	boolean first = true;
            	for (final String path : execution.getTestExecutionConfiguration().getIncludePath()) {
            		if (!first) {
            			includePath.append(ExecutionUtils.isWindows() ? ";" : ":");
            		}
            		includePath.append(path);
            	}
            	result = includePath.toString();
            } else {
            	throw new MojoExecutionException("invalid include path format " + this.format);
            }
            
            if (this.toFile == null) {
            	getLog().info("include path: " + result);
            } else {
            	getLog().info("Writeing include path to " + this.toFile);
            	final FileOutputStream fos = new FileOutputStream(this.toFile);
            	fos.write(result.getBytes());
            	fos.flush();
            	fos.close();
            }
        } catch (IOException ex) {
            throw new MojoExecutionException("failed executing print-include-path", ex);
        } catch (ComponentLookupException ex) {
            throw new MojoExecutionException("failed executing print-include-path", ex);
        } catch (PlexusConfigurationException ex) {
            throw new MojoExecutionException("failed executing print-include-path", ex);
        }
    }
    
}
