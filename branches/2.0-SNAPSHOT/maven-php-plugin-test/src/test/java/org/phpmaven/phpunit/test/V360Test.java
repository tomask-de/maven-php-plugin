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

package org.phpmaven.phpunit.test;

import java.io.File;
import java.util.Iterator;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.it.Verifier;
import org.apache.maven.monitor.logging.DefaultLog;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.phpunit.IPhpunitConfiguration;
import org.phpmaven.phpunit.IPhpunitResult;
import org.phpmaven.phpunit.IPhpunitSupport;
import org.phpmaven.phpunit.IPhpunitTestRequest;
import org.phpmaven.phpunit.IPhpunitTestResult;

/**
 * test cases for PHPUNIT support.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public class V360Test extends AbstractVersionTestCase {

    /**
     * The phpunit version.
     */
    private static final String PHPUNIT_VERSION = "3.6.0";
    
    /**
     * The packages.
     */
    private static final Pkg[] PACKAGES = new Pkg[]{
        new Pkg("de.phpunit", "PHPUnit", PHPUNIT_VERSION),
        new Pkg("de.phpunit", "File_Iterator", "1.3.0"),
        new Pkg("de.phpunit", "Text_Template", "1.1.1"),
        new Pkg("de.phpunit", "PHP_CodeCoverage", "1.1.0"),
        new Pkg("de.phpunit", "PHP_TokenStream", "1.1.0"),
        new Pkg("de.phpunit", "PHP_Timer", "1.0.1"),
        new Pkg("de.phpunit", "PHPUnit_MockObject", "1.1.0"),
        new Pkg("de.phpunit", "PHP_Invoker", "1.1.0"),
        new Pkg("com.symfony-project", "YAML", "1.0.2")
    };

    /**
     * Tests if the phpunit support can be created.
     *
     * @throws Exception thrown on errors
     */
    public void testPhpunitOrgCreation() throws Exception {
        // look up the component factory
        final IComponentFactory factory = lookup(IComponentFactory.class);
        // create the execution config
        final MavenSession session = this.createSimpleSession("phpunit/pom-360");
        final IPhpunitConfiguration config = factory.lookup(
                IPhpunitConfiguration.class,
                IComponentFactory.EMPTY_CONFIG,
                session);
        final Verifier verifier = this.getVerifier("phpunit/pom-360");
        final IPhpunitSupport phpunit = config.getPhpunitSupport(PHPUNIT_VERSION);
        
        this.prepareMaven(verifier, session, PACKAGES);
        
        final IPhpunitTestRequest request = factory.lookup(
                IPhpunitTestRequest.class,
                IComponentFactory.EMPTY_CONFIG,
                session);
        final File testFile = new File(
                session.getCurrentProject().getBasedir(),
                "test-classes/FooTest.php");
        request.addTestFile(testFile);
        
        final DefaultLog logger = new DefaultLog(new ConsoleLogger());
        phpunit.setResultFolder(new File(session.getCurrentProject().getBasedir(), "target/phpunit"));
        final IPhpunitTestResult testResult = phpunit.executeTests(request, logger);
        
        assertNotNull(testResult);
        if (!testResult.isSuccess()) {
            fail(testResult.toString());
        }
        final Iterator<IPhpunitResult> results = testResult.getResults().iterator();
        assertTrue(results.hasNext());
        final IPhpunitResult result = results.next();
        assertNotNull(result);
        assertFalse(results.hasNext());
        assertEquals(testFile.getAbsolutePath(), result.getFileToTest().getAbsolutePath());
        assertEquals(IPhpunitResult.ResultType.SUCCESS, result.getResultType());
        assertEquals(1, result.getTests());
        assertEquals(0, result.getErrors());
        assertEquals(0, result.getFailures());
    }

}