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

import junit.framework.Assert;

import org.junit.Test;
import org.phpmaven.phpexec.cli.PhpExecutableConfiguration;
import org.phpmaven.phpexec.library.IPhpExecutable;
import org.phpmaven.phpexec.library.IPhpExecutableConfiguration;

/**
 * test cases for PHP support.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public class CacheTest {

    /**
     * Tests if the execution configuration can be created.
     *
     * @throws Exception thrown on errors
     */
	@Test
    public void testDefines() throws Exception {
		final IPhpExecutableConfiguration execConfig = new PhpExecutableConfiguration();

        final File defineTestPhp = new File("target/test-classes/org/phpmaven/phpexec/test/empty-pom/define-test.php");
        execConfig.getPhpDefines().put("max_execution_time", "foo bar");

        // assert that the environment variable is mapped correctly
        final IPhpExecutable exec = execConfig.getPhpExecutable();
        Assert.assertEquals("success: foo bar\n", exec.execute(defineTestPhp));
    }
    
}