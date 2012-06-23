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

package org.phpmaven.lint.test;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.monitor.logging.DefaultLog;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.lint.ILintChecker;
import org.phpmaven.lint.ILintExecution;
import org.phpmaven.test.AbstractTestCase;

/**
 * Base test cases for the line check.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public class BaseTest extends AbstractTestCase {

    /**
     * Tests if the checker can be created.
     *
     * @throws Exception thrown on errors
     */
    public void testCFCreation() throws Exception {
        final MavenSession session = this.createSimpleSession("lint/empty-pom");
        // look up the component factory
        final IComponentFactory factory = lookup(IComponentFactory.class);
        assertNotNull(factory);
        final ILintChecker checker = factory.lookup(ILintChecker.class, IComponentFactory.EMPTY_CONFIG, session);
        assertNotNull(checker);
    }

    /**
     * Tests if validation is ok.
     *
     * @throws Exception thrown on errors
     */
    public void testValidationOk() throws Exception {
        final MavenSession session = this.createSimpleSession("lint/empty-pom");
        final IComponentFactory factory = lookup(IComponentFactory.class);
        final ILintChecker checker = factory.lookup(ILintChecker.class, IComponentFactory.EMPTY_CONFIG, session);
        
        final DefaultLog logger = new DefaultLog(new ConsoleLogger());
        checker.addFileToCheck(new File(session.getCurrentProject().getBasedir(), "success.php"));
        final Iterable<ILintExecution> result = checker.run(logger);
        assertFalse(result.iterator().hasNext());
    }

    /**
     * Tests if validation is failing on parse errors.
     *
     * @throws Exception thrown on errors
     */
    public void testValidationFailed() throws Exception {
        final MavenSession session = this.createSimpleSession("lint/empty-pom");
        final IComponentFactory factory = lookup(IComponentFactory.class);
        final ILintChecker checker = factory.lookup(ILintChecker.class, IComponentFactory.EMPTY_CONFIG, session);
        
        final DefaultLog logger = new DefaultLog(new ConsoleLogger());
        final File failedFile = new File(session.getCurrentProject().getBasedir(), "failed.php");
        checker.addFileToCheck(failedFile);
        final Iterable<ILintExecution> result = checker.run(logger);
        final Iterator<ILintExecution> iter = result.iterator();
        assertTrue(iter.hasNext());
        final ILintExecution failure = iter.next();
        assertFalse(iter.hasNext());
        assertEquals(failedFile, failure.getFile());
        assertNotNull(failure.getException());
    }

    /**
     * Tests if validation is failing with succeeding files and parse errors.
     *
     * @throws Exception thrown on errors
     */
    public void testMultiFailure() throws Exception {
        final MavenSession session = this.createSimpleSession("lint/empty-pom");
        final IComponentFactory factory = lookup(IComponentFactory.class);
        final ILintChecker checker = factory.lookup(ILintChecker.class, IComponentFactory.EMPTY_CONFIG, session);
        
        final DefaultLog logger = new DefaultLog(new ConsoleLogger());
        final File failedFile = new File(session.getCurrentProject().getBasedir(), "multiple/failed.php");
        final File failed2File = new File(session.getCurrentProject().getBasedir(), "multiple/failed2.php");
        checker.addFileToCheck(new File(session.getCurrentProject().getBasedir(), "multiple/success.php"));
        checker.addFileToCheck(failedFile);
        checker.addFileToCheck(failed2File);
        final Iterable<ILintExecution> result = checker.run(logger);
        final Map<File, ILintExecution> failures = new HashMap<File, ILintExecution>();
        for (final ILintExecution failure : result) {
            failures.put(failure.getFile(), failure);
        }
        assertEquals(2, failures.size());
        assertNotNull(failures.get(failedFile));
        assertNotNull(failures.get(failed2File));
    }

}