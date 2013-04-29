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

import org.apache.maven.it.Verifier;
import org.phpmaven.test.it.AbstractTestCase;

/**
 * Testcase for php-maven mojos being present.
 * 
 * Tests: http://maven.apache.org/plugin-developers/plugin-testing.html
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.1
 */
public class PhpUnitMultipomTest extends AbstractTestCase {

    /**
     * tests the goal "package" with multipom referring each other.
     *
     * @throws Exception 
     */
    public void testPackage() throws Exception {
        final Verifier verifierDep1 = this.getPhpMavenVerifier("mojos-phpunit/test-multipom");
        
        // delete the pom from previous runs
        verifierDep1.deleteArtifact("org.sample.my-php-lib", "my-php-module-one", "1.0-SNAPSHOT", "pom");
        verifierDep1.deleteArtifact("org.sample.my-php-lib", "my-php-module-two", "1.0-SNAPSHOT", "pom");
        verifierDep1.deleteArtifact("org.sample.my-php-lib", "my-php-module-one", "1.0-SNAPSHOT", "phar");
        verifierDep1.deleteArtifact("org.sample.my-php-lib", "my-php-module-two", "1.0-SNAPSHOT", "phar");

        // execute testing
        verifierDep1.executeGoal("package");

        // verify no error was thrown
        verifierDep1.verifyErrorFreeLog();

        // reset the streams
        verifierDep1.resetStreams();
    }


}
