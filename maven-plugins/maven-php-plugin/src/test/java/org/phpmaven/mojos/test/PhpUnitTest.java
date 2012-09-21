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

import java.io.StringReader;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.phpmaven.plugin.build.PhpExtractDeps;
import org.phpmaven.plugin.build.PhpResources;
import org.phpmaven.plugin.build.PhpTest;
import org.phpmaven.plugin.build.PhpTestExtractDeps;
import org.phpmaven.plugin.build.PhpTestResources;
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
    public void testGoalTestWithAutoprependFile() throws Exception {
    	final MavenSession session = this.createSessionForPhpMaven("mojos-phpunit/test-autoprepend");
    	
    	final PhpResources resourcesMojo = this.createConfiguredMojo(
    			PhpResources.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"resources",
    			new Xpp3Dom("configuration"));
		resourcesMojo.execute();
    	final PhpTestResources testResourcesMojo = this.createConfiguredMojo(
    			PhpTestResources.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"testResources",
    			new Xpp3Dom("configuration"));
    	testResourcesMojo.execute();

    	this.resolveProjectDependencies(session);
		final PhpExtractDeps extractDepsMojo = this.createConfiguredMojo(
				PhpExtractDeps.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"extractDependencies",
    			new Xpp3Dom("configuration"));
		extractDepsMojo.execute();
		final PhpTestExtractDeps extractTestDepsMojo = this.createConfiguredMojo(
				PhpTestExtractDeps.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"extractTestDependencies",
    			new Xpp3Dom("configuration"));
		extractTestDepsMojo.execute();
		
		final PhpTest test = this.createConfiguredMojo(
				PhpTest.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"test",
    			new Xpp3Dom("configuration"));
		test.execute();
    }

    /**
     * tests the goal "test" with bootstrap file (passing phpunit options).
     *
     * @throws Exception 
     */
    public void testGoalTestWithBootstrapFile() throws Exception {
    	final MavenSession session = this.createSessionForPhpMaven("mojos-phpunit/test-bootstrap");
    	
    	final PhpResources resourcesMojo = this.createConfiguredMojo(
    			PhpResources.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"resources",
    			new Xpp3Dom("configuration"));
		resourcesMojo.execute();
    	final PhpTestResources testResourcesMojo = this.createConfiguredMojo(
    			PhpTestResources.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"testResources",
    			new Xpp3Dom("configuration"));
    	testResourcesMojo.execute();

    	this.resolveProjectDependencies(session);
		final PhpExtractDeps extractDepsMojo = this.createConfiguredMojo(
				PhpExtractDeps.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"extractDependencies",
    			new Xpp3Dom("configuration"));
		extractDepsMojo.execute();
		final PhpTestExtractDeps extractTestDepsMojo = this.createConfiguredMojo(
				PhpTestExtractDeps.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"extractTestDependencies",
    			new Xpp3Dom("configuration"));
		extractTestDepsMojo.execute();
		
		Xpp3Dom config = Xpp3DomBuilder.build(new StringReader(
    			"<configuration>" +
    			"<phpUnitArguments>--bootstrap maven-autoloader.php</phpUnitArguments>" +
    			"</configuration>"));
		final PhpTest test = this.createConfiguredMojo(
				PhpTest.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"test",
    			config);
		test.execute();
    }

    /**
     * tests the goal "test" with bootstrap file (passing phpunit options).
     * 
     * @see http://trac.php-maven.org/ticket/59
     * @throws Exception 
     */
    public void testGoalTestWithBootstrap2File() throws Exception {
    	final MavenSession session = this.createSessionForPhpMaven("mojos-phpunit/test-bootstrap2");
    	
    	final PhpResources resourcesMojo = this.createConfiguredMojo(
    			PhpResources.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"resources",
    			new Xpp3Dom("configuration"));
		resourcesMojo.execute();
    	final PhpTestResources testResourcesMojo = this.createConfiguredMojo(
    			PhpTestResources.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"testResources",
    			new Xpp3Dom("configuration"));
    	testResourcesMojo.execute();

    	this.resolveProjectDependencies(session);
		final PhpExtractDeps extractDepsMojo = this.createConfiguredMojo(
				PhpExtractDeps.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"extractDependencies",
    			new Xpp3Dom("configuration"));
		extractDepsMojo.execute();
		final PhpTestExtractDeps extractTestDepsMojo = this.createConfiguredMojo(
				PhpTestExtractDeps.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"extractTestDependencies",
    			new Xpp3Dom("configuration"));
		extractTestDepsMojo.execute();
		
		final PhpTest test = this.createConfiguredMojo(
				PhpTest.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"test",
    			new Xpp3Dom("configuration"));
		test.execute();
    }

    /**
     * tests the goal "test" with simple test.
     *
     * @throws Exception 
     */
    public void testGoalTestWithTests() throws Exception {
    	final MavenSession session = this.createSessionForPhpMaven("mojos-phpunit/test-oktests");
    	
    	final PhpResources resourcesMojo = this.createConfiguredMojo(
    			PhpResources.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"resources",
    			new Xpp3Dom("configuration"));
		resourcesMojo.execute();
    	final PhpTestResources testResourcesMojo = this.createConfiguredMojo(
    			PhpTestResources.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"testResources",
    			new Xpp3Dom("configuration"));
    	testResourcesMojo.execute();

    	this.resolveProjectDependencies(session);
		final PhpExtractDeps extractDepsMojo = this.createConfiguredMojo(
				PhpExtractDeps.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"extractDependencies",
    			new Xpp3Dom("configuration"));
		extractDepsMojo.execute();
		final PhpTestExtractDeps extractTestDepsMojo = this.createConfiguredMojo(
				PhpTestExtractDeps.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"extractTestDependencies",
    			new Xpp3Dom("configuration"));
		extractTestDepsMojo.execute();
		
		final PhpTest test = this.createConfiguredMojo(
				PhpTest.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"test",
    			new Xpp3Dom("configuration"));
		test.execute();
    }

    /**
     * tests the goal "test" with simple test.
     *
     * @throws Exception 
     */
    public void testGoalTestWith2Tests() throws Exception {
    	final MavenSession session = this.createSessionForPhpMaven("mojos-phpunit/test-oktests-multiple");
    	
    	final PhpResources resourcesMojo = this.createConfiguredMojo(
    			PhpResources.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"resources",
    			new Xpp3Dom("configuration"));
		resourcesMojo.execute();
    	final PhpTestResources testResourcesMojo = this.createConfiguredMojo(
    			PhpTestResources.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"testResources",
    			new Xpp3Dom("configuration"));
    	testResourcesMojo.execute();

    	this.resolveProjectDependencies(session);
		final PhpExtractDeps extractDepsMojo = this.createConfiguredMojo(
				PhpExtractDeps.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"extractDependencies",
    			new Xpp3Dom("configuration"));
		extractDepsMojo.execute();
		final PhpTestExtractDeps extractTestDepsMojo = this.createConfiguredMojo(
				PhpTestExtractDeps.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"extractTestDependencies",
    			new Xpp3Dom("configuration"));
		extractTestDepsMojo.execute();
		
		final PhpTest test = this.createConfiguredMojo(
				PhpTest.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"test",
    			new Xpp3Dom("configuration"));
		test.execute();
    }

    /**
     * tests the goal "test" without any test.
     *
     * @throws Exception 
     */
    public void testGoalTestWithNoTests() throws Exception {
    	final MavenSession session = this.createSessionForPhpMaven("mojos-phpunit/test-notests");
    	
    	final PhpResources resourcesMojo = this.createConfiguredMojo(
    			PhpResources.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"resources",
    			new Xpp3Dom("configuration"));
		resourcesMojo.execute();
    	final PhpTestResources testResourcesMojo = this.createConfiguredMojo(
    			PhpTestResources.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"testResources",
    			new Xpp3Dom("configuration"));
    	testResourcesMojo.execute();

    	this.resolveProjectDependencies(session);
		final PhpExtractDeps extractDepsMojo = this.createConfiguredMojo(
				PhpExtractDeps.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"extractDependencies",
    			new Xpp3Dom("configuration"));
		extractDepsMojo.execute();
		final PhpTestExtractDeps extractTestDepsMojo = this.createConfiguredMojo(
				PhpTestExtractDeps.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"extractTestDependencies",
    			new Xpp3Dom("configuration"));
		extractTestDepsMojo.execute();
		
		final PhpTest test = this.createConfiguredMojo(
				PhpTest.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"test",
    			new Xpp3Dom("configuration"));
		test.execute();
    }

    /**
     * tests the goal "test" without any test; complaining.
     *
     * @throws Exception 
     */
    public void testGoalTestWithNoTestsFailing() throws Exception {
    	final MavenSession session = this.createSessionForPhpMaven("mojos-phpunit/test-notests");
    	session.getUserProperties().setProperty("failIfNoTests", "true");
    	
    	final PhpResources resourcesMojo = this.createConfiguredMojo(
    			PhpResources.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"resources",
    			new Xpp3Dom("configuration"));
		resourcesMojo.execute();
    	final PhpTestResources testResourcesMojo = this.createConfiguredMojo(
    			PhpTestResources.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"testResources",
    			new Xpp3Dom("configuration"));
    	testResourcesMojo.execute();

    	this.resolveProjectDependencies(session);
		final PhpExtractDeps extractDepsMojo = this.createConfiguredMojo(
				PhpExtractDeps.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"extractDependencies",
    			new Xpp3Dom("configuration"));
		extractDepsMojo.execute();
		final PhpTestExtractDeps extractTestDepsMojo = this.createConfiguredMojo(
				PhpTestExtractDeps.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"extractTestDependencies",
    			new Xpp3Dom("configuration"));
		extractTestDepsMojo.execute();
		
		final PhpTest test = this.createConfiguredMojo(
				PhpTest.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"test",
    			new Xpp3Dom("configuration"));
		try {
			test.execute();
			fail("Build failure expected");
		} catch (MojoFailureException ex) {
			assertEquals(PhpUnitTestfileWalker.FAIL_ON_NO_TEST_TEXT, ex.getMessage());
		}
    }

    /**
     * tests the goal "test" with failing tests and skip tests set to true.
     *
     * @throws Exception 
     */
    public void testGoalTestFailingSkipped() throws Exception {
    	final MavenSession session = this.createSessionForPhpMaven("mojos-phpunit/test-failing");
    	session.getUserProperties().setProperty("skipTests", "true");
    	
    	final PhpResources resourcesMojo = this.createConfiguredMojo(
    			PhpResources.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"resources",
    			new Xpp3Dom("configuration"));
		resourcesMojo.execute();
    	final PhpTestResources testResourcesMojo = this.createConfiguredMojo(
    			PhpTestResources.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"testResources",
    			new Xpp3Dom("configuration"));
    	testResourcesMojo.execute();

    	this.resolveProjectDependencies(session);
		final PhpExtractDeps extractDepsMojo = this.createConfiguredMojo(
				PhpExtractDeps.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"extractDependencies",
    			new Xpp3Dom("configuration"));
		extractDepsMojo.execute();
		final PhpTestExtractDeps extractTestDepsMojo = this.createConfiguredMojo(
				PhpTestExtractDeps.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"extractTestDependencies",
    			new Xpp3Dom("configuration"));
		extractTestDepsMojo.execute();
		
		final PhpTest test = this.createConfiguredMojo(
				PhpTest.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"test",
    			new Xpp3Dom("configuration"));
		test.execute();
    }

    /**
     * tests the goal "test" with failing tests and skip tests set to true.
     *
     * @throws Exception 
     */
    public void testGoalTestFailingSkipped2() throws Exception {
    	final MavenSession session = this.createSessionForPhpMaven("mojos-phpunit/test-failing");
    	session.getUserProperties().setProperty("maven.test.skip", "true");
    	
    	final PhpResources resourcesMojo = this.createConfiguredMojo(
    			PhpResources.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"resources",
    			new Xpp3Dom("configuration"));
		resourcesMojo.execute();
    	final PhpTestResources testResourcesMojo = this.createConfiguredMojo(
    			PhpTestResources.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"testResources",
    			new Xpp3Dom("configuration"));
    	testResourcesMojo.execute();

    	this.resolveProjectDependencies(session);
		final PhpExtractDeps extractDepsMojo = this.createConfiguredMojo(
				PhpExtractDeps.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"extractDependencies",
    			new Xpp3Dom("configuration"));
		extractDepsMojo.execute();
		final PhpTestExtractDeps extractTestDepsMojo = this.createConfiguredMojo(
				PhpTestExtractDeps.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"extractTestDependencies",
    			new Xpp3Dom("configuration"));
		extractTestDepsMojo.execute();
		
		final PhpTest test = this.createConfiguredMojo(
				PhpTest.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"test",
    			new Xpp3Dom("configuration"));
		test.execute();
    }

    /**
     * tests the goal "test" with failing tests and ignore failures set to true.
     *
     * @throws Exception 
     */
    public void testGoalTestFailingIgnored() throws Exception {
    	final MavenSession session = this.createSessionForPhpMaven("mojos-phpunit/test-failing");
    	session.getUserProperties().setProperty("maven.test.failure.ignore", "true");
    	
    	final PhpResources resourcesMojo = this.createConfiguredMojo(
    			PhpResources.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"resources",
    			new Xpp3Dom("configuration"));
		resourcesMojo.execute();
    	final PhpTestResources testResourcesMojo = this.createConfiguredMojo(
    			PhpTestResources.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"testResources",
    			new Xpp3Dom("configuration"));
    	testResourcesMojo.execute();

    	this.resolveProjectDependencies(session);
		final PhpExtractDeps extractDepsMojo = this.createConfiguredMojo(
				PhpExtractDeps.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"extractDependencies",
    			new Xpp3Dom("configuration"));
		extractDepsMojo.execute();
		final PhpTestExtractDeps extractTestDepsMojo = this.createConfiguredMojo(
				PhpTestExtractDeps.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"extractTestDependencies",
    			new Xpp3Dom("configuration"));
		extractTestDepsMojo.execute();
		
		final PhpTest test = this.createConfiguredMojo(
				PhpTest.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"test",
    			new Xpp3Dom("configuration"));
		test.execute();
    }

    /**
     * tests the goal "test" with failing tests; complaining.
     *
     * @throws Exception 
     */
    public void testGoalTestFailing() throws Exception {
    	final MavenSession session = this.createSessionForPhpMaven("mojos-phpunit/test-failing");
    	
    	final PhpResources resourcesMojo = this.createConfiguredMojo(
    			PhpResources.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"resources",
    			new Xpp3Dom("configuration"));
		resourcesMojo.execute();
    	final PhpTestResources testResourcesMojo = this.createConfiguredMojo(
    			PhpTestResources.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"testResources",
    			new Xpp3Dom("configuration"));
    	testResourcesMojo.execute();

    	this.resolveProjectDependencies(session);
		final PhpExtractDeps extractDepsMojo = this.createConfiguredMojo(
				PhpExtractDeps.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"extractDependencies",
    			new Xpp3Dom("configuration"));
		extractDepsMojo.execute();
		final PhpTestExtractDeps extractTestDepsMojo = this.createConfiguredMojo(
				PhpTestExtractDeps.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"extractTestDependencies",
    			new Xpp3Dom("configuration"));
		extractTestDepsMojo.execute();
		
		final PhpTest test = this.createConfiguredMojo(
				PhpTest.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"test",
    			new Xpp3Dom("configuration"));
		try {
			test.execute();
			fail("Expected exception not thrown");
		} catch (MojoExecutionException ex) {
			assertEquals("Test failures", ex.getMessage());
		}
    }


}
