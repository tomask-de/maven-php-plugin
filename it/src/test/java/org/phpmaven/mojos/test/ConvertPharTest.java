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
import java.util.List;

import org.apache.maven.it.Verifier;
import org.phpmaven.test.it.AbstractTestCase;

/**
 * Testing the phar support with dependency extraction.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public class ConvertPharTest extends AbstractTestCase {
    
    /**
     * tests the goal "convert-phar"
     *
     * @throws Exception 
     */
    public void testGoal() throws Exception {
        final Verifier verifierDep1 = this.getPhpMavenVerifier("mojos-phar/phar-with-dep1-folders");
        
        // delete the pom from previous runs
        verifierDep1.deleteArtifact("org.phpmaven.test", "phar-with-dep1-folders", "0.0.1", "pom");
        verifierDep1.deleteArtifact("org.phpmaven.test", "phar-with-dep1-folders", "0.0.1", "phar");

        // execute testing
        verifierDep1.executeGoal("package");

        // verify no error was thrown
        verifierDep1.verifyErrorFreeLog();

        // reset the streams
        verifierDep1.resetStreams();
        
        final File phar = new File(new File(verifierDep1.getBasedir()), "target/phar-with-dep1-folders-0.0.1.phar");
        assertTrue(phar.exists());
        
        verifierDep1.setAutoclean(false);
        
        // to zip
        verifierDep1.addCliOption("-Dfrom="+phar.getAbsolutePath());
        verifierDep1.addCliOption("-Dto="+
                phar.getAbsolutePath().substring(0, phar.getAbsolutePath().length() - 4) + "zip");
        
        verifierDep1.executeGoal("org.phpmaven:maven-php-plugin:convert-phar");

        // verify no error was thrown
        verifierDep1.verifyErrorFreeLog();

        // reset the streams
        verifierDep1.resetStreams();
        
        verifierDep1.assertFilePresent("target/phar-with-dep1-folders-0.0.1.zip");
        
        // to jar
        verifierDep1.getCliOptions().clear();
        verifierDep1.addCliOption("-Dfrom="+
                phar.getAbsolutePath().substring(0, phar.getAbsolutePath().length() - 4) + "zip");
        verifierDep1.addCliOption("-Dto="+
                phar.getAbsolutePath().substring(0, phar.getAbsolutePath().length() - 4) + "jar");
        
        verifierDep1.executeGoal("org.phpmaven:maven-php-plugin:convert-phar");

        // verify no error was thrown
        verifierDep1.verifyErrorFreeLog();

        // reset the streams
        verifierDep1.resetStreams();
        
        verifierDep1.assertFilePresent("target/phar-with-dep1-folders-0.0.1.jar");
        
        // to phar
        verifierDep1.getCliOptions().clear();
        verifierDep1.addCliOption("-Dfrom="+
                phar.getAbsolutePath().substring(0, phar.getAbsolutePath().length() - 4) + "jar");
        verifierDep1.addCliOption("-Dto="+
                phar.getAbsolutePath().substring(0, phar.getAbsolutePath().length() - 4) + "2.phar");
        
        verifierDep1.executeGoal("org.phpmaven:maven-php-plugin:convert-phar");

        // verify no error was thrown
        verifierDep1.verifyErrorFreeLog();

        // reset the streams
        verifierDep1.resetStreams();
        
        verifierDep1.assertFilePresent("target/phar-with-dep1-folders-0.0.1.2.phar");

        verifierDep1.getCliOptions().clear();
        verifierDep1.addCliOption("-Dphar=target/phar-with-dep1-folders-0.0.1.2.phar");
        verifierDep1.executeGoal("org.phpmaven:maven-php-plugin:list-phar-files");
        @SuppressWarnings("unchecked")
        final List<String> lines = verifierDep1.loadFile(verifierDep1.getBasedir(), verifierDep1.getLogFileName(), false);
        boolean found1 = false;
        boolean found2 = false;
        for (final String line : lines) {
            if (line.startsWith("[INFO] " + File.separatorChar + "folderA" + File.separatorChar + "MyClassA.php")) {
                found1 = true;
            }
            if (line.startsWith("[INFO] " + File.separatorChar + "folderB" + File.separatorChar + "MyClassB.php")) {
                found2 = true;
            }
        }
        
        // verify no error was thrown
        verifierDep1.verifyErrorFreeLog();

        // reset the streams
        verifierDep1.resetStreams();
        
        assertTrue(found1);
        assertTrue(found2);
    }

}
