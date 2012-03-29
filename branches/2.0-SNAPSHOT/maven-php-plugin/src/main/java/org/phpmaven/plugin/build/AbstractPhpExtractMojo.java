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
import java.io.IOException;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.plugin.php.MultiException;
import org.phpmaven.plugin.php.PhpException;
import org.phpmaven.project.IProjectPhpExecution;


/**
 * Base class for the extract dependencies mojos.
 *
 * @requiresDependencyResolution compile
 * @author Erik Dannenberg
 */
public abstract class AbstractPhpExtractMojo extends AbstractPhpMojo {

    /**
     * Returns the scope from which dependencies should be unpacked from.
     * @return target scope
     */
    protected abstract String getTargetScope();

    @Override
    public void execute() throws MojoExecutionException {
        getLog().info("Unpacking dependencies...");
        IProjectPhpExecution config = null;
        File targetDir = null;
        try {
            config = factory.lookup(
                IProjectPhpExecution.class,
                IComponentFactory.EMPTY_CONFIG,
                this.getSession());

            if (Artifact.SCOPE_TEST.equals(getTargetScope())) {
                targetDir = config.getTestDepsDir();
            } else {
                targetDir = config.getDepsDir();
            }
        } catch (ComponentLookupException ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        } catch (PlexusConfigurationException ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        } catch (ExpressionEvaluationException ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        }
        
        try {
            this.getPhpHelper().prepareDependencies(this.factory, this.getSession(), targetDir, getTargetScope());
        } catch (MultiException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        } catch (PhpException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

}
