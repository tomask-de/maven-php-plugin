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

package org.phpmaven.project.legacy.test;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.monitor.logging.DefaultLog;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.exec.IPhpExecutableConfiguration;
import org.phpmaven.project.IProjectPhpExecution;
import org.phpmaven.test.AbstractTestCase;

/**
 * test cases for PHP legacy project support.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public class BaseTest extends AbstractTestCase {

    /**
     * Tests if the execution configuration can be created.
     *
     * @throws Exception thrown on errors
     */
    public void testECCreation() throws Exception {
        // look up the component factory
        final IComponentFactory factory = lookup(IComponentFactory.class);
        // create the execution config
        final MavenSession session = this.createSessionForPhpMaven(
                "project/legacy/small-project");
        final IProjectPhpExecution prjConfig = factory.lookup(
                IProjectPhpExecution.class,
                IComponentFactory.EMPTY_CONFIG,
                session);
        // assert that it is not null
        assertNotNull(prjConfig);
        
        final String targetPhpDeps = new File(session.getCurrentProject().getBasedir(),
                "target/php-deps").getAbsolutePath();
        final String targetPhpTestDeps = new File(session.getCurrentProject().getBasedir(),
                "target/php-test-deps").getAbsolutePath();
        final String targetPhpDependencies = new File(session.getCurrentProject().getBasedir(),
                "target/php-dependencies").getAbsolutePath();
        final String targetPhpTestDependencies = new File(session.getCurrentProject().getBasedir(),
                "target/php-test-dependencies").getAbsolutePath();

        // test the defaults
        assertEquals(
                targetPhpDeps,
                prjConfig.getDepsDir().getAbsolutePath());
        assertEquals(
                targetPhpTestDeps,
                prjConfig.getTestDepsDir().getAbsolutePath());
        assertEquals(
                targetPhpDeps,
                prjConfig.getDepsDir(session.getCurrentProject()).getAbsolutePath());
        assertEquals(
                targetPhpTestDeps,
                prjConfig.getTestDepsDir(session.getCurrentProject()).getAbsolutePath());
        
        // test to overwrite via config
        final Xpp3Dom config = new Xpp3Dom("configuration");
        final Xpp3Dom deps = new Xpp3Dom("dependenciesDir");
        deps.setValue("${project.basedir}/target/php-dependencies");
        config.addChild(deps);
        final Xpp3Dom testDeps = new Xpp3Dom("testDependenciesDir");
        testDeps.setValue("${project.basedir}/target/php-test-dependencies");
        config.addChild(testDeps);
        assertEquals(
                targetPhpDependencies,
                prjConfig.getDepsDir(config).getAbsolutePath());
        assertEquals(
                targetPhpTestDependencies,
                prjConfig.getTestDepsDir(config).getAbsolutePath());
        assertEquals(
                targetPhpDependencies,
                prjConfig.getDepsDir(config, session.getCurrentProject()).getAbsolutePath());
        assertEquals(
                targetPhpTestDependencies,
                prjConfig.getTestDepsDir(config, session.getCurrentProject()).getAbsolutePath());
        
        // test for the correct include path (normal include path)
        IPhpExecutableConfiguration execConfig = prjConfig.getExecutionConfiguration();
        Set<String> includes = new HashSet<String>(execConfig.getIncludePath());
        assertTrue(includes.contains(targetPhpDeps));
        execConfig = prjConfig.getExecutionConfiguration(session.getCurrentProject());
        includes = new HashSet<String>(execConfig.getIncludePath());
        assertTrue(includes.contains(targetPhpDeps));
        execConfig = prjConfig.getExecutionConfiguration(config);
        includes = new HashSet<String>(execConfig.getIncludePath());
        assertTrue(includes.contains(targetPhpDependencies));
        execConfig = prjConfig.getExecutionConfiguration(config, session.getCurrentProject());
        includes = new HashSet<String>(execConfig.getIncludePath());
        assertTrue(includes.contains(targetPhpDependencies));
        
        // test for the correct include path (test include path)
        execConfig = prjConfig.getTestExecutionConfiguration();
        includes = new HashSet<String>(execConfig.getIncludePath());
        assertTrue(includes.contains(targetPhpDeps));
        assertTrue(includes.contains(targetPhpTestDeps));
        execConfig = prjConfig.getTestExecutionConfiguration(session.getCurrentProject());
        includes = new HashSet<String>(execConfig.getIncludePath());
        assertTrue(includes.contains(targetPhpDeps));
        assertTrue(includes.contains(targetPhpTestDeps));
        execConfig = prjConfig.getTestExecutionConfiguration(config);
        includes = new HashSet<String>(execConfig.getIncludePath());
        assertTrue(includes.contains(targetPhpDependencies));
        assertTrue(includes.contains(targetPhpTestDependencies));
        execConfig = prjConfig.getTestExecutionConfiguration(config, session.getCurrentProject());
        includes = new HashSet<String>(execConfig.getIncludePath());
        assertTrue(includes.contains(targetPhpDependencies));
        assertTrue(includes.contains(targetPhpTestDependencies));
    }

    /**
     * Tests an additional include path scenario.
     *
     * @throws Exception thrown on errors
     */
    public void testIncludePathScenario() throws Exception {
        // look up the component factory
        final IComponentFactory factory = lookup(IComponentFactory.class);
        // create the execution config
        final MavenSession session = this.createSessionForPhpMaven(
                "project/legacy/include-project");
        final IProjectPhpExecution prjConfig = factory.lookup(
                IProjectPhpExecution.class,
                IComponentFactory.EMPTY_CONFIG,
                session);
        
        final DefaultLog logger = new DefaultLog(new ConsoleLogger());
        final File testPhp = new File(session.getCurrentProject().getBasedir(), "test.php");
        final File test2Php = new File(session.getCurrentProject().getBasedir(), "test2.php");
        
        final String result = prjConfig.getExecutionConfiguration().getPhpExecutable(logger).execute(testPhp);
        assertEquals("foobar", result.trim());
        
        final String resultTest = prjConfig.getTestExecutionConfiguration().getPhpExecutable(logger).execute(testPhp);
        assertEquals("foobar", resultTest.trim());
        
        final String result2 = prjConfig.getTestExecutionConfiguration().getPhpExecutable(logger).execute(test2Php);
        assertEquals("testFoobar", result2.trim());
    }
   
}