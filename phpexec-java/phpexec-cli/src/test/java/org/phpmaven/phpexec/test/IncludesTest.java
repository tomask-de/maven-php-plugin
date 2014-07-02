/**
 * Copyright 2010-2012 by PHP-maven.org
 *
 * This file is part of phpexec-java.
 *
 * phpexec-java is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * phpexec-java is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with phpexec-java.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.phpmaven.phpexec.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.phpmaven.phpexec.cli.PhpExecutableConfiguration;
import org.phpmaven.phpexec.library.IPhpExecutable;
import org.phpmaven.phpexec.library.IPhpExecutableConfiguration;
import org.phpmaven.phpexec.library.PhpErrorException;
import org.phpmaven.phpexec.library.PhpWarningException;

/**
 * test cases for PHP support.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public class IncludesTest {

    /**
     * Tests if the execution configuration can be created.
     *
     * @throws Exception thrown on errors
     */
	@Test
    public void testExisting() throws Exception {
		final IPhpExecutableConfiguration execConfig = new PhpExecutableConfiguration();

        final File includeTestPhp = new File("target/test-classes/org/phpmaven/phpexec/test/empty-pom/includes-test.php");
        execConfig.getIncludePath().add(
                new File("target/test-classes/org/phpmaven/phpexec/test/empty-pom/includes").getAbsolutePath());

        // assert that the environment variable is mapped correctly
        final IPhpExecutable exec = execConfig.getPhpExecutable();
        Assert.assertEquals("SUCCESS_EXISTING\n", exec.execute(includeTestPhp));
    }

    /**
     * Tests if the execution configuration can be created.
     *
     * @throws Exception thrown on errors
     */
	@Test
    public void testExistingPut() throws Exception {
		final IPhpExecutableConfiguration execConfig = new PhpExecutableConfiguration();

        final File includeTestPhp = new File("target/test-classes/org/phpmaven/phpexec/test/empty-pom/includes-test.php");
        final List<String> includes = new ArrayList<String>();
        includes.add(
                new File("target/test-classes/org/phpmaven/phpexec/test/empty-pom/includes").getAbsolutePath());
        execConfig.setIncludePath(includes);

        // assert that the environment variable is mapped correctly
        final IPhpExecutable exec = execConfig.getPhpExecutable();
        Assert.assertEquals("SUCCESS_EXISTING\n", exec.execute(includeTestPhp));
    }

    /**
     * Tests if the execution configuration can be created.
     *
     * @throws Exception thrown on errors
     */
	@Test
    public void testFailing() throws Exception {
		final IPhpExecutableConfiguration execConfig = new PhpExecutableConfiguration();

        final File includeTestPhp = new File("target/test-classes/org/phpmaven/phpexec/test/empty-pom/includes-test.php");

        // assert that the environment variable is mapped correctly
        final IPhpExecutable exec = execConfig.getPhpExecutable();
        try {
            // we will either expect a php warning or a php error.
            // depends on php.ini and php version.
            exec.execute(includeTestPhp);
            Assert.fail("Exception expected");
        } catch (PhpWarningException ex) {
            // ignore; we expect this exception
            Assert.assertTrue(ex.getMessage().contains("Warning: require_once(existing.php)"));
        } catch (PhpErrorException ex) {
            // ignore; we expect this exception
            Assert.assertTrue(ex.getMessage().contains("Fatal error: require_once()"));
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