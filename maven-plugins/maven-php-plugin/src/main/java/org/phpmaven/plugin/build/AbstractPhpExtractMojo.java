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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.ProjectBuilder;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.phpexec.library.PhpException;
import org.phpmaven.plugin.php.IPhpConfigurationMojo;
import org.phpmaven.project.IPhpProject;
import org.phpmaven.project.IProjectPhpExecution;


/**
 * Base class for the extract dependencies mojos.
 *
 * @requiresDependencyResolution compile
 * @author Erik Dannenberg
 */
public abstract class AbstractPhpExtractMojo extends AbstractMojo implements IPhpConfigurationMojo {
    
    /**
     * The maven project builder.
     * @component
     * @required
     */
    private ProjectBuilder mavenProjectBuilder;

    /**
     * Returns the Project builder to be used.
     * @return the project builder
     */
    @Override
    public ProjectBuilder getMavenProjectBuilder() {
        return this.mavenProjectBuilder;
    }

    /**
     * Returns the scope from which dependencies should be unpacked from.
     * @return target scope
     */
    protected abstract String getTargetScope();

    @Override
    public void execute() throws MojoExecutionException {
        getLog().info("Unpacking dependencies...");
        IProjectPhpExecution config = null;
        IPhpProject project = null;
        File targetDir = null;
        // TODO verify integrity of dependencies config
        try {
        	project = factory.lookup(
                IPhpProject.class,
                IComponentFactory.EMPTY_CONFIG,
                this.getSession());
        	project.validateDependencies();
            
            config = factory.lookup(
                IProjectPhpExecution.class,
                IComponentFactory.EMPTY_CONFIG,
                this.getSession());

            if (Artifact.SCOPE_TEST.equals(getTargetScope())) {
                targetDir = config.getTestDepsDir();
            } else {
                targetDir = config.getDepsDir();
            }
            
            project.prepareDependencies(getLog(), targetDir, getTargetScope());
        } catch (ComponentLookupException ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        } catch (PlexusConfigurationException ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        } catch (ExpressionEvaluationException ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        } catch (PhpException ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        }
        
//        try {
//            // TODO move this to a plugin (f.e. maven-php-project)
//            // TODO verify integrity of dependencies config
////            this.getPhpHelper().prepareDependencies(
////                this.factory,
////                this.getSession(),
////                targetDir,
////                getTargetScope(),
////                depConfig);
//        } catch (MultiException e) {
//            throw new MojoExecutionException(e.getMessage(), e);
//        } catch (PhpException e) {
//            throw new MojoExecutionException(e.getMessage(), e);
//        } catch (IOException e) {
//            throw new MojoExecutionException(e.getMessage(), e);
//        }
    }

}
