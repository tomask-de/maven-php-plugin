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

package org.phpmaven.project.depmng.test;

import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.it.Verifier;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.exec.IPhpExecutableConfiguration;
import org.phpmaven.project.IProjectPhpExecution;
import org.phpmaven.test.AbstractTestCase;

/**
 * test cases for PHP project support with dependency management.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public class BaseTest extends AbstractTestCase {

    /**
     * Tests if the dependency is put on include path.
     *
     * @throws Exception thrown on errors
     */
    public void testInclude() throws Exception {
        // look up the component factory
        final IComponentFactory factory = lookup(IComponentFactory.class);
        
        final Verifier verifier = this.getPhpMavenVerifier("project/depmng/lib1");
        verifier.executeGoal("install");
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();
        verifier.assertArtifactPresent("org.phpmaven.test", "lib1", "0.0.1", "pom");
        verifier.assertArtifactPresent("org.phpmaven.test", "lib1", "0.0.1", "phar");
        
        // create the execution config
        final MavenSession session = this.createSessionForPhpMaven(
                "project/depmng/proj1", false);
        this.resolveProjectDependencies(session);
        
        final IProjectPhpExecution prjConfig = factory.lookup(
                IProjectPhpExecution.class,
                IComponentFactory.EMPTY_CONFIG,
                session);
        assertNotNull(prjConfig);
        final IPhpExecutableConfiguration config = prjConfig.getExecutionConfiguration();
        assertNotNull(config);
        final List<String> includePath = config.getIncludePath();
        boolean found = false;
        for (final String path : includePath) {
            if (path.startsWith("phar://") && (
                    path.endsWith("org/phpmaven/test/lib1/0.0.1/lib1-0.0.1.phar/") ||
                    path.endsWith("org\\phpmaven\\test\\lib1\\0.0.1\\lib1-0.0.1.phar/"))) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }
   
}