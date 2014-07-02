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
import java.util.HashMap;
import java.util.Map;

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
public class EnvTest {

    /**
     * Tests if the execution configuration can be created.
     *
     * @throws Exception thrown on errors
     */
	@Test
    public void testEnvVar() throws Exception {
		final IPhpExecutableConfiguration execConfig = new PhpExecutableConfiguration();

        final File envTestPhp = new File("target/test-classes/org/phpmaven/phpexec/test/empty-pom/env-test.php");
        execConfig.getEnv().put("JUNIT_ENV_TEST", "foo bar");

        // assert that the environment variable is mapped correctly
        final IPhpExecutable exec = execConfig.getPhpExecutable();
        Assert.assertEquals("success: foo bar\n", exec.execute(envTestPhp));
    }

    /**
     * Tests if the execution configuration can be created.
     *
     * @throws Exception thrown on errors
     */
	@Test
    public void testEnvVarSetter() throws Exception {
		final IPhpExecutableConfiguration execConfig = new PhpExecutableConfiguration();

        final File envTestPhp = new File("target/test-classes/org/phpmaven/phpexec/test/empty-pom/env-test.php");
        final Map<String, String> env = new HashMap<String, String>();
        env.put("JUNIT_ENV_TEST", "foo bar");
        execConfig.setEnv(env);

        // assert that the environment variable is mapped correctly
        final IPhpExecutable exec = execConfig.getPhpExecutable();
        Assert.assertEquals("success: foo bar\n", exec.execute(envTestPhp));
    }
    
}