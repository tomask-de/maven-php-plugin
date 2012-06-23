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

import org.apache.maven.execution.MavenSession;
import org.apache.maven.monitor.logging.DefaultLog;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.phpmaven.core.ExecutionUtils;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.exec.IPhpExecutableConfiguration;
import org.phpmaven.exec.PhpException;
import org.phpmaven.exec.PhpVersion;
import org.phpmaven.test.AbstractTestCase;

/**
 * test cases for PHP version detection.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public class GetVersionTest extends AbstractTestCase {

    /**
     * Tests if the version can be detected.
     *
     * @throws Exception thrown on errors
     */
    public void testGetVersion4() throws Exception {
        // look up the component factory
        final IComponentFactory factory = lookup(IComponentFactory.class);
        // create the execution config
        final MavenSession session = this.createSimpleSession("exec/version");
        final Xpp3Dom dom = new Xpp3Dom("configuration");
        final Xpp3Dom exec = new Xpp3Dom("executable");
        if (ExecutionUtils.isWindows()) {
            exec.setValue(session.getCurrentProject().getBasedir() + "/php4.cmd");
        } else {
            exec.setValue(session.getCurrentProject().getBasedir() + "/php4");
        }
        dom.addChild(exec);
        
        final IPhpExecutableConfiguration execConfig = factory.lookup(
                IPhpExecutableConfiguration.class,
                new Xpp3Dom[]{dom},
                session);

        final Log log = new DefaultLog(new ConsoleLogger());
        assertEquals(PhpVersion.PHP4, execConfig.getPhpExecutable(log).getVersion());
    }

    /**
     * Tests if the version can be detected.
     *
     * @throws Exception thrown on errors
     */
    public void testGetVersion5() throws Exception {
        // look up the component factory
        final IComponentFactory factory = lookup(IComponentFactory.class);
        // create the execution config
        final MavenSession session = this.createSimpleSession("exec/version");
        final Xpp3Dom dom = new Xpp3Dom("configuration");
        final Xpp3Dom exec = new Xpp3Dom("executable");
        if (ExecutionUtils.isWindows()) {
            exec.setValue(session.getCurrentProject().getBasedir() + "/php5.cmd");
        } else {
            exec.setValue(session.getCurrentProject().getBasedir() + "/php5");
        }
        dom.addChild(exec);
        
        final IPhpExecutableConfiguration execConfig = factory.lookup(
                IPhpExecutableConfiguration.class,
                new Xpp3Dom[]{dom},
                session);

        final Log log = new DefaultLog(new ConsoleLogger());
        assertEquals(PhpVersion.PHP5, execConfig.getPhpExecutable(log).getVersion());
    }

    /**
     * Tests if the version can be detected.
     *
     * @throws Exception thrown on errors
     */
    public void testGetVersion6() throws Exception {
        // look up the component factory
        final IComponentFactory factory = lookup(IComponentFactory.class);
        // create the execution config
        final MavenSession session = this.createSimpleSession("exec/version");
        final Xpp3Dom dom = new Xpp3Dom("configuration");
        final Xpp3Dom exec = new Xpp3Dom("executable");
        if (ExecutionUtils.isWindows()) {
            exec.setValue(session.getCurrentProject().getBasedir() + "/php6.cmd");
        } else {
            exec.setValue(session.getCurrentProject().getBasedir() + "/php6");
        }
        dom.addChild(exec);
        
        final IPhpExecutableConfiguration execConfig = factory.lookup(
                IPhpExecutableConfiguration.class,
                new Xpp3Dom[]{dom},
                session);

        final Log log = new DefaultLog(new ConsoleLogger());
        assertEquals(PhpVersion.PHP6, execConfig.getPhpExecutable(log).getVersion());
    }

    /**
     * Tests if the version can be detected.
     *
     * @throws Exception thrown on errors
     */
    public void testGetVersionUnknown() throws Exception {
        // look up the component factory
        final IComponentFactory factory = lookup(IComponentFactory.class);
        // create the execution config
        final MavenSession session = this.createSimpleSession("exec/version");
        final Xpp3Dom dom = new Xpp3Dom("configuration");
        final Xpp3Dom exec = new Xpp3Dom("executable");
        if (ExecutionUtils.isWindows()) {
            exec.setValue(session.getCurrentProject().getBasedir() + "/phpUnknown.cmd");
        } else {
            exec.setValue(session.getCurrentProject().getBasedir() + "/phpUnknown");
        }
        dom.addChild(exec);
        
        final IPhpExecutableConfiguration execConfig = factory.lookup(
                IPhpExecutableConfiguration.class,
                new Xpp3Dom[]{dom},
                session);

        final Log log = new DefaultLog(new ConsoleLogger());
        assertEquals(PhpVersion.UNKNOWN, execConfig.getPhpExecutable(log).getVersion());
    }

    /**
     * Tests if the version can be detected.
     *
     * @throws Exception thrown on errors
     */
    public void testGetVersionIllegal() throws Exception {
        // look up the component factory
        final IComponentFactory factory = lookup(IComponentFactory.class);
        // create the execution config
        final MavenSession session = this.createSimpleSession("exec/version");
        final Xpp3Dom dom = new Xpp3Dom("configuration");
        final Xpp3Dom exec = new Xpp3Dom("executable");
        if (ExecutionUtils.isWindows()) {
            exec.setValue(session.getCurrentProject().getBasedir() + "/phpIllegal.cmd");
        } else {
            exec.setValue(session.getCurrentProject().getBasedir() + "/phpIllegal");
        }
        dom.addChild(exec);
        
        final IPhpExecutableConfiguration execConfig = factory.lookup(
                IPhpExecutableConfiguration.class,
                new Xpp3Dom[]{dom},
                session);

        final Log log = new DefaultLog(new ConsoleLogger());
        try {
            execConfig.getPhpExecutable(log).getVersion();
            fail("Expected exception not thrown");
        // CHECKSTYLE:OFF
        // checkstyle does not like empty catches
        } catch (PhpException ex) {
            // ignore; we expect this exception
        }
        // CHECKSTYLE:ON
    }

    /**
     * Tests if the version can be detected.
     *
     * @throws Exception thrown on errors
     */
    public void testGetVersion4NotCached() throws Exception {
        // look up the component factory
        final IComponentFactory factory = lookup(IComponentFactory.class);
        // create the execution config
        final MavenSession session = this.createSimpleSession("exec/version");
        final Xpp3Dom dom = new Xpp3Dom("configuration");
        final Xpp3Dom exec = new Xpp3Dom("executable");
        if (ExecutionUtils.isWindows()) {
            exec.setValue(session.getCurrentProject().getBasedir() + "/php4.cmd");
        } else {
            exec.setValue(session.getCurrentProject().getBasedir() + "/php4");
        }
        dom.addChild(exec);
        
        final IPhpExecutableConfiguration execConfig = factory.lookup(
                IPhpExecutableConfiguration.class,
                new Xpp3Dom[]{dom},
                session);
        execConfig.setUseCache(false);

        final Log log = new DefaultLog(new ConsoleLogger());
        assertEquals(PhpVersion.PHP4, execConfig.getPhpExecutable(log).getVersion());
    }

    /**
     * Tests if the version can be detected.
     *
     * @throws Exception thrown on errors
     */
    public void testGetVersion5NotCached() throws Exception {
        // look up the component factory
        final IComponentFactory factory = lookup(IComponentFactory.class);
        // create the execution config
        final MavenSession session = this.createSimpleSession("exec/version");
        final Xpp3Dom dom = new Xpp3Dom("configuration");
        final Xpp3Dom exec = new Xpp3Dom("executable");
        if (ExecutionUtils.isWindows()) {
            exec.setValue(session.getCurrentProject().getBasedir() + "/php5.cmd");
        } else {
            exec.setValue(session.getCurrentProject().getBasedir() + "/php5");
        }
        dom.addChild(exec);
        
        final IPhpExecutableConfiguration execConfig = factory.lookup(
                IPhpExecutableConfiguration.class,
                new Xpp3Dom[]{dom},
                session);
        execConfig.setUseCache(false);

        final Log log = new DefaultLog(new ConsoleLogger());
        assertEquals(PhpVersion.PHP5, execConfig.getPhpExecutable(log).getVersion());
    }

    /**
     * Tests if the version can be detected.
     *
     * @throws Exception thrown on errors
     */
    public void testGetVersion6NotCached() throws Exception {
        // look up the component factory
        final IComponentFactory factory = lookup(IComponentFactory.class);
        // create the execution config
        final MavenSession session = this.createSimpleSession("exec/version");
        final Xpp3Dom dom = new Xpp3Dom("configuration");
        final Xpp3Dom exec = new Xpp3Dom("executable");
        if (ExecutionUtils.isWindows()) {
            exec.setValue(session.getCurrentProject().getBasedir() + "/php6.cmd");
        } else {
            exec.setValue(session.getCurrentProject().getBasedir() + "/php6");
        }
        dom.addChild(exec);
        
        final IPhpExecutableConfiguration execConfig = factory.lookup(
                IPhpExecutableConfiguration.class,
                new Xpp3Dom[]{dom},
                session);
        execConfig.setUseCache(false);

        final Log log = new DefaultLog(new ConsoleLogger());
        assertEquals(PhpVersion.PHP6, execConfig.getPhpExecutable(log).getVersion());
    }

    /**
     * Tests if the version can be detected.
     *
     * @throws Exception thrown on errors
     */
    public void testGetVersionUnknownNotCached() throws Exception {
        // look up the component factory
        final IComponentFactory factory = lookup(IComponentFactory.class);
        // create the execution config
        final MavenSession session = this.createSimpleSession("exec/version");
        final Xpp3Dom dom = new Xpp3Dom("configuration");
        final Xpp3Dom exec = new Xpp3Dom("executable");
        if (ExecutionUtils.isWindows()) {
            exec.setValue(session.getCurrentProject().getBasedir() + "/phpUnknown.cmd");
        } else {
            exec.setValue(session.getCurrentProject().getBasedir() + "/phpUnknown");
        }
        dom.addChild(exec);
        
        final IPhpExecutableConfiguration execConfig = factory.lookup(
                IPhpExecutableConfiguration.class,
                new Xpp3Dom[]{dom},
                session);
        execConfig.setUseCache(false);

        final Log log = new DefaultLog(new ConsoleLogger());
        assertEquals(PhpVersion.UNKNOWN, execConfig.getPhpExecutable(log).getVersion());
    }

    /**
     * Tests if the version can be detected.
     *
     * @throws Exception thrown on errors
     */
    public void testGetVersionIllegalNotCached() throws Exception {
        // look up the component factory
        final IComponentFactory factory = lookup(IComponentFactory.class);
        // create the execution config
        final MavenSession session = this.createSimpleSession("exec/version");
        final Xpp3Dom dom = new Xpp3Dom("configuration");
        final Xpp3Dom exec = new Xpp3Dom("executable");
        if (ExecutionUtils.isWindows()) {
            exec.setValue(session.getCurrentProject().getBasedir() + "/phpIllegal.cmd");
        } else {
            exec.setValue(session.getCurrentProject().getBasedir() + "/phpIllegal");
        }
        dom.addChild(exec);
        
        final IPhpExecutableConfiguration execConfig = factory.lookup(
                IPhpExecutableConfiguration.class,
                new Xpp3Dom[]{dom},
                session);
        execConfig.setUseCache(false);

        final Log log = new DefaultLog(new ConsoleLogger());
        try {
            execConfig.getPhpExecutable(log).getVersion();
            fail("Expected exception not thrown");
        // CHECKSTYLE:OFF
        // checkstyle does not like empty catches
        } catch (PhpException ex) {
            // ignore; we expect this exception
        }
        // CHECKSTYLE:ON
    }
    
}