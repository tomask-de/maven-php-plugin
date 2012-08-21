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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.ProjectBuildingRequest;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.dependency.IAction;
import org.phpmaven.dependency.IActionExtract;
import org.phpmaven.dependency.IActionExtractAndInclude;
import org.phpmaven.dependency.IDependency;
import org.phpmaven.dependency.IDependencyConfiguration;
import org.phpmaven.plugin.build.FileHelper;

/**
 * Helper class to execute PHP scripts and PHP commands. Will be used by various mojos.
 * 
 * @author Christian Wiedemann
 * @author Tobias Sarnowski
 * @author Martin Eisengardt
 * @author Erik Dannenberg
 */
public class PhpMojoHelper {
    
    /**
     * The log to be used for logging php output.
     */
    private Log log;

    /**
     * The Maven project.
     */
    private MavenProject project;
    
    /**
     * The maven project builder.
     */
    private ProjectBuilder mavenProjectBuilder;
    
    /**
     * The Maven session.
     */
    private MavenSession session;
    
    /**
     * Constructor to create the helper.
     * 
     * @param config The configuration aware mojo.
     */
    public PhpMojoHelper(IPhpConfigurationMojo config) {
        this.log = config.getLog();
        this.project = config.getProject();
        this.mavenProjectBuilder = config.getMavenProjectBuilder();
        this.session = config.getSession();
    }
    
    /**
     * Returns the maven project from given artifact.
     * @param a artifact
     * @return maven project
     * @throws ProjectBuildingException thrown if there are problems creating the project
     */
    protected MavenProject getProjectFromArtifact(final Artifact a) throws ProjectBuildingException {
        final ProjectBuildingRequest request = session.getProjectBuildingRequest();
        request.setLocalRepository(session.getLocalRepository());
        request.setRemoteRepositories(this.project.getRemoteArtifactRepositories());
        request.setProcessPlugins(false);
        return this.mavenProjectBuilder.build(a, request).getProject();
    }

    /**
     * Unzips all dependency sources.
     * 
     * @param factory Component factory
     * @param session maven session
     * @param targetDir target directory
     * @param sourceScope dependency scope to unpack from
     * @param depConfig the dependency config 
     *
     * @throws IOException if something goes wrong while prepareing the dependencies
     * @throws PhpException php exceptions can fly everywhere..
     */
    public void prepareDependencies(
            IComponentFactory factory,
            MavenSession session,
            File targetDir,
            String sourceScope,
            IDependencyConfiguration depConfig)
            throws IOException, PhpException, MojoExecutionException {
        final Set<Artifact> deps = this.project.getArtifacts();
        for (final Artifact dep : deps) {
            if (!sourceScope.equals(dep.getScope())) {
                continue;
            }
            
            final List<String> packedElements = new ArrayList<String>();
            packedElements.add(dep.getFile().getAbsolutePath());
            boolean isClassic = true;
            for (final IDependency depCfg : depConfig.getDependencies()) {
                if (depCfg.getGroupId().equals(dep.getGroupId()) &&
                        depCfg.getArtifactId().equals(dep.getArtifactId())) {
                    isClassic = false;
                    for (final IAction action : depCfg.getActions()) {
                        switch (action.getType()) {
                            case ACTION_CLASSIC:
                                isClassic = true;
                                break;
                            case ACTION_PEAR:
                                this.log.info(dep.getFile().getAbsolutePath() + " will be installed through pear");
                                // TODO add support
                                throw new PhpCoreException("pear installed currently not supported");
                            case ACTION_IGNORE:
                                // do nothing, isClassic should be false so that it is ignored
                                this.log.info(dep.getFile().getAbsolutePath() + " will be ignored");
                                break;
                            case ACTION_INCLUDE:
                                this.log.info(dep.getFile().getAbsolutePath() + " will be added on include path");
                                break;
                            case ACTION_EXTRACT:
                                this.log.info(dep.getFile().getAbsolutePath() + " will be extracted to " +
                                    ((IActionExtract) action).getTargetPath());
                                if (((IActionExtract) action).getPharPath() == null ||
                                    ((IActionExtract) action).getPharPath().equals("/")) {
                                    FileHelper.unzipElements(
                                        this.log,
                                        new File(((IActionExtract) action).getTargetPath()),
                                        packedElements,
                                        factory,
                                        session);
                                } else {
                                    // TODO add support
                                    throw new PhpCoreException("paths inside phar currently not supported");
                                }
                                break;
                            case ACTION_EXTRACT_INCLUDE:
                                this.log.info(dep.getFile().getAbsolutePath() + " will be extracted to " +
                                    ((IActionExtractAndInclude) action).getTargetPath() + " and added on " +
                                    "include path");
                                if (((IActionExtractAndInclude) action).getPharPath() == null ||
                                    ((IActionExtractAndInclude) action).getPharPath().equals("/")) {
                                    FileHelper.unzipElements(
                                        this.log,
                                        new File(((IActionExtractAndInclude) action).getTargetPath()),
                                        packedElements,
                                        factory,
                                        session);
                                } else {
                                    // TODO add support
                                    throw new PhpCoreException("paths inside phar currently not supported");
                                }
                                break;
                        }
                    }
                }
            }
            
            if (isClassic) {
                this.log.info("Extracting " + dep.getFile().getAbsolutePath() + " to target directory");
                try {
                    if (this.getProjectFromArtifact(dep).getFile() != null) {
                        // Reference to a local project; should only happen in IDEs or multi-project-poms
                        this.log.debug("Dependency resolved to a local project. skipping.");
                        // the maven-php-project plugin will fix it by adding the include paths
                        continue;
                    }
                } catch (ProjectBuildingException ex) {
                    throw new IOException("Problems creating maven project from dependency", ex);
                }
                FileHelper.unzipElements(this.log, targetDir, packedElements, factory, session);
            }
        }
    }

}
