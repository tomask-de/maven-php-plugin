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

import org.apache.maven.execution.MavenSession;
import org.apache.maven.monitor.logging.DefaultLog;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.pear.IPearConfiguration;
import org.phpmaven.pear.IPearUtility;
import org.phpmaven.test.AbstractTestCase;

/**
 * test cases for adding a local channel.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public class ChannelAddTest extends AbstractTestCase {
    
    /**
     * Gets the maven session.
     * @return gets the maven session.
     * @throws Exception thrown on pear errors.
     */
    private MavenSession getSession() throws Exception {
        // create the execution config
        final MavenSession session = this.createSessionForPhpMaven("pear/local-add");
        return session;
    }

    /**
     * Tests if we can add a channel locally.
     *
     * @throws Exception thrown on errors
     */
    public void testChannelAdd() throws Exception {
        // look up the component factory
        final IComponentFactory factory = lookup(IComponentFactory.class);
        final MavenSession session = getSession();
        final IPearConfiguration pearConfig = factory.lookup(
                IPearConfiguration.class,
                IComponentFactory.EMPTY_CONFIG,
                session);
        // assert that we are able to create the util
        final DefaultLog logger = new DefaultLog(new ConsoleLogger());
        final IPearUtility util = pearConfig.getUtility(logger);
        
        util.installPear(false);
        util.initChannels(false);
        util.channelAdd("pear-dummy.php-maven.org", null, "Dummy test channel");
    }

}