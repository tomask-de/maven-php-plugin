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
 * Test copying the resources to target directory.
 * 
 * @auhor Martin Eisengardt
 */
public class CopyTest extends TestCase {
    
    /**
     * Setup test case.
     */
    protected void setUp() throws Exception {
        // required for mojo lookups to work
        super.setUp();
    }

    /**
     * tests the goal "compile" with sources.
     *
     * @throws Exception
     */
    public void testCompile() throws Exception {
        final File testDir = ResourceExtractor.simpleExtractResources( getClass(), "/org/phpmaven/test/projects/source-copy");
        
        final Verifier verifier = new Verifier( testDir.getAbsolutePath() );
        
        // delete the pom from previous runs
        verifier.deleteArtifact( "org.phpmaven.test", "source-copy", "0.0.1", "pom" );
        verifier.deleteArtifact( "org.phpmaven.test", "source-copy", "0.0.1", "phar" );
        verifier.setAutoclean(true);

        verifier.executeGoal("compile");
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();
        
        verifier.assertFilePresent("target/classes/MyClass.php");
    }

    /**
     * tests the goal "test-compile" with sources.
     *
     * @throws Exception
     */
    public void testTestCompile() throws Exception {
        final File testDir = ResourceExtractor.simpleExtractResources( getClass(), "/org/phpmaven/test/projects/source-copy");
        
        final Verifier verifier = new Verifier( testDir.getAbsolutePath() );
        
        // delete the pom from previous runs
        verifier.deleteArtifact( "org.phpmaven.test", "source-copy", "0.0.1", "pom" );
        verifier.deleteArtifact( "org.phpmaven.test", "source-copy", "0.0.1", "phar" );
        verifier.setAutoclean(true);

        verifier.executeGoal("test-compile");
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();
        
        verifier.assertFilePresent("target/test-classes/FooTest.php");
    }

}