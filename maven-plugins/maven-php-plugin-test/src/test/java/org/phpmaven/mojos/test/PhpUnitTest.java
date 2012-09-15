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

import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.phpmaven.plugin.build.PhpTest;
import org.phpmaven.plugin.php.PhpUnitTestfileWalker;
import org.phpmaven.test.AbstractTestCase;

/**
 * Testcase for php-maven mojos being present.
 * 
 * Tests: http://maven.apache.org/plugin-developers/plugin-testing.html
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public class PhpUnitTest extends AbstractTestCase {

    /**
     * tests the goal "test" with simple autoloader (autoprepend file).
     *
     * @throws Exception 
     */
    public void testGoalTestWithDependencies() throws Exception {
        final Verifier verifierDep1 = this.getPhpMavenVerifier("mojos-phpunit/test-with-dep1");
        
        // delete the pom from previous runs
        verifierDep1.deleteArtifact("org.phpmaven.test", "test-with-dep1", "0.0.1", "pom");
        verifierDep1.deleteArtifact("org.phpmaven.test", "test-with-dep1", "0.0.1", "phar");

        // execute testing
        verifierDep1.executeGoal("install");

        // verify no error was thrown
        verifierDep1.verifyErrorFreeLog();

        // reset the streams
        verifierDep1.resetStreams();
        
        verifierDep1.assertArtifactPresent("org.phpmaven.test", "test-with-dep1", "0.0.1", "pom");
        verifierDep1.assertArtifactPresent("org.phpmaven.test", "test-with-dep1", "0.0.1", "phar");

        final Verifier verifierDep2 = this.getPhpMavenVerifier("mojos-phpunit/test-with-dep2");
        
        // execute testing
        verifierDep2.executeGoal("test");

        // verify no error was thrown
        verifierDep2.verifyErrorFreeLog();

        // reset the streams
        verifierDep2.resetStreams();
    }

    /**
     * tests the goal "test" with simple autoloader (autoprepend file).
     *
     * @throws Exception 
     */
    public void testGoalTestWithAutoprependFile() throws Exception {
        final Verifier verifier = this.getPhpMavenVerifier("mojos-phpunit/test-autoprepend");
        
        // delete the pom from previous runs
        verifier.deleteArtifact("org.phpmaven.test", "test-autoprepend", "0.0.1", "pom");

        // execute testing
        verifier.executeGoal("test");

        // verify no error was thrown
        verifier.verifyErrorFreeLog();

        // reset the streams
        verifier.resetStreams();
    }

    /**
     * tests the goal "test" with bootstrap file (passing phpunit options).
     *
     * @throws Exception 
     */
    public void testGoalTestWithBootstrapFile() throws Exception {
        final Verifier verifier = this.getPhpMavenVerifier("mojos-phpunit/test-bootstrap");
        
        // delete the pom from previous runs
        verifier.deleteArtifact("org.phpmaven.test", "test-bootstrap", "0.0.1", "pom");

        // execute testing
        verifier.executeGoal("test");

        // verify no error was thrown
        verifier.verifyErrorFreeLog();

        // reset the streams
        verifier.resetStreams();
    }

    /**
     * tests the goal "test" with bootstrap file (passing phpunit options).
     * 
     * @see http://trac.php-maven.org/ticket/59
     * @throws Exception 
     */
    public void testGoalTestWithBootstrap2File() throws Exception {
        final Verifier verifier = this.getPhpMavenVerifier("mojos-phpunit/test-bootstrap2");
        
        // delete the pom from previous runs
        verifier.deleteArtifact("org.phpmaven.test", "test-bootstrap", "0.0.1", "pom");

        // execute testing
        verifier.executeGoal("test");

        // verify no error was thrown
        verifier.verifyErrorFreeLog();

        // reset the streams
        verifier.resetStreams();
    }

    /**
     * tests the goal "test" with simple test.
     *
     * @throws Exception 
     */
    public void testGoalTestWithTests() throws Exception {
        final Verifier verifier = this.getPhpMavenVerifier("mojos-phpunit/test-oktests");
        
        // delete the pom from previous runs
        verifier.deleteArtifact("org.phpmaven.test", "test-oktests", "0.0.1", "pom");

        // execute testing
        verifier.executeGoal("test");

        // verify no error was thrown
        verifier.verifyErrorFreeLog();

        // reset the streams
        verifier.resetStreams();
    }

    /**
     * tests the goal "test" with simple test.
     *
     * @throws Exception 
     */
    public void testGoalTestWith2Tests() throws Exception {
        final Verifier verifier = this.getPhpMavenVerifier("mojos-phpunit/test-oktests-multiple");
        
        // delete the pom from previous runs
        verifier.deleteArtifact("org.phpmaven.test", "test-oktests-multiple", "0.0.1", "pom");

        // execute testing
        verifier.executeGoal("test");

        // verify no error was thrown
        verifier.verifyErrorFreeLog();

        // reset the streams
        verifier.resetStreams();
    }
    
// TODO Setting singleTestInvocation to true requires at least phpunitXmlResult to be set manually.
//    /**
//     * tests the goal "test" with simple test.
//     *
//     * @throws Exception
//     */
//    public void testGoalTestWith2TestsSingleInvocation() throws Exception {
//        final File testDir = ResourceExtractor.simpleExtractResources( getClass(), "/org/phpmaven/test/projects/test-oktests-multiple");
//        
//        final Verifier verifier = new Verifier( testDir.getAbsolutePath() );
//        
//        // delete the pom from previous runs
//        verifier.deleteArtifact( "org.phpmaven.test", "test-oktests-multiple", "0.0.1", "pom" );
//        verifier.setSystemProperty("singleTestInvocation", "true");
//
//        // execute testing
//        verifier.executeGoal("test");
//
//        // verify no error was thrown
//        verifier.verifyErrorFreeLog();
//
//        // reset the streams
//        verifier.resetStreams();
//    }

    /**
     * tests the goal "test" without any test.
     *
     * @throws Exception 
     */
    public void testGoalTestWithNoTests() throws Exception {
        final Verifier verifier = this.getPhpMavenVerifier("mojos-phpunit/test-notests");
        
        // delete the pom from previous runs
        verifier.deleteArtifact("org.phpmaven.test", "test-notests", "0.0.1", "pom");

        // execute testing
        verifier.executeGoal("test");

        // verify no error was thrown
        verifier.verifyErrorFreeLog();

        // reset the streams
        verifier.resetStreams();
    }

    /**
     * tests the goal "test" without any test; complaining.
     *
     * @throws Exception 
     */
    public void testGoalTestWithNoTestsFailing() throws Exception {
        final Verifier verifier = this.getPhpMavenVerifier("mojos-phpunit/test-notests");
        // delete the pom from previous runs
        verifier.deleteArtifact("org.phpmaven.test", "test-notest", "0.0.1", "pom");

        // execute testing
        verifier.addCliOption("-DfailIfNoTests");
        try {
            verifier.executeGoal("test");
            verifier.resetStreams();
            fail("Build failure expected");
        } catch (VerificationException ex) {
            // we expect a verification exception
            verifier.verifyTextInLog(PhpUnitTestfileWalker.FAIL_ON_NO_TEST_TEXT);
            verifier.resetStreams();
        }
    }

    /**
     * tests the goal "test" with failing tests and skip tests set to true.
     *
     * @throws Exception 
     */
    public void testGoalTestFailingSkipped() throws Exception {
        final Verifier verifier = this.getPhpMavenVerifier("mojos-phpunit/test-failing");
        
        // delete the pom from previous runs
        verifier.deleteArtifact("org.phpmaven.test", "test-failing", "0.0.1", "pom");

        // execute testing
        verifier.addCliOption("-DskipTests");
        verifier.executeGoal("test");

        // verify no error was thrown
        verifier.verifyErrorFreeLog();
        verifier.verifyTextInLog(PhpUnitTestfileWalker.SKIP_TESTS_TEXT);

        // reset the streams
        verifier.resetStreams();
    }

    /**
     * tests the goal "test" with failing tests and skip tests set to true.
     *
     * @throws Exception 
     */
    public void testGoalTestFailingSkipped2() throws Exception {
        final Verifier verifier = this.getPhpMavenVerifier("mojos-phpunit/test-failing");
        
        // delete the pom from previous runs
        verifier.deleteArtifact("org.phpmaven.test", "test-failing", "0.0.1", "pom");

        // execute testing
        verifier.addCliOption("-Dmaven.test.skip");
        verifier.executeGoal("test");

        // verify no error was thrown
        verifier.verifyErrorFreeLog();
        verifier.verifyTextInLog(PhpUnitTestfileWalker.SKIP_TESTS_TEXT);

        // reset the streams
        verifier.resetStreams();
    }

    /**
     * tests the goal "test" with failing tests and ignore failures set to true.
     *
     * @throws Exception 
     */
    public void testGoalTestFailingIgnored() throws Exception {
        final Verifier verifier = this.getPhpMavenVerifier("mojos-phpunit/test-failing");
        
        // delete the pom from previous runs
        verifier.deleteArtifact("org.phpmaven.test", "test-failing", "0.0.1", "pom");

        // execute testing
        verifier.addCliOption("-Dmaven.test.failure.ignore");
        verifier.executeGoal("test");

        // verify no error was thrown
        verifier.verifyTextInLog(PhpTest.IGNORING_TEST_FAILURES_TEXT);

        // reset the streams
        verifier.resetStreams();
    }

    /**
     * tests the goal "test" with failing tests; complaining.
     *
     * @throws Exception 
     */
    public void testGoalTestFailing() throws Exception {
        final Verifier verifier = this.getPhpMavenVerifier("mojos-phpunit/test-failing");
        // delete the pom from previous runs
        verifier.deleteArtifact("org.phpmaven.test", "test-failing", "0.0.1", "pom");

        // execute testing
        try {
            verifier.executeGoal("test");
            verifier.resetStreams();
            fail("Build failure expected");
        } catch (VerificationException ex) {
            // we expect a verification exception
            verifier.resetStreams();
        }
    }


}
