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

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;

/**
 * Interface for mojos that are aware to be configured or php executions. Mainly introduces Getters.
 * 
 * @author Martin Eisengardt
 */
public interface IPhpConfigurationMojo {
    
    /**
     * Returns the maven project we are configured on.
     * 
     * @return The maven project.
     */
    MavenProject getProject();
    
    /**
     * Returns the logging delegate.
     * 
     * @return logging delegate.
     */
    Log getLog();

    /**
     * Returns the path/command for php executable.
     * 
     * @return The php executable
     */
    String getPhpExecutable();
    
    /**
     * Returns additional parameters to be passed to php and command line.
     * 
     * @return Additional php parameters.
     */
    String getAdditionalPhpParameters();
    
    /**
     * Returns the file for dependencies to be extracted to.
     * 
     * @return The dependencies extraction target directory.
     */
    File getDependenciesTargetDirectory();
    
    /**
     * Returns the file for dependencies in testing stage to be extracted to.
     * 
     * @return The test dependencies extraction target directory.
     */
    File getTestDependenciesTargetDirectory();
    
    /**
     * True if any output of php invocations should be printed to console.
     * 
     * @return true to print php output
     */
    boolean isLogPhpOutput();
    
    /**
     * True if any include error should be ignored.
     * 
     * @return true for ignoring include errors.
     */
    boolean isIgnoreIncludeErrors();
    
    /**
     * The file of the temporary script used to execute temporary php actions.
     * 
     * @return temporary script filename.
     */
    File getTemporaryScriptFilename();
    
    /**
     * The target classes directory.
     * 
     * @return target/classes
     */
    File getTargetClassesDirectory();
    
    /**
     * The target test classes directory.
     * 
     * @return target/test-classes
     */
    File getTargetTestClassesDirectory();
    
    /**
     * Returns the Project builder to be used.
     * @return the project builder
     */
    ProjectBuilder getMavenProjectBuilder();
    
    /**
     * Returns the The Maven session to be used.
     * @return the maven session.
     */
    MavenSession getSession();

}
