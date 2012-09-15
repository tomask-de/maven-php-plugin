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

package org.phpmaven.archetypes.test;

import java.io.File;

import org.apache.maven.it.Verifier;
import org.phpmaven.test.AbstractTestCase;

/**
 * Tests the php5-lib-archetype.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public class LibTest extends AbstractTestCase {

    /**
     * tests the goal "package" after installing the archetype.
     *
     * @throws Exception 
     */
    public void testPackage() throws Exception {
        final Verifier verifier = this.getPhpMavenVerifier("archetypes/lib");
        this.installArchetypes();
        
        verifier.addCliOption("-DarchetypeGroupId=org.phpmaven");
        verifier.addCliOption("-DarchetypeArtifactId=php5-lib-archetype");
        verifier.addCliOption("-DarchetypeVersion=2.0.1");
        verifier.addCliOption("-DgroupId=org.sample");
        verifier.addCliOption("-DartifactId=my-app");
        verifier.addCliOption("-Dversion=0.0.1-SNAPSHOT");
        verifier.addCliOption("-DinteractiveMode=false");
        
        verifier.executeGoal("archetype:generate");
        verifier.verifyErrorFreeLog();
        
        verifier.assertFilePresent("my-app/pom.xml");
        verifier.assertFilePresent("my-app/src/main/php/org/phpmaven/library/LibraryClass.php");
        verifier.assertFilePresent("my-app/src/site/apt/index.apt");
        verifier.assertFilePresent("my-app/src/site/site.xml");
        verifier.assertFilePresent("my-app/src/test/php/org/phpmaven/library/LibraryClassTest.php");
        
        final Verifier verifier2 = this.getVerifierWithoutPrepare("archetypes/lib");
        verifier2.executeGoal("package");
        verifier2.verifyErrorFreeLog();
        verifier2.assertFilePresent("my-app/target/my-app-0.0.1-SNAPSHOT.phar");
    }

}