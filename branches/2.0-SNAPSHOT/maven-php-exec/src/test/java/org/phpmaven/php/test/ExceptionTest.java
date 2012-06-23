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

import org.phpmaven.exec.PhpCoreException;
import org.phpmaven.exec.PhpExecutionException;
import org.phpmaven.test.AbstractTestCase;

/**
 * test cases for PHP support.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public class ExceptionTest extends AbstractTestCase {

    /**
     * Tests some things on php execution exception.
     *
     * @throws Exception thrown on errors
     */
    public void testExecException() throws Exception {
        PhpExecutionException ex = new PhpExecutionException(null, "FOO");
        assertEquals("\nFOO", ex.getMessage());
        final File fooFile = new File("foo.php");
        ex = new PhpExecutionException(fooFile, "FOO");
        assertTrue(ex.getMessage().contains("FOO"));
        assertTrue(ex.getMessage().contains(fooFile.getAbsolutePath()));
    }
    
    /**
     * Tests some things on php core exception.
     *
     * @throws Exception thrown on errors
     */
    public void testCoreException() throws Exception {
        // constructors for code coverage
        PhpCoreException ex = new PhpCoreException();
        ex = new PhpCoreException("FOO", new Exception());
        ex = new PhpCoreException(new Exception());

        ex = new PhpCoreException("some meaningful error");
        assertNull(ex.getAppendedOutput());
        assertEquals("some meaningful error", ex.getMessage());
        ex.appendOutput("FOOBAR");
        assertEquals("FOOBAR", ex.getAppendedOutput());
        assertTrue(ex.getMessage().contains("FOOBAR"));
        assertTrue(ex.getMessage().contains("some meaningful error"));
        ex.appendOutput("BAZ");
        assertEquals("BAZ", ex.getAppendedOutput());
        assertFalse(ex.getMessage().contains("FOOBAR"));
        assertTrue(ex.getMessage().contains("BAZ"));
        assertTrue(ex.getMessage().contains("some meaningful error"));
    }

}