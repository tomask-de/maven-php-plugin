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

package org.phpmaven.pear.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Build;
import org.apache.maven.model.Plugin;
import org.apache.maven.project.DefaultDependencyResolutionRequest;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.DependencyResolutionException;
import org.apache.maven.project.DependencyResolutionRequest;
import org.apache.maven.project.DependencyResolutionResult;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.project.ProjectBuildingResult;
import org.apache.maven.project.ProjectDependenciesResolver;
import org.apache.maven.repository.RepositorySystem;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.phpmaven.core.ConfigurationParameter;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.pear.IMavenPearUtility;
import org.phpmaven.pear.library.impl.Version;
import org.phpmaven.phpexec.library.PhpCoreException;
import org.phpmaven.phpexec.library.PhpException;
import org.sonatype.aether.util.version.GenericVersionScheme;
import org.sonatype.aether.version.InvalidVersionSpecificationException;

/**
 * Implementation of a pear utility via PHP.EXE and http-client.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
@Component(role = IMavenPearUtility.class, hint = "PHP_EXE", instantiationStrategy = "per-lookup")
public class PearUtility  extends org.phpmaven.pear.library.impl.PearUtility implements IMavenPearUtility {
    
    /** the generic version scheme. */
    private static final GenericVersionScheme SCHEME = new GenericVersionScheme();

    /**
     * The component factory.
     */
    @Requirement
    private IComponentFactory factory;
    
    /**
     * The maven session.
     */
    @ConfigurationParameter(name = "session", expression = "${session}")
    private MavenSession session;
    
    /**
     * the repository system.
     */
    @Requirement
    private RepositorySystem reposSystem;
    
    /**
     * The project builder.
     */
    @Requirement
    private ProjectBuilder projectBuilder;
    
    /**
     * The dependencies resolver.
     */
    @Requirement
    private ProjectDependenciesResolver dependencyResolver;

    /**
     * {@inheritDoc}
     */
    @Override
    public String convertMavenVersionToPearVersion(String src) {
        return PackageHelper.convertMavenVersionToPearVersion(src);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String convertPearVersionToMavenVersion(String src) {
        return PackageHelper.convertPearVersionToMavenVersion(src);
    }
    
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
    private Artifact resolveArtifact(String groupId, String artifactId, String version, String type, String classifier)
        throws PhpException {
        final Artifact artifact = this.reposSystem.createArtifactWithClassifier(
                groupId, artifactId, version, type, classifier);
        final ArtifactResolutionRequest request = new ArtifactResolutionRequest();
        request.setArtifact(artifact);
        request.setLocalRepository(this.session.getLocalRepository());
        request.setOffline(this.session.isOffline());
        final Set<ArtifactRepository> setRepos = new HashSet<ArtifactRepository>(
                this.session.getRequest().getRemoteRepositories());
        setRepos.addAll(this.session.getCurrentProject().getRemoteArtifactRepositories());
        request.setRemoteRepositories(new ArrayList<ArtifactRepository>(setRepos));
        final ArtifactResolutionResult result = this.reposSystem.resolve(request);
        if (!result.isSuccess()) {
            throw new PhpCoreException("dependency resolution failed for " +
                groupId + ":" + artifactId + ":" + version);
        }
        
        final Artifact resultArtifact = result.getArtifacts().iterator().next();
        return resultArtifact;
    }

    private void installFromMavenRepository(
            String groupId, String artifactId, String version,
            boolean ignoreCore) throws PhpException {
        final Artifact artifact = this.resolveArtifact(groupId, artifactId, version, "pom", null);
        final File pomFile = artifact.getFile();
        final ProjectBuildingRequest pbr = new DefaultProjectBuildingRequest(this.session.getProjectBuildingRequest());
        try {
            pbr.setProcessPlugins(false);
            final ProjectBuildingResult pbres = this.projectBuilder.build(pomFile, pbr);
            final MavenProject project = pbres.getProject();
            final DependencyResolutionRequest drr = new DefaultDependencyResolutionRequest(
                project, session.getRepositorySession());
            final DependencyResolutionResult drres = this.dependencyResolver.resolve(drr);
            // dependencies may be duplicate. ensure we have only one version (the newest).
            final Map<String, org.sonatype.aether.graph.Dependency> deps =
                new HashMap<String, org.sonatype.aether.graph.Dependency>();
            for (final org.sonatype.aether.graph.Dependency dep : drres.getDependencies()) {
                final String key = dep.getArtifact().getGroupId() + ":" + dep.getArtifact().getArtifactId();
                if (!deps.containsKey(key)) {
                    deps.put(key, dep);
                } else {
                    final org.sonatype.aether.graph.Dependency dep2 = deps.get(key);
                    final org.sonatype.aether.version.Version ver =
                        SCHEME.parseVersion(dep.getArtifact().getVersion());
                    final org.sonatype.aether.version.Version ver2 =
                        SCHEME.parseVersion(dep2.getArtifact().getVersion());
                    if (ver2.compareTo(ver) < 0) {
                        deps.put(key, dep);
                    }
                }
            }
            final List<File> filesToInstall = new ArrayList<File>();
            // first the dependencies
//            this.log.debug(
//                    "resolving tgz and project for " +
//                    groupId + ":" +
//                    artifactId + ":" + 
//                    version);
            this.resolveTgz(groupId, artifactId, version, filesToInstall, ignoreCore);
            this.resolveChannels(project);
            for (final org.sonatype.aether.graph.Dependency dep : deps.values()) {
//                this.log.debug(
//                        "resolving tgz and project for " +
//                        dep.getArtifact().getGroupId() + ":" +
//                        dep.getArtifact().getArtifactId() + ":" + 
//                        dep.getArtifact().getVersion());
                if (ignoreCore && this.isMavenCorePackage(
                    dep.getArtifact().getGroupId(),
                    dep.getArtifact().getArtifactId())) {
                    // ignore core packages
                    continue;
                }
                this.resolveTgz(
                    dep.getArtifact().getGroupId(),
                    dep.getArtifact().getArtifactId(),
                    dep.getArtifact().getVersion(),
                    filesToInstall,
                    ignoreCore);
                final Artifact depPomArtifact = this.resolveArtifact(
                    dep.getArtifact().getGroupId(),
                    dep.getArtifact().getArtifactId(),
                    dep.getArtifact().getVersion(),
                    "pom", null);
                final File depPomFile = depPomArtifact.getFile();
                final ProjectBuildingResult depPbres = this.projectBuilder.build(depPomFile, pbr);
                final MavenProject depProject = depPbres.getProject();
                this.resolveChannels(depProject);
            }
            
            Collections.reverse(filesToInstall);
            for (final File file : filesToInstall) {
                this.executePearCmd("install --force --nodeps \"" + file.getAbsolutePath() + "\"");
            }
        } catch (InvalidVersionSpecificationException ex) {
            throw new PhpCoreException(ex);
        } catch (ProjectBuildingException ex) {
            throw new PhpCoreException(ex);
        } catch (DependencyResolutionException ex) {
            throw new PhpCoreException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void installFromMavenRepository(String groupId, String artifactId, String version) throws PhpException {
        this.installFromMavenRepository(groupId, artifactId, version, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void installCoreFromMavenRepository(String groupId, String artifactId, String version) throws PhpException {
        this.installFromMavenRepository(groupId, artifactId, version, false);
    }

    /**
     * resolving the pear channels from given project.
     * @param project the project
     * @throws PhpException thrown on discover errors
     */
    private void resolveChannels(MavenProject project) throws PhpException {
        final Build build = project.getBuild();
        if (build != null) {
            for (final Plugin plugin : build.getPlugins()) {
                if ("org.phpmaven".equals(plugin.getGroupId()) &&
                        "maven-php-plugin".equals(plugin.getArtifactId())) {
                    final Xpp3Dom dom = (Xpp3Dom) plugin.getConfiguration();
                    final Xpp3Dom pearChannelsDom = dom.getChild("pearChannels");
                    if (pearChannelsDom != null) {
                        for (final Xpp3Dom child : pearChannelsDom.getChildren()) {
                            this.channelAdd(child.getValue(), null, "local-pear-channel");
                        }
                    }
                }
            }
        }
    }

    /**
     * Resolves the tgz and adds it to the files for installation.
     * @param groupId group id
     * @param artifactId artifact id
     * @param version version
     * @param filesToInstall files to be installed
     * @param ignoreCore true to ignore core packages
     * @throws PhpException thrown on resolve errors.
     */
    private void resolveTgz(
            String groupId, String artifactId, String version, List<File> filesToInstall, boolean ignoreCore)
        throws PhpException {
        if (!ignoreCore || !this.isMavenCorePackage(groupId, artifactId)) {
            final Artifact artifact = this.resolveArtifact(groupId, artifactId, version, "tgz", "pear-tgz");
            filesToInstall.add(artifact.getFile());
        }
    }

    @Override
    public boolean isMavenCorePackage(String groupId, String artifactId) {
        if (("net.php".equals(groupId)) && (
                "Archive_Tar".equals(artifactId)
                || "Console_Getopt".equals(artifactId)
                || "PEAR".equals(artifactId)
                || "Structures_Graph".equals(artifactId)
                || "XML_Util".equals(artifactId))) {
            return true;
        }
        return false;
    }

	@Override
	protected Version createVersionEx() {
		return new org.phpmaven.pear.impl.Version();
	}

}
