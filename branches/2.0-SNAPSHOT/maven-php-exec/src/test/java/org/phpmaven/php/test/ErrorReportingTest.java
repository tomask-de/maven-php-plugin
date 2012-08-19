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

package org.phpmaven.php.test;

import java.io.File;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.monitor.logging.DefaultLog;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.exec.IPhpExecutable;
import org.phpmaven.exec.IPhpExecutableConfiguration;
import org.phpmaven.test.AbstractTestCase;

/**
 * test cases for PHP error reporting support.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public class ErrorReportingTest extends AbstractTestCase {

    /**
     * Tests if the execution is aware of detecting error while setting error_reporting in the script.
     *
     * @throws Exception thrown on errors
     */
    public void testFalse() throws Exception {
        // look up the component factory
        final IComponentFactory factory = lookup(IComponentFactory.class);
        // create the execution config
        final MavenSession session = this.createSimpleSession("exec/error-reporting");
        final IPhpExecutableConfiguration execConfig = factory.lookup(
                IPhpExecutableConfiguration.class,
                IComponentFactory.EMPTY_CONFIG,
                session);

        final File defineTestPhp = new File(session.getCurrentProject().getBasedir(), "false-test.php");

        // assert that the execution is aware of detecting the error
        final IPhpExecutable exec = execConfig.getPhpExecutable(new DefaultLog(new ConsoleLogger()));
        assertTrue(exec.execute(defineTestPhp).contains("some deprecated warning"));
    }

    /**
     * Tests if the execution is aware of detecting error.
     *
     * @throws Exception thrown on errors
     */
    public void testEALL() throws Exception {
        // look up the component factory
        final IComponentFactory factory = lookup(IComponentFactory.class);
        // create the execution config
        final MavenSession session = this.createSimpleSession("exec/error-reporting");
        final IPhpExecutableConfiguration execConfig = factory.lookup(
                IPhpExecutableConfiguration.class,
                IComponentFactory.EMPTY_CONFIG,
                session);
        execConfig.setErrorReporting("E_ALL");

        final File defineTestPhp = new File(session.getCurrentProject().getBasedir(), "test.php");

        // assert that the execution is aware of detecting the error
        final IPhpExecutable exec = execConfig.getPhpExecutable(new DefaultLog(new ConsoleLogger()));
        assertTrue(exec.execute(defineTestPhp).contains("some deprecated warning"));
    }

    /**
     * Tests if the execution is aware of detecting error.
     *
     * @throws Exception thrown on errors
     */
    public void testEALLandNotEUSERDEPRECATED() throws Exception {
        // look up the component factory
        final IComponentFactory factory = lookup(IComponentFactory.class);
        // create the execution config
        final MavenSession session = this.createSimpleSession("exec/error-reporting");
        final IPhpExecutableConfiguration execConfig = factory.lookup(
                IPhpExecutableConfiguration.class,
                IComponentFactory.EMPTY_CONFIG,
                session);
        execConfig.setErrorReporting("E_ALL & !E_USER_DEPRECATED");

        final File defineTestPhp = new File(session.getCurrentProject().getBasedir(), "test.php");

        // assert that the execution is aware of detecting the error
        final IPhpExecutable exec = execConfig.getPhpExecutable(new DefaultLog(new ConsoleLogger()));
        assertEquals("", exec.execute(defineTestPhp));
    }

    /**
     * Tests if the execution is aware of detecting error.
     *
     * @throws Exception thrown on errors
     */
    public void testNULL() throws Exception {
        // look up the component factory
        final IComponentFactory factory = lookup(IComponentFactory.class);
        // create the execution config
        final MavenSession session = this.createSimpleSession("exec/error-reporting");
        final IPhpExecutableConfiguration execConfig = factory.lookup(
                IPhpExecutableConfiguration.class,
                IComponentFactory.EMPTY_CONFIG,
                session);
        execConfig.setErrorReporting("0");

        final File defineTestPhp = new File(session.getCurrentProject().getBasedir(), "test.php");

        // assert that the execution is aware of detecting the error
        final IPhpExecutable exec = execConfig.getPhpExecutable(new DefaultLog(new ConsoleLogger()));
        assertEquals("", exec.execute(defineTestPhp));
    }
    
}