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

import junit.framework.Assert;

import org.junit.Test;
import org.phpmaven.phpexec.cli.PhpExecutableConfiguration;
import org.phpmaven.phpexec.library.IPhpExecutable;
import org.phpmaven.phpexec.library.IPhpExecutableConfiguration;
import org.phpmaven.phpexec.library.PhpException;

/**
 * test cases for PHP support.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public class BaseTest {

    /**
     * Tests if the execution configuration can be created.
     *
     * @throws Exception thrown on errors
     */
	@Test
    public void testECCreation() throws Exception {
		final IPhpExecutableConfiguration execConfig = new PhpExecutableConfiguration();
        // assert that it is not null
		Assert.assertNotNull(execConfig);
        // assert that we are able to create the executable
        final IPhpExecutable exec = execConfig.getPhpExecutable();
        Assert.assertNotNull(exec.getStrVersion());
        Assert.assertNotNull(exec.getVersion());
    }

    /**
     * Tests if the execution configuration can be created
     * with an unknown executable set.
     *
     * @throws Exception thrown on errors
     */
	@Test
    public void testUnknownExecutable() throws Exception {
		final IPhpExecutableConfiguration execConfig = new PhpExecutableConfiguration();
        execConfig.setExecutable("/foo/bar/php");
        // assert that we are able to create the executable
        final IPhpExecutable exec = execConfig.getPhpExecutable();
        try {
            exec.getStrVersion();
            Assert.fail("Exception expected");
        // CHECKSTYLE:OFF
        // checkstyle does not like empty catches
        } catch (PhpException ex) {
            // ignore; we expect this exception
        }
        // CHECKSTYLE:ON
    }

    /**
     * Tests if isUseCache is active per default.
     * @throws Exception
     */
	@Test
    public void testIsUseCacheActive() throws Exception {
		final IPhpExecutableConfiguration execConfig = new PhpExecutableConfiguration();
        Assert.assertTrue(execConfig.isUseCache());
        execConfig.setUseCache(false);
        Assert.assertFalse(execConfig.isUseCache());
    }
    
    // TODO: test additionalPhpParameters
    // TODO: test flushPHPOutput
    // TODO: test temporaryScriptFile

}