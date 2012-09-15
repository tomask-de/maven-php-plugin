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

package org.phpmaven.core.test;

import junit.framework.TestCase;

import org.phpmaven.core.ExecutionUtils;


/**
 * Test case for the isWindows method.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.1
 */
public class IsWindowsTest extends TestCase {

    /**
     * Tests isWindows.
     * @throws Exception thrown on errors
     */
    public void testIsWindows() throws Exception {
        final String oldOs = System.getProperty("os.name");
        try {
            System.setProperty("os.name", "Windows");
            assertTrue(ExecutionUtils.isWindows());
        } finally {
            System.setProperty("os.name", oldOs);
        }
    }

    /**
     * Tests isNotWindows.
     * @throws Exception thrown on errors
     */
    public void testIsNotWindows() throws Exception {
        final String oldOs = System.getProperty("os.name");
        try {
            System.setProperty("os.name", "Unix/Linux");
            assertFalse(ExecutionUtils.isWindows());
        } finally {
            System.setProperty("os.name", oldOs);
        }
    }

}
