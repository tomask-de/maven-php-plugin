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

import java.io.File;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.plugin.php.IPhpWalkConfigurationMojo;

/**
 * Helper class to give fast access to the PHP executable and the basic configuration.
 *
 * @author Christian Wiedemann
 * @author Tobias Sarnowski
 */
public abstract class AbstractPhpWalkMojo extends AbstractMojo implements IPhpWalkConfigurationMojo {

    // properties for IPhpConfigurationMojo
    
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
    
    // end of properties for IPhpConfigurationMojo
    
    // properties for IPhpWalkConfigurationMojo

    /**
     * Files and directories to exclude.
     *
     * @parameter
     */
    private String[] excludes = new String[0];

    /**
     * Files and directories to include.
     *
     * @parameter
     */
    private String[] includes = new String[0];

    /**
     * How php files will be identified after the last point.
     *
     * @parameter default-value="php" expression="${phpFileEnding}"
     */
    private String phpFileEnding;
    
    // end of properties for IPhpWalkConfigurationMojo

    /**
     * The project's base directory.
     *
     * @parameter expression="${project.basedir}"
     * @required
     * @readonly
     */
    private File baseDir;

    /**
     * If the source files should be included in the resulting jar.
     *
     * @parameter default-value="true" expression="${includeInJar}"
     */
    private boolean includeInJar = true;
    
    /**
     * The configuration factory.
     * @component
     * @required
     */
    protected IComponentFactory factory;

    // methods for IPhpConfigurationMojo
    
    /**
     * Represents the maven project.
     *
     * @return the current maven project.
     */
    @Override
    public MavenProject getProject() {
        return project;
    }
    
    /**
     * Returns the The Maven session to be used.
     * @return the maven session.
     */
    @Override
    public MavenSession getSession() {
        return this.session;
    }
    
    // end of methods for IPhpConfigurationMojo
    
    // methods for IPhpWalkConfigurationMojo
    
    /**
     * Returns files and directories to exclude.
     * @return files and directories to exclude.
     */
    @Override
    public String[] getExcludes() {
        return this.excludes;
    }

    /**
     * Returns files and directories to include.
     * @return files and directories to include.
     */
    @Override
    public String[] getIncludes() {
        return this.includes;
    }

    /**
     * Returns how php files will be identified after the last point.
     * @return how php files will be identified after the last point.
     */
    @Override
    public String getPhpFileEnding() {
        return this.phpFileEnding;
    }
    
    // end of methods for IPhpWalkConfigurationMojo

    /**
     * The project's base directory.
     *
     * @return the project's basedir
     */
    public File getBaseDir() {
        return baseDir;
    }

    /**
     * Where the PHP source files can be found.
     *
     * @return where the php sources can be found
     */
    public File getSourceDirectory() {
        return new File(this.getProject().getCompileSourceRoots().get(0).toString());
    }

    /**
     * Where the PHP test sources can be found.
     *
     * @return where the php test sources can be found
     */
    public File getTestSourceDirectory() {
        return new File(this.getProject().getTestCompileSourceRoots().get(0).toString());
    }

    /**
     * If the sources should be included in the resulting jar file. If not,
     * the sources won't be copied to the target directory.
     *
     * @return if the php sources should be included in the resulting jar
     */
    public boolean isIncludeInJar() {
        // XXX: Review. Do we really need this?
        // Contra: There is already an exclusion configured.
        // Contra2: Have a look if this should be better places in the resources mojo and
        // not in the abstract base class
        return includeInJar;
    }
    
}
