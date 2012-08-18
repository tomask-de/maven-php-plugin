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

package org.phpmaven.plugin.php;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.exec.IPhpExecutable;
import org.phpmaven.exec.IPhpExecutableConfiguration;
import org.phpmaven.exec.PhpException;
import org.phpmaven.plugin.build.AbstractMojo;
import org.phpmaven.project.IProjectPhpExecution;

/**
 * executes a php command.
 *
 * @goal exec
 * @author Martin Eisengardt
 */
public final class PhpExec extends AbstractMojo {
    
    /**
     * The php file to be executed.
     * 
     * @parameter expression="${phpFile}"
     * @required
     */
    private File phpFile;
    
    /**
     * True to include the test dependencies into include path.
     * 
     * @parameter default-value="false" expression="${testIncludePath}"
     */
    private boolean testIncludePath;
    
    /**
     * True to include the standard compile dependencies into include path.
     * 
     * @parameter default-value="true" expression="${compileIncludePath}"
     */
    private boolean compileIncludePath;
    
    /**
     * Command line arguments for the php file to be executed.
     * 
     * @parameter expression="${additionalPhpFileArguments}"
     */
    private String phpFileArguments;
    
    /**
     * Print php-stdout to given file. If this is not specified the stdout will be logged as info.
     * 
     * @parameter expression="${phpOutToFile}"
     */
    private File phpOutToFile;
    
    /**
     * The executable configuration to be used. See the javadoc or configuration page for details.
     * @parameter
     */
    private Xpp3Dom executableConfiguration;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            IPhpExecutable exec;
            final Xpp3Dom[] xppconfig = this.executableConfiguration == null ?
                    IComponentFactory.EMPTY_CONFIG :
                        new Xpp3Dom[]{ this.executableConfiguration };
            
            if (this.testIncludePath) {
                final IProjectPhpExecution projExec = this.factory.lookup(
                        IProjectPhpExecution.class,
                        xppconfig,
                        this.getSession());
                exec = projExec.getTestExecutionConfiguration().getPhpExecutable(this.getLog());
            } else if (this.compileIncludePath) {
                final IProjectPhpExecution projExec = this.factory.lookup(
                        IProjectPhpExecution.class,
                        xppconfig,
                        this.getSession());
                exec = projExec.getExecutionConfiguration().getPhpExecutable(this.getLog());
            } else {
                final IPhpExecutableConfiguration config = this.factory.lookup(
                        IPhpExecutableConfiguration.class,
                        xppconfig,
                        this.getSession());
                exec = config.getPhpExecutable(this.getLog());
            }
            
            String commandLine = "";
            commandLine += "\"" + this.phpFile.getAbsolutePath() + "\"";
            if (this.phpFileArguments != null && this.phpFileArguments.length() > 0) {
                commandLine += " " + this.phpFileArguments;
            }
            getLog().info("Executing php: " + commandLine);
            
            final String result = exec.execute(commandLine, this.phpFile);
            
            if (this.phpOutToFile != null) {
                if (!phpOutToFile.getParentFile().exists()) {
                    phpOutToFile.getParentFile().mkdirs();
                }
                final FileWriter writer = new FileWriter(this.phpOutToFile);
                writer.write(result);
            } else {
                getLog().info("Result:\n" + result);
            }
        } catch (ComponentLookupException ex) {
            throw new MojoFailureException("Failed executing php command", ex);
        } catch (PlexusConfigurationException ex) {
            throw new MojoFailureException("Failed executing php command", ex);
        } catch (PhpException ex) {
            throw new MojoFailureException("Failed executing php command", ex);
        } catch (IOException ex) {
            throw new MojoFailureException("Failed executing php command", ex);
        }
    }
    

    
}
