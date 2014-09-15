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
import org.apache.maven.repository.RepositorySystem;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.phpmaven.core.ConfigurationParameter;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.dependency.IAction;
import org.phpmaven.dependency.IAction.ActionType;
import org.phpmaven.dependency.IActionExtract;
import org.phpmaven.dependency.IActionExtractAndInclude;
import org.phpmaven.dependency.IDependency;
import org.phpmaven.dependency.IDependencyConfiguration;
import org.phpmaven.phpexec.library.IPhpExecutable;
import org.phpmaven.phpexec.library.PhpCoreException;
import org.phpmaven.phpexec.library.PhpException;
import org.phpmaven.project.IPhpProject;
import org.phpmaven.project.IProjectPhpExecution;
import org.phpmaven.statedb.IStateDatabase;

/**
 * The php project implementation.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.3
 */
@Component(role = IPhpProject.class, instantiationStrategy = "per-lookup")
public class PhpProject implements IPhpProject {
	
	/**
	 * 
	 */
	private static final String DEPSTATEKEY = "DependencyState";

	/**
	 * 
	 */
	private static final String ARTIFACTID = "maven-php-project";

	/**
	 * 
	 */
	private static final String GROUPID = "org.phpmaven";

	/**
	 * the component factory
	 */
	@Requirement
	private IComponentFactory factory;
	
	/**
	 * The dependency configuration
	 */
	@Requirement
	private IDependencyConfiguration depConfig;
	
	/**
	 * The maven session
	 */
	@ConfigurationParameter(name="session", expression="${session}")
	private MavenSession session;
	
	/**
	 * The state database
	 */
	@Requirement
	private IStateDatabase stateDatabase;
    
    /**
     * The maven project builder.
     */
	@Requirement
    private ProjectBuilder mavenProjectBuilder;
    
    /**
     * the repository system.
     */
    @Requirement
    private RepositorySystem reposSystem;

	@Override
	public void prepareDependencies(final Log log, File targetDir, String sourceScope)
			throws MojoExecutionException, PhpException {
		DependencyState depState = this.stateDatabase.get(GROUPID, ARTIFACTID, DEPSTATEKEY, DependencyState.class);
		if (depState == null) {
			depState = new DependencyState();
		}
		if (!depState.containsKey(sourceScope) || !depState.get(sourceScope).getDefaultTargetDir().equals(targetDir)) {
			final DependencyInformation info = new DependencyInformation();
			depState.put(sourceScope, info);
			info.setDefaultTargetDir(targetDir);
		}
		final DependencyInformation info = depState.get(sourceScope);
		
		final BootstrapInfo bootstrapInfo = new BootstrapInfo();
		
		try {
			final MavenProject project = this.session.getCurrentProject();
			final Set<Artifact> deps = project.getArtifacts();
	        for (final Artifact dep : deps) {
	            if (!sourceScope.equals(dep.getScope())) {
	                continue;
	            }
	            
	            final List<String> packedElements = new ArrayList<String>();
	            packedElements.add(dep.getFile().getAbsolutePath());
	            for (final IAction action : DependencyHelper.getDependencyActions(dep, depConfig, reposSystem, session, mavenProjectBuilder, factory)) {
                    switch (action.getType()) {
                    	case ACTION_BOOTSTRAP:
                    		performBootstrap(log, info, dep, bootstrapInfo);
                    		break;
                        case ACTION_CLASSIC:
                        	performClassic(log, targetDir, dep, packedElements, info);
                            break;
                        case ACTION_PEAR:
                        	performPearInstall(log, dep, info);
                        	break;
                        case ACTION_IGNORE:
                            // do nothing, isClassic should be false so that it is ignored
                            log.info(dep.getFile().getAbsolutePath() + " will be ignored");
                            break;
                        case ACTION_INCLUDE:
                            log.info(dep.getFile().getAbsolutePath() + " will be added on include path");
                            break;
                        case ACTION_EXTRACT:
                        	performExtraction(log, dep, packedElements,
								action, info);
                            break;
                        case ACTION_EXTRACT_INCLUDE:
                        	performExtractAndInclude(log, dep,
								packedElements, action, info);
                            break;
                    }
                }
	        }
		} catch (IOException ex) {
			throw new MojoExecutionException("Failed preparing dependencies", ex);
		}
		
		if (!bootstrapInfo.isEmpty()) {
			// invoke the bootstrap
			invokeBootstrap(log, bootstrapInfo, targetDir, sourceScope, depConfig.getBootstrapFile());
		}
		
		this.stateDatabase.set(GROUPID, ARTIFACTID, DEPSTATEKEY, depState);
	}

	/**
	 * @param bootstrapInfo
	 * @param targetDir
	 * @param sourceScope
	 * @param bootstrapFile
	 */
	private void invokeBootstrap(Log log, BootstrapInfo bootstrapInfo, File targetDir,
			String sourceScope, File bootstrapFile) throws PhpException {
		try {
			final IProjectPhpExecution config = this.factory.lookup(
	                IProjectPhpExecution.class,
	                IComponentFactory.EMPTY_CONFIG,
	                this.session);
	        final IPhpExecutable exec = config.getExecutionConfiguration(
	                null,
	                this.session.getCurrentProject()).getPhpExecutable();
	        final StringBuffer script = new StringBuffer();
	        script.append("$mavenDependencies = array(\n");
	        for (final BootstrapFileInfo fileInfo : bootstrapInfo) {
	        	script.append("    array(\n");
	        	script.append("        'groupId' => '").append(fileInfo.getGroupId()).append("',\n");
	        	script.append("        'artifactId' => '").append(fileInfo.getArtifactId()).append("',\n");
	        	script.append("        'version' => '").append(fileInfo.getVersion()).append("',\n");
	        	script.append("        'pharFile' => '").append(fileInfo.getPharFile().getAbsolutePath().replace("\\", "\\\\").replace("'", "\\'")).append("',\n");
	        	script.append("        'isNew' => ").append(fileInfo.isNew() ? "true" : "false").append(",\n");
	        	script.append("        'isChanged' => ").append(fileInfo.isChanged() ? "true" : "false").append("\n");
	        	script.append("    ),\n");
	        }
	        script.append("    'scope' => '").append(sourceScope).append("',\n");
	        script.append("    'targetDir' => '").append(targetDir.getAbsolutePath().replace("\\", "\\\\").replace("'", "\\'")).append("'\n");
	        script.append(");\n");
	        script.append("require '").append(bootstrapFile.getAbsolutePath().replace("\\", "\\\\").replace("'", "\\'")).append("';\n");
	        exec.executeCode("", script.toString());
		} catch (ComponentLookupException ex) {
			throw new PhpCoreException("Unable to execute bootstrap script", ex);
		} catch (PlexusConfigurationException ex) {
			throw new PhpCoreException("Unable to execute bootstrap script", ex);
		}
	}

	/**
	 * @param info
	 * @param dep
	 */
	private void performBootstrap(Log log, final DependencyInformation info,
			final Artifact dep, BootstrapInfo bootstrapInfo) {
		final String depKey = getDepKey(dep);
		log.debug("Dependency " + depKey + " will be passed to bootstrap.");
		final BootstrapFileInfo fileInfo = new BootstrapFileInfo();
		fileInfo.setGroupId(dep.getGroupId());
		fileInfo.setArtifactId(dep.getArtifactId());
		fileInfo.setVersion(dep.getVersion());
		fileInfo.setPharFile(dep.getFile());
		DependencyArtifact artifact = info.get(depKey);
		if (artifact == null || !artifact.getActionStatement().equals("bootstrap")) {
			fileInfo.setNew(true);
			artifact = new DependencyArtifact();
			artifact.setDepKey(depKey);
			artifact.setActionStatement("bootstrap");
			artifact.setFileTimestamp(dep.getFile().lastModified());
			artifact.setReposFile(dep.getFile());
		} else if (artifact.getFileTimestamp() != dep.getFile().lastModified() || !artifact.getReposFile().equals(dep.getFile())) {
			fileInfo.setChanged(true);
			artifact.setFileTimestamp(dep.getFile().lastModified());
			artifact.setReposFile(dep.getFile());
		}
		bootstrapInfo.add(fileInfo);
	}

	/**
	 * @param log
	 * @param targetDir
	 * @param dep
	 * @param packedElements
	 * @param info 
	 * @throws IOException
	 */
	private void performClassic(final Log log, File targetDir,
			final Artifact dep, final List<String> packedElements, DependencyInformation info)
			throws IOException {
		try {
		    final String depKey = getDepKey(dep);
			if (this.getProjectFromArtifact(dep).getFile() != null) {
		        // Reference to a local project; should only happen in IDEs or multi-project-poms
		        log.debug("Dependency " + depKey + " resolved to a local project. skipping.");
		        // the maven-php-project plugin will fix it by adding the include paths
		    } else {
				final DependencyArtifact depArtifact = new DependencyArtifact();
				depArtifact.setDepKey(depKey);
				depArtifact.setActionStatement("classic");
				depArtifact.setFileTimestamp(dep.getFile().lastModified());
				depArtifact.setReposFile(dep.getFile());
				if (info.containsKey(depKey) && info.get(depKey).equals(depArtifact)) {
					// check info for changes on that dependency
					log.info("Dependency " + depKey + " is up to date. skipping...");
				} else {
					log.info("Extracting " + dep.getFile().getAbsolutePath() + " to target directory");
					FileHelper.unzipElements(log, targetDir, packedElements, factory, session);
					info.put(depKey, depArtifact);
				}
		    }
		} catch (ProjectBuildingException ex) {
		    throw new IOException("Problems creating maven project from dependency", ex);
		}
	}

	/**
	 * @param log
	 * @param dep
	 * @param packedElements
	 * @param action
	 * @param info 
	 * @throws IOException
	 * @throws PhpCoreException
	 */
	private void performExtractAndInclude(final Log log, final Artifact dep,
			final List<String> packedElements, final IAction action, DependencyInformation info)
			throws IOException, PhpCoreException {
		
		final String depKey = getDepKey(dep);
		
		try {
			if (this.getProjectFromArtifact(dep).getFile() != null) {
				// Reference to a local project; should only happen in IDEs or multi-project-poms
				// TODO add support
				throw new PhpCoreException("extract action on local project " + depKey + " currently not supported");
			}
		} catch (ProjectBuildingException ex) {
		    throw new IOException("Problems creating maven project from dependency", ex);
		}
		
		final DependencyArtifact depArtifact = new DependencyArtifact();
		depArtifact.setDepKey(depKey);
		depArtifact.setActionStatement("extractAndInclude:" + ((IActionExtractAndInclude) action).getPharPath() + ":" + ((IActionExtractAndInclude) action).getTargetPath());
		depArtifact.setFileTimestamp(dep.getFile().lastModified());
		depArtifact.setReposFile(dep.getFile());
		if (info.containsKey(depKey) && info.get(depKey).equals(depArtifact)) {
			// check info for changes on that dependency
			log.info("Dependency " + depKey + " is up to date. skipping...");
			return;
		}
		
		log.info(dep.getFile().getAbsolutePath() + " will be extracted to " +
			    ((IActionExtractAndInclude) action).getTargetPath() + " and added on " +
			    "include path");
			
		if (((IActionExtractAndInclude) action).getPharPath() == null ||
		    ((IActionExtractAndInclude) action).getPharPath().equals("/")) {
		    FileHelper.unzipElements(
		        log,
		        new File(((IActionExtractAndInclude) action).getTargetPath()),
		        packedElements,
		        factory,
		        session);
		} else {
		    // TODO add support
		    throw new PhpCoreException("paths inside phar currently not supported");
		}
	}

	/**
	 * @param log
	 * @param dep
	 * @param packedElements
	 * @param action
	 * @param info 
	 * @throws IOException
	 * @throws PhpCoreException
	 */
	private void performExtraction(final Log log, final Artifact dep,
			final List<String> packedElements, final IAction action, DependencyInformation info)
			throws IOException, PhpCoreException {
		final String depKey = getDepKey(dep);
		try {
			if (this.getProjectFromArtifact(dep).getFile() != null) {
				// Reference to a local project; should only happen in IDEs or multi-project-poms
				// TODO add support
				throw new PhpCoreException("extract action on local project " + depKey + " currently not supported");
			}
		} catch (ProjectBuildingException ex) {
		    throw new IOException("Problems creating maven project from dependency", ex);
		}
		
		final DependencyArtifact depArtifact = new DependencyArtifact();
		depArtifact.setDepKey(depKey);
		depArtifact.setActionStatement("extract:" + ((IActionExtract) action).getPharPath() + ":" + ((IActionExtract) action).getTargetPath());
		depArtifact.setFileTimestamp(dep.getFile().lastModified());
		depArtifact.setReposFile(dep.getFile());
		if (info.containsKey(depKey) && info.get(depKey).equals(depArtifact)) {
			// check info for changes on that dependency
			log.info("Dependency " + depKey + " is up to date. skipping...");
			return;
		}
		
		log.info(dep.getFile().getAbsolutePath() + " will be extracted to " +
			    ((IActionExtract) action).getTargetPath());
		
		if (((IActionExtract) action).getPharPath() == null ||
		    ((IActionExtract) action).getPharPath().equals("/")) {
		    FileHelper.unzipElements(
		        log,
		        new File(((IActionExtract) action).getTargetPath()),
		        packedElements,
		        factory,
		        session);
		} else {
		    // TODO add support
		    throw new PhpCoreException("paths inside phar currently not supported");
		}
	}

	/**
	 * @param log
	 * @param dep
	 * @param info 
	 * @throws PhpCoreException
	 */
	private void performPearInstall(final Log log, final Artifact dep, DependencyInformation info)
			throws PhpCoreException {
		log.info(dep.getFile().getAbsolutePath() + " will be installed through pear");
		// TODO add support
		throw new PhpCoreException("pear installed currently not supported");
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
        request.setRemoteRepositories(this.session.getCurrentProject().getRemoteArtifactRepositories());
        request.setProcessPlugins(false);
        return this.mavenProjectBuilder.build(a, request).getProject();
    }
    
    private String getDepKey(Artifact dep) {
    	return dep.getGroupId()+":"+dep.getArtifactId()+":"+dep.getVersion()+":"+dep.getClassifier();
    }

	@Override
	public void validateDependencies() throws MojoExecutionException {
		// check if we have a dependency that is not added to dependency section
		final MavenProject project = this.session.getCurrentProject();
		final Set<Artifact> deps = project.getArtifacts();
		for (final IDependency depCfg : depConfig.getDependencies()) {
			for (final IAction action : depCfg.getActions()) {
				if (action.getType() == ActionType.ACTION_BOOTSTRAP && (depConfig.getBootstrapFile() == null || !depConfig.getBootstrapFile().exists())) {
					throw new MojoExecutionException("There are bootstrap actions but bootstrap file '" + depConfig.getBootstrapFile() + "' does not exist or was not set.");
				}
			}
			boolean found = false;
			for (final Artifact dep : deps) {
	            if (depCfg.getGroupId().equals(dep.getGroupId()) &&
	                    depCfg.getArtifactId().equals(dep.getArtifactId())) {
	            	found = true;
	            	break;
	            }
			}
			if (!found) {
				throw new MojoExecutionException("Dependency " + depCfg.getGroupId() + ":" + depCfg.getArtifactId() + " has a dependency configuration but is not added to dependency section.");
			}
		}
	}

}
