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

package org.phpmaven.phpexec.library.test;

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;
import org.phpmaven.phpexec.library.PhpCoreException;
import org.phpmaven.phpexec.library.PhpExecutionException;

/**
 * test cases for PHP support.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 0.1.8
 */
public class Exception2Test {

    /**
     * Tests some things on php execution exception.
     *
     * @throws Exception thrown on errors
     */
	@Test
    public void testExecException() throws Exception {
        PhpExecutionException ex = new PhpExecutionException(null, "FOO");
        Assert.assertEquals("\nFOO", ex.getMessage());
        final File fooFile = new File("foo.php");
        ex = new PhpExecutionException(fooFile, "FOO");
        Assert.assertTrue(ex.getMessage().contains("FOO"));
        Assert.assertTrue(ex.getMessage().contains(fooFile.getAbsolutePath()));
    }
    
    /**
     * Tests some things on php core exception.
     *
     * @throws Exception thrown on errors
     */
	@Test
    public void testCoreException() throws Exception {
        // constructors for code coverage
        PhpCoreException ex = new PhpCoreException();
        ex = new PhpCoreException("FOO", new Exception());
        ex = new PhpCoreException(new Exception());

        ex = new PhpCoreException("some meaningful error");
        Assert.assertNull(ex.getAppendedOutput());
        Assert.assertEquals("some meaningful error", ex.getMessage());
        ex.appendOutput("FOOBAR");
        Assert.assertEquals("FOOBAR", ex.getAppendedOutput());
        Assert.assertTrue(ex.getMessage().contains("FOOBAR"));
        Assert.assertTrue(ex.getMessage().contains("some meaningful error"));
        ex.appendOutput("BAZ");
        Assert.assertEquals("BAZ", ex.getAppendedOutput());
        Assert.assertFalse(ex.getMessage().contains("FOOBAR"));
        Assert.assertTrue(ex.getMessage().contains("BAZ"));
        Assert.assertTrue(ex.getMessage().contains("some meaningful error"));
    }

}