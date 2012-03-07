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

package org.phpmaven.pear.test;

import java.io.File;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.monitor.logging.DefaultLog;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.exec.PhpException;
import org.phpmaven.pear.IPearChannel;
import org.phpmaven.pear.IPearConfiguration;
import org.phpmaven.pear.IPearUtility;
import org.phpmaven.test.AbstractTestCase;

/**
 * test cases for the pear support.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public class BaseTest extends AbstractTestCase {

    /**
     * Tests if the pear utility can be created.
     *
     * @throws Exception thrown on errors
     */
    public void testPUCreation() throws Exception {
        // look up the component factory
        final IComponentFactory factory = lookup(IComponentFactory.class);
        final MavenSession session = getSession();
        final IPearConfiguration pearConfig = factory.lookup(
                IPearConfiguration.class,
                IComponentFactory.EMPTY_CONFIG,
                session);
        // assert that it is not null
        assertNotNull(pearConfig);
        // assert that we are able to create the util
        final DefaultLog logger = new DefaultLog(new ConsoleLogger());
        final IPearUtility util = pearConfig.getUtility(logger);
        assertNotNull(util);
    }
    /**
     * Gets the maven session.
     * @return gets the maven session.
     * @throws Exception thrown on pear errors.
     */
    private MavenSession getSession() throws Exception {
        // create the execution config
        final MavenSession session = this.createSimpleSession("pear/empty-pom");
        return session;
    }

    /**
     * Tests if the channel.xml can be read.
     *
     * @throws Exception thrown on errors
     */
    public void testChannelXml() throws Exception {
        // look up the component factory
        final IComponentFactory factory = lookup(IComponentFactory.class);
        final MavenSession session = getSession();
        final IPearConfiguration pearConfig = factory.lookup(
                IPearConfiguration.class,
                IComponentFactory.EMPTY_CONFIG,
                session);
        // assert that we are able to create the util
        final IPearUtility util = getPearUtility(pearConfig, true);
        
        final IPearChannel channel = util.channelDiscover(
                "file://" + 
                session.getCurrentProject().getBasedir().getAbsolutePath() + 
                "/pear.php.net/channel.xml");
        assertNotNull(channel);
        assertNotNull(channel.getRestUrl(IPearChannel.REST_1_3));
        assertEquals("pear.php.net", channel.getName());
        assertEquals("pear", channel.getSuggestedAlias());
        assertEquals("PHP Extension and Application Repository", channel.getSummary());
    }
    
    /**
     * Returns the pear utility.
     * @param pearConfig
     * @param install
     * @return
     * @throws PlexusConfigurationException
     * @throws ComponentLookupException
     * @throws PhpException
     */
    private IPearUtility getPearUtility(final IPearConfiguration pearConfig, final boolean install)
        throws PlexusConfigurationException, ComponentLookupException,
            PhpException {
        final DefaultLog logger = new DefaultLog(new ConsoleLogger());
        final IPearUtility util = pearConfig.getUtility(logger);
        
        if (util.isInstalled()) {
            util.uninstall();
        }
        
        if (install) {
            util.installPear(false);
        }
        return util;
    }

}