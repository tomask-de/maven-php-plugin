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

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.phpmaven.plugin.build.PhpResources;
import org.phpmaven.plugin.build.PhpTestResources;
import org.phpmaven.test.AbstractTestCase;

/**
 * Testing the lint check.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public class LintTest extends AbstractTestCase {
    
    /**
     * Tests the goal "compile" with simple error within the sources.
     *
     * @throws Exception 
     */
    public void testSimpleFailure() throws Exception {
    	final MavenSession session = this.createSimpleSession("mojos-lint/check-lint");
    	
    	final PhpResources resourcesMojo = this.createConfiguredMojo(
    			PhpResources.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"resources",
    			new Xpp3Dom("configuration"));
    	try {
    		resourcesMojo.execute();
    		fail("Build failure expected");
    	} catch (MojoExecutionException ex) {
    		assertEquals("Lint check failures.", ex.getMessage());
    	}
    }

    /**
     * tests the goal "compile" with simple error in test class.
     * Will expected to work because compile does not look at the test classes.
     *
     * @throws Exception 
     */
    public void testCompileOkWithTestFailure() throws Exception {
    	final MavenSession session = this.createSimpleSession("mojos-lint/check-linttests");
    	
    	final PhpResources resourcesMojo = this.createConfiguredMojo(
    			PhpResources.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"resources",
    			new Xpp3Dom("configuration"));
		resourcesMojo.execute();
    }

    /**
     * tests the goal "test-compile" with simple error in test class.
     *
     * @throws Exception 
     */
    public void testSimpletestFailure() throws Exception {
    	final MavenSession session = this.createSimpleSession("mojos-lint/check-linttests");
    	
    	final PhpTestResources resourcesMojo = this.createConfiguredMojo(
    			PhpTestResources.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"testResources",
    			new Xpp3Dom("configuration"));
		try {
    		resourcesMojo.execute();
    		fail("Build failure expected");
    	} catch (MojoExecutionException ex) {
    		assertEquals("Lint check failures.", ex.getMessage());
    	}
    }

}
