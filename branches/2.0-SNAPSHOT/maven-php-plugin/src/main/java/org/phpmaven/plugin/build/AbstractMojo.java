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

package org.phpmaven.plugin.build;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.phpmaven.core.IComponentFactory;

/**
 * Helper class to give fast access to the PHP executable and the basic configuration.
 *
 * @author Christian Wiedemann
 * @author Tobias Sarnowski
 */
public abstract class AbstractMojo extends org.apache.maven.plugin.AbstractMojo {

    /**
     * The Maven project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;
    
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
    
    /**
     * Represents the maven project.
     *
     * @return the current maven project.
     */
    public MavenProject getProject() {
        return project;
    }
    
    /**
     * Returns the The Maven session to be used.
     * @return the maven session.
     */
    public MavenSession getSession() {
        return this.session;
    }
    
}
