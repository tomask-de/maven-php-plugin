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

import java.io.File;

import org.apache.maven.execution.MavenSession;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.phpmaven.plugin.build.PhpResources;
import org.phpmaven.plugin.build.PhpTestResources;
import org.phpmaven.test.AbstractTestCase;

/**
 * Test copying the resources to target directory.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public class CopyTest extends AbstractTestCase {

    /**
     * tests the goal "compile" with sources.
     *
     * @throws Exception 
     */
    public void testCompile() throws Exception {
    	final MavenSession session = this.createSimpleSession("mojos-compile/source-copy");
    	
    	final PhpResources resourcesMojo = this.createConfiguredMojo(
    			PhpResources.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"resources",
    			new Xpp3Dom("configuration"));
    	resourcesMojo.execute();
    	
    	assertTrue(new File(session.getCurrentProject().getBasedir(), "target/classes/MyClass.php").exists());
    }

    /**
     * tests the goal "test-compile" with sources.
     *
     * @throws Exception 
     */
    public void testTestCompile() throws Exception {
    	final MavenSession session = this.createSimpleSession("mojos-compile/source-copy");
    	
    	final PhpTestResources resourcesMojo = this.createConfiguredMojo(
    			PhpTestResources.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"testResources",
    			new Xpp3Dom("configuration"));
    	resourcesMojo.execute();
    	
    	assertTrue(new File(session.getCurrentProject().getBasedir(), "target/test-classes/FooTest.php").exists());
    }

}