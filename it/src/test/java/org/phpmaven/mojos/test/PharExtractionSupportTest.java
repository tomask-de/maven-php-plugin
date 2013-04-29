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

package org.phpmaven.mojos.test;

import org.apache.maven.it.Verifier;
import org.phpmaven.test.it.AbstractTestCase;

/**
 * Testing the phar support with dependency extraction.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public class PharExtractionSupportTest extends AbstractTestCase {
    
    /**
     * tests the goal "install" with dependencies to another phar that provides a folder structure.
     *
     * @throws Exception 
     */
    public void testGoalTestWithDependenciesInFolders() throws Exception {
        final Verifier verifierDep1 = this.getPhpMavenVerifier("mojos-phar/phar-with-dep1-folders");
        
        // delete the pom from previous runs
        verifierDep1.deleteArtifact("org.phpmaven.test", "phar-with-dep1-folders", "0.0.1", "pom");
        verifierDep1.deleteArtifact("org.phpmaven.test", "phar-with-dep1-folders", "0.0.1", "phar");

        // execute testing
        verifierDep1.executeGoal("install");

        // verify no error was thrown
        verifierDep1.verifyErrorFreeLog();

        // reset the streams
        verifierDep1.resetStreams();
        
        verifierDep1.assertArtifactPresent("org.phpmaven.test", "phar-with-dep1-folders", "0.0.1", "pom");
        verifierDep1.assertArtifactPresent("org.phpmaven.test", "phar-with-dep1-folders", "0.0.1", "phar");

        final Verifier verifierDep2 = this.getPhpMavenVerifier("mojos-phar/phar-with-dep2-folders-extraction");
        
        // delete the pom from previous runs
        verifierDep2.deleteArtifact("org.phpmaven.test", "phar-with-dep2-folders", "0.0.1", "pom");
        verifierDep2.deleteArtifact("org.phpmaven.test", "phar-with-dep2-folders", "0.0.1", "phar");

        // execute testing
        verifierDep2.executeGoal("install");

        // verify no error was thrown
        verifierDep2.verifyErrorFreeLog();

        // reset the streams
        verifierDep2.resetStreams();
        
        verifierDep2.assertArtifactPresent("org.phpmaven.test", "phar-with-dep2-folders", "0.0.1", "pom");
        verifierDep2.assertArtifactPresent("org.phpmaven.test", "phar-with-dep2-folders", "0.0.1", "phar");
        
        verifierDep2.assertFilePresent("target/somepath/folderA/MyClassA.php");
        verifierDep2.assertFilePresent("target/somepath/folderB/MyClassB.php");
        verifierDep2.assertFileNotPresent("target/php-deps/folderA/MyClassA.php");
        verifierDep2.assertFileNotPresent("target/php-deps/folderB/MyClassB.php");
    }

}
