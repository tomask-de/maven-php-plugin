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
import org.phpmaven.phpexec.library.PhpWarningException;

/**
 * test cases for detecting php warnings as warnings.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public class WarningsTest {

    /**
     * Tests if the execution is aware of detecting warnings.
     *
     * @throws Exception thrown on errors
     */
	@Test
    public void testFalse() throws Exception {
		final IPhpExecutableConfiguration execConfig = new PhpExecutableConfiguration();

        final IPhpExecutable exec = execConfig.getPhpExecutable();
        try {
        	exec.executeCode("", "echo \"Warning: Some Warning\n\";");
        	Assert.fail("Expected exception not thrown");
        }
        catch (PhpWarningException ex) {
        	// expected exception
        }
    }
    
}