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
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.execution.MavenSession;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.exec.IPhpExecutableConfiguration;
import org.phpmaven.phpexec.library.IPhpExecutable;
import org.phpmaven.test.AbstractTestCase;

/**
 * test cases for PHP support.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public class DefineTest extends AbstractTestCase {

    /**
     * Tests if the execution configuration can be created.
     *
     * @throws Exception thrown on errors
     */
    public void testDefines() throws Exception {
        // look up the component factory
        final IComponentFactory factory = lookup(IComponentFactory.class);
        // create the execution config
        final MavenSession session = this.createSimpleSession("exec/empty-pom");
        final IPhpExecutableConfiguration execConfig = factory.lookup(
                IPhpExecutableConfiguration.class,
                IComponentFactory.EMPTY_CONFIG,
                session);

        final File defineTestPhp = new File(session.getCurrentProject().getBasedir(), "define-test.php");
        execConfig.getPhpDefines().put("max_execution_time", "foo bar");

        // assert that the environment variable is mapped correctly
        final IPhpExecutable exec = execConfig.getPhpExecutable();
        assertEquals("success: foo bar\n", exec.execute(defineTestPhp));
    }

    /**
     * Tests if the execution configuration can be created.
     *
     * @throws Exception thrown on errors
     */
    public void testDefinesSet() throws Exception {
        // look up the component factory
        final IComponentFactory factory = lookup(IComponentFactory.class);
        // create the execution config
        final MavenSession session = this.createSimpleSession("exec/empty-pom");
        final IPhpExecutableConfiguration execConfig = factory.lookup(
                IPhpExecutableConfiguration.class,
                IComponentFactory.EMPTY_CONFIG,
                session);

        final File defineTestPhp = new File(session.getCurrentProject().getBasedir(), "define-test.php");
        final Map<String, String> defines = new HashMap<String, String>();
        defines.put("max_execution_time", "foo bar");
        execConfig.setPhpDefines(defines);

        // assert that the environment variable is mapped correctly
        final IPhpExecutable exec = execConfig.getPhpExecutable();
        assertEquals("success: foo bar\n", exec.execute(defineTestPhp));
    }
    
}