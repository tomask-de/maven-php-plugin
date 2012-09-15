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

package org.phpmaven.test;

import java.io.File;

import junit.framework.TestCase;

import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;

/**
 * Testing the phar support.
 * 
 * @author Martin Eisengardt
 */
public class PharSupportTest extends TestCase {
    
    /**
     * Setup test case.
     */
    protected void setUp() throws Exception {
        // required for mojo lookups to work
        super.setUp();
    }
    

    /**
     * tests the goal "install" with simple phar file.
     *
     * @throws Exception
     */
    public void testGoalTestWithSimplePhar() throws Exception {
        final File testDir = ResourceExtractor.simpleExtractResources( getClass(), "/org/phpmaven/test/projects/phar-simple");
        
        final Verifier verifier = new Verifier( testDir.getAbsolutePath() );
        
        // delete the pom from previous runs
        verifier.deleteArtifact( "org.phpmaven.test", "phar-simple", "0.0.1", "pom" );
        verifier.deleteArtifact( "org.phpmaven.test", "phar-simple", "0.0.1", "phar" );

        // execute testing
        verifier.executeGoal("install");

        // verify no error was thrown
        verifier.verifyErrorFreeLog();

        // reset the streams
        verifier.resetStreams();
        
        verifier.assertArtifactPresent("org.phpmaven.test", "phar-simple", "0.0.1", "pom");
        verifier.assertArtifactPresent("org.phpmaven.test", "phar-simple", "0.0.1", "phar");
    }

    /**
     * tests the goal "install" with autoprepend file.
     *
     * @throws Exception
     */
    public void testGoalTestWithAutoprepend() throws Exception {
        final File testDir = ResourceExtractor.simpleExtractResources( getClass(), "/org/phpmaven/test/projects/phar-autoprepend");
        
        final Verifier verifier = new Verifier( testDir.getAbsolutePath() );
        
        // delete the pom from previous runs
        verifier.deleteArtifact( "org.phpmaven.test", "phar-autoprepend", "0.0.1", "pom" );
        verifier.deleteArtifact( "org.phpmaven.test", "phar-autoprepend", "0.0.1", "phar" );

        // execute testing
        verifier.executeGoal("install");

        // verify no error was thrown
        verifier.verifyErrorFreeLog();

        // reset the streams
        verifier.resetStreams();
        
        verifier.assertArtifactPresent("org.phpmaven.test", "phar-autoprepend", "0.0.1", "pom");
        verifier.assertArtifactPresent("org.phpmaven.test", "phar-autoprepend", "0.0.1", "phar");
    }

    /**
     * tests the goal "install" with autoprepend file.
     *
     * @throws Exception
     */
    public void testGoalTestWithDependencies() throws Exception {
        final File testDirDep1 = ResourceExtractor.simpleExtractResources( getClass(), "/org/phpmaven/test/projects/phar-with-dep1");
        final File testDirDep2 = ResourceExtractor.simpleExtractResources( getClass(), "/org/phpmaven/test/projects/phar-with-dep2");
        
        final Verifier verifierDep1 = new Verifier( testDirDep1.getAbsolutePath() );
        
        // delete the pom from previous runs
        verifierDep1.deleteArtifact( "org.phpmaven.test", "phar-with-dep1", "0.0.1", "pom" );
        verifierDep1.deleteArtifact( "org.phpmaven.test", "phar-with-dep1", "0.0.1", "phar" );

        // execute testing
        verifierDep1.executeGoal("install");

        // verify no error was thrown
        verifierDep1.verifyErrorFreeLog();

        // reset the streams
        verifierDep1.resetStreams();
        
        verifierDep1.assertArtifactPresent("org.phpmaven.test", "phar-with-dep1", "0.0.1", "pom");
        verifierDep1.assertArtifactPresent("org.phpmaven.test", "phar-with-dep1", "0.0.1", "phar");

        final Verifier verifierDep2 = new Verifier( testDirDep2.getAbsolutePath() );
        
        // delete the pom from previous runs
        verifierDep2.deleteArtifact( "org.phpmaven.test", "phar-with-dep2", "0.0.1", "pom" );
        verifierDep2.deleteArtifact( "org.phpmaven.test", "phar-with-dep2", "0.0.1", "phar" );

        // execute testing
        verifierDep2.executeGoal("install");

        // verify no error was thrown
        verifierDep2.verifyErrorFreeLog();

        // reset the streams
        verifierDep2.resetStreams();
        
        verifierDep2.assertArtifactPresent("org.phpmaven.test", "phar-with-dep2", "0.0.1", "pom");
        verifierDep2.assertArtifactPresent("org.phpmaven.test", "phar-with-dep2", "0.0.1", "phar");
    }

}
