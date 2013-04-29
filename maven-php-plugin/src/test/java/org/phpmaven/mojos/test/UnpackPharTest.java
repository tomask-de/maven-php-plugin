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
import java.io.StringReader;

import org.apache.maven.execution.MavenSession;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.phpmaven.plugin.phar.UnpackPharMojo;
import org.phpmaven.test.AbstractTestCase;

/**
 * Testing the phar support with dependency extraction.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public class UnpackPharTest extends AbstractTestCase {
    
    /**
     * tests the goal "unpack-phar"
     *
     * @throws Exception 
     */
    public void testGoal() throws Exception {
    	final MavenSession session = this.createSimpleSession("mojos-phar/unpack-phar");
    	final File phar = new File(session.getCurrentProject().getBasedir(), "phar-with-dep1-folders-0.0.1.phar");
    	final File fileA = new File(session.getCurrentProject().getBasedir(), "target/folderA/MyClassA.php");
    	final File fileB = new File(session.getCurrentProject().getBasedir(), "target/folderB/MyClassB.php");
    	
    	assertTrue(phar.exists());
    	assertFalse(fileA.exists());
    	assertFalse(fileB.exists());
    	
    	Xpp3Dom config = Xpp3DomBuilder.build(new StringReader(
    			"<configuration>" +
    			"<phar>"+phar.getAbsolutePath()+"</phar>" +
    			"<target>"+new File(session.getCurrentProject().getBasedir(), "target").getAbsolutePath()+"</target>" +
    			"</configuration>"));
    	UnpackPharMojo unpackMojo = this.createConfiguredMojo(
    			UnpackPharMojo.class, session,
    			"org.phpmaven", "maven-php-plugin", "2.0.3-SNAPSHOT",
    			"extract-phar",
    			config);
    	unpackMojo.execute();
    	
    	assertTrue(fileA.exists());
    	assertTrue(fileB.exists());
    }

}
