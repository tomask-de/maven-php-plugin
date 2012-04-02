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
import org.phpmaven.exec.PhpErrorException;
import org.phpmaven.exec.PhpWarningException;
import org.phpmaven.test.AbstractTestCase;

/**
 * test cases for PHP support.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public class IncludesTest extends AbstractTestCase {

    /**
     * Tests if the execution configuration can be created.
     *
     * @throws Exception thrown on errors
     */
    public void testExisting() throws Exception {
        // look up the component factory
        final IComponentFactory factory = lookup(IComponentFactory.class);
        // create the execution config
        final MavenSession session = this.createSimpleSession("php/empty-pom");
        final IPhpExecutableConfiguration execConfig = factory.lookup(
                IPhpExecutableConfiguration.class,
                IComponentFactory.EMPTY_CONFIG,
                session);

        final File includeTestPhp = new File(session.getCurrentProject().getBasedir(), "includes-test.php");
        execConfig.getIncludePath().add(
                new File(session.getCurrentProject().getBasedir(), "includes").getAbsolutePath());

        // assert that the environment variable is mapped correctly
        final IPhpExecutable exec = execConfig.getPhpExecutable(new DefaultLog(new ConsoleLogger()));
        assertEquals("SUCCESS_EXISTING\n", exec.execute(includeTestPhp));
    }

    /**
     * Tests if the execution configuration can be created.
     *
     * @throws Exception thrown on errors
     */
    public void testFailing() throws Exception {
        // look up the component factory
        final IComponentFactory factory = lookup(IComponentFactory.class);
        // create the execution config
        final MavenSession session = this.createSimpleSession("php/empty-pom");
        final IPhpExecutableConfiguration execConfig = factory.lookup(
                IPhpExecutableConfiguration.class,
                IComponentFactory.EMPTY_CONFIG,
                session);

        final File includeTestPhp = new File(session.getCurrentProject().getBasedir(), "includes-test.php");

        // assert that the environment variable is mapped correctly
        final IPhpExecutable exec = execConfig.getPhpExecutable(new DefaultLog(new ConsoleLogger()));
        try {
            // we will either expect a php warning or a php error.
            // depends on php.ini and php version.
            exec.execute(includeTestPhp);
            fail("Exception expected");
        } catch (PhpWarningException ex) {
            // ignore; we expect this exception
            assertTrue(ex.getMessage().contains("Warning: require_once(existing.php)"));
        } catch (PhpErrorException ex) {
            // ignore; we expect this exception
            assertTrue(ex.getMessage().contains("Fatal error: require_once()"));
        }
    }
//
//    /**
//     * Tests if the execution configuration can be created.
//     *
//     * @throws Exception thrown on errors
//     */
//    public void testFailingIgnored() throws Exception {
//        // look up the component factory
//        final IComponentFactory factory = lookup(IComponentFactory.class);
//        // create the execution config
//        final MavenSession session = this.createSession("empty-pom");
//        final IPhpExecutableConfiguration execConfig = factory.lookup(
//                IPhpExecutableConfiguration.class,
//                IComponentFactory.EMPTY_CONFIG,
//                session);
//
//        final File includeTestPhp = new File(session.getCurrentProject().getBasedir(), "includes-test.php");
//        execConfig.setIgnoreIncludeErrors(true);
//
//        // assert that the environment variable is mapped correctly
//        final IPhpExecutable exec = execConfig.getPhpExecutable(new DefaultLog(new ConsoleLogger()));
//        // TODO exec.execute(includeTestPhp);
//        // TODO currently does not work because the php.exe returns non-zero error code at cli.
//        // there should be no exception thrown.
//    }
    
}