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
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;

/**
 * Test the site generation.
 * 
 * @auhor Martin Eisengardt
 */
public class SiteTest extends TestCase {
    
    /**
     * Setup test case.
     */
    protected void setUp() throws Exception {
        // required for mojo lookups to work
        super.setUp();
    }

    /**
     * tests the goal "site" with a project continaing all default reports.
     *
     * @throws Exception
     */
    public void testSite() throws Exception {
        final File testDir = ResourceExtractor.simpleExtractResources( getClass(), "/org/phpmaven/test/projects/site-all");
        
        final Verifier verifier = new Verifier( testDir.getAbsolutePath() );
        
        // delete the pom from previous runs
        verifier.deleteArtifact( "org.phpmaven.test", "site-all", "0.0.1", "pom" );
        verifier.deleteArtifact( "org.phpmaven.test", "site-all", "0.0.1", "phar" );
        verifier.setAutoclean(true);

        final List<String> goals = new ArrayList<String>();
        goals.add("compile");
        goals.add("test-compile");
        goals.add("site");
        verifier.executeGoals(goals);
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();
        
        // phpdocumentor report
        verifier.assertFilePresent("target/site/apidocs/phpdocumentor.html");
        verifier.assertFilePresent("target/site/apidocs/phpdocumentor/index.html");
        verifier.assertFilePresent("target/site/apidocs/phpdocumentor/packages.html");
        verifier.assertFilePresent("target/site/apidocs/phpdocumentor/default/_MyClass.php.html");
        
        // phpunit-coverage report
        verifier.assertFilePresent("target/site/phpunit/coverage.html");
        verifier.assertFilePresent("target/site/phpunit/phpunit/index.html");
        verifier.assertFilePresent("target/site/phpunit/phpunit/MyClass.php.html");
        
        // test report
        verifier.assertFilePresent("target/site/surefire-report.html");
    }

}