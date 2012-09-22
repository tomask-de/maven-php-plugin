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

package org.phpmaven.project.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.project.ProjectBuildingResult;
import org.apache.maven.repository.RepositorySystem;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.dependency.IAction;
import org.phpmaven.dependency.IAction.ActionType;
import org.phpmaven.dependency.IDependency;
import org.phpmaven.dependency.IDependencyConfiguration;
import org.phpmaven.dependency.impl.Classic;
import org.phpmaven.phpexec.library.PhpException;

/**
 * Helper to process and calculate the dependency actions on a dependency
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.3
 */
public class DependencyHelper {
	
	/**
     * Resolves the artifact.
     * @param groupId group id
     * @param artifactId artifact id
     * @param version version
     * @param type type
     * @param classifier classifier
     * @return the resolved artifact
     * @throws PhpException thrown on resolve errors
     */
    private static Artifact resolveArtifact(
    		String groupId, String artifactId, String version, String type, String classifier, RepositorySystem reposSystem,
    		MavenSession session)
        throws MojoExecutionException {
        final Artifact artifact = reposSystem.createArtifactWithClassifier(
                groupId, artifactId, version, type, classifier);
        final ArtifactResolutionRequest request = new ArtifactResolutionRequest();
        request.setArtifact(artifact);
        request.setLocalRepository(session.getLocalRepository());
        request.setOffline(session.isOffline());
        final Set<ArtifactRepository> setRepos = new HashSet<ArtifactRepository>(
                session.getRequest().getRemoteRepositories());
        setRepos.addAll(session.getCurrentProject().getRemoteArtifactRepositories());
        request.setRemoteRepositories(new ArrayList<ArtifactRepository>(setRepos));
        final ArtifactResolutionResult result = reposSystem.resolve(request);
        if (!result.isSuccess()) {
            throw new MojoExecutionException("dependency resolution failed for " +
                groupId + ":" + artifactId + ":" + version);
        }
        
        final Artifact resultArtifact = result.getArtifacts().iterator().next();
        return resultArtifact;
    }
	
	public static Iterable<IAction> getDependencyActions(
			Artifact dep, IDependencyConfiguration depConfig, RepositorySystem reposSystem, MavenSession session,
			ProjectBuilder projectBuilder, IComponentFactory factory) throws MojoExecutionException {
		final List<IAction> actions = new ArrayList<IAction>();
		boolean hasClassic = true;
		
		// try to find dependency configuration
		for (final IDependency depCfg : depConfig.getDependencies()) {
			if (depCfg.getGroupId().equals(dep.getGroupId()) &&
				depCfg.getArtifactId().equals(dep.getArtifactId())) {
				hasClassic = false;
				for (final IAction action : depCfg.getActions()) {
					if (action.getType() == ActionType.ACTION_CLASSIC) {
						hasClassic = true;
					} else {
						actions.add(action);
					}
				}
				break;
			}
		}
		
		if (hasClassic) {
			// try to resolve project defaults
			final Artifact artifact = resolveArtifact(dep.getGroupId(), dep.getArtifactId(), dep.getVersion(), "pom", null, reposSystem, session);
			final File pomFile = artifact.getFile();
			final ProjectBuildingRequest pbr = new DefaultProjectBuildingRequest(session.getProjectBuildingRequest());
			pbr.setProcessPlugins(false);
			ProjectBuildingResult pbres;
			try {
				pbres = projectBuilder.build(pomFile, pbr);
			} catch (ProjectBuildingException e) {
				throw new MojoExecutionException("Error building project", e);
			}
			final MavenProject project = pbres.getProject();
			final Xpp3Dom pluginConfig = factory.getBuildConfig(project, "org.phpmaven", "maven-php-dependency");
			if (pluginConfig != null) {
				final Xpp3Dom defaultConfig = pluginConfig.getChild("defaults");
				if (defaultConfig != null) {
					// filter the other nodes to prevent from broken configs on future versions
					while (pluginConfig.getChildCount() > 0) pluginConfig.removeChild(0);
					pluginConfig.addChild(defaultConfig);
					IDependencyConfiguration defCfg;
					try {
						defCfg = factory.lookup(IDependencyConfiguration.class, pluginConfig, null);
					} catch (ComponentLookupException e) {
						throw new MojoExecutionException("error receiving default config", e);
					} catch (PlexusConfigurationException e) {
						throw new MojoExecutionException("error receiving default config", e);
					}
					for (final IAction action : defCfg.getDefaults()) {
						actions.add(action);
					}
					hasClassic = false;
				}
			}
			
			// if hasClassic is still true we did not find any default config
			if (hasClassic) {
				actions.add(new Classic());
			}
		}
		return actions;
	}

}
