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

package org.phpmaven.phar.test;

import java.io.File;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.monitor.logging.DefaultLog;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.exec.IPhpExecutableConfiguration;
import org.phpmaven.phar.IPharPackager;
import org.phpmaven.phar.IPharPackagerConfiguration;
import org.phpmaven.phar.IPharPackagingRequest;
import org.phpmaven.phpexec.library.IPhpExecutable;
import org.phpmaven.test.AbstractTestCase;

/**
 * test cases for the PHAR packager supporting alias.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public class AliasTest extends AbstractTestCase {

    /**
     * Tests the alias support
     *
     * @throws Exception thrown on errors
     */
    public void testAlias() throws Exception {
        // look up the component factory
        final IComponentFactory factory = lookup(IComponentFactory.class);
        // create the execution config
        final MavenSession session = this.createSimpleSession("phar/large-phar");
        final File pharFile = new File(session.getCurrentProject().getBasedir(), "phar1.phar");
        delete(pharFile);
        final IPharPackagerConfiguration pharConfig = factory.lookup(
                IPharPackagerConfiguration.class,
                IComponentFactory.EMPTY_CONFIG,
                session);
        final IPharPackager exec = pharConfig.getPharPackager();
        final IPharPackagingRequest request = factory.lookup(
                IPharPackagingRequest.class,
                IComponentFactory.EMPTY_CONFIG,
                session);
        
        // prepare the request
        request.setStub("die('HELLO STUB!');");
        request.setLargePhar(true);
        request.setAlias("p.phar");
        request.addFile("/some/file.php", new File(session.getCurrentProject().getBasedir(), "testphar.php"));
        request.addDirectory("/", new File(session.getCurrentProject().getBasedir(), "phar1"));
        assertEquals(
                new File(session.getCurrentProject().getBasedir(), "target").getAbsolutePath(),
                request.getTargetDirectory().getAbsolutePath());
        request.setTargetDirectory(session.getCurrentProject().getBasedir());
        request.setFilename(pharFile.getName());
        
        // package
        final DefaultLog logger = new DefaultLog(new ConsoleLogger());
        exec.packagePhar(request, logger);
        assertTrue(pharFile.exists());
        
        final IPhpExecutableConfiguration phpConfig = factory.lookup(
                IPhpExecutableConfiguration.class,
                IComponentFactory.EMPTY_CONFIG,
                session);
        phpConfig.setAdditionalPhpParameters("-d suhosin.executor.include.whitelist=\"phar\"");
        
        // check the phar
        final IPhpExecutable phpExec = phpConfig.getPhpExecutable();
        assertEquals("INSIDE FILE.PHP\n", phpExec.execute(new File(
                session.getCurrentProject().getBasedir(), "alias.php")));
    }

    /**
     * Tests the alias support
     *
     * @throws Exception thrown on errors
     */
    public void testJavaAlias() throws Exception {
        // look up the component factory
        final IComponentFactory factory = lookup(IComponentFactory.class);
        // create the execution config
        final MavenSession session = this.createSimpleSession("phar/large-phar");
        final File pharFile = new File(session.getCurrentProject().getBasedir(), "phar1.phar");
        delete(pharFile);
        final IPharPackagerConfiguration pharConfig = factory.lookup(
                IPharPackagerConfiguration.class,
                IComponentFactory.EMPTY_CONFIG,
                session);
        pharConfig.setPackager("JAVA");
        final IPharPackager exec = pharConfig.getPharPackager();
        final IPharPackagingRequest request = factory.lookup(
                IPharPackagingRequest.class,
                IComponentFactory.EMPTY_CONFIG,
                session);
        
        // prepare the request
        request.setStub("die('HELLO STUB!');");
        request.setLargePhar(true);
        request.setAlias("p.phar");
        request.addFile("/some/file.php", new File(session.getCurrentProject().getBasedir(), "testphar.php"));
        request.addDirectory("/", new File(session.getCurrentProject().getBasedir(), "phar1"));
        assertEquals(
                new File(session.getCurrentProject().getBasedir(), "target").getAbsolutePath(),
                request.getTargetDirectory().getAbsolutePath());
        request.setTargetDirectory(session.getCurrentProject().getBasedir());
        request.setFilename(pharFile.getName());
        
        // package
        final DefaultLog logger = new DefaultLog(new ConsoleLogger());
        exec.packagePhar(request, logger);
        assertTrue(pharFile.exists());
        
        final IPhpExecutableConfiguration phpConfig = factory.lookup(
                IPhpExecutableConfiguration.class,
                IComponentFactory.EMPTY_CONFIG,
                session);
        phpConfig.setAdditionalPhpParameters("-d suhosin.executor.include.whitelist=\"phar\"");
        
        // check the phar
        final IPhpExecutable phpExec = phpConfig.getPhpExecutable();
        assertEquals("INSIDE FILE.PHP\n", phpExec.execute(new File(
                session.getCurrentProject().getBasedir(), "alias.php")));
    }

    private void delete(final File someTxt) {
        if (someTxt.exists()) {
            someTxt.delete();
        }
        assertFalse(someTxt.exists());
    }

}