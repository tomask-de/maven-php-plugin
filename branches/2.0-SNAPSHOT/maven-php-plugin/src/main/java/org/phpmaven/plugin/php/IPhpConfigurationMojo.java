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
