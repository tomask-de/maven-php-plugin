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

package org.phpmaven.core.test;

import java.io.File;

import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.it.util.ResourceExtractor;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingRequest;
import org.codehaus.plexus.PlexusTestCase;
import org.sonatype.aether.RepositorySystemSession;

/**
 * Abstract base class for testing the core module.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
abstract class AbstractTestCase extends PlexusTestCase {

    /**
     * Creates a maven session with given test directory (name relative to this class package).
     * 
     * @param strTestDir the relative folder containing the pom.xml to be used
     * @return the maven session
     * @throws Exception thrown on errors
     */
    protected MavenSession createSession(final String strTestDir)
        throws Exception {
        final File testDir = ResourceExtractor.simpleExtractResources(
                getClass(), "/org/phpmaven/core/test/projects/" + strTestDir);
        final RepositorySystemSession systemSession = null;
        final MavenExecutionRequest request = new DefaultMavenExecutionRequest();
        final MavenExecutionResult result = null;
        final MavenSession session = new MavenSession(getContainer(), systemSession, request, result);
        final File projectFile = new File(testDir, "pom.xml");
        final ProjectBuildingRequest buildingRequest = new DefaultProjectBuildingRequest();
        final MavenProject project = lookup(ProjectBuilder.class).build(projectFile, buildingRequest).getProject();
        session.setCurrentProject(project);
        return session;
    }

}