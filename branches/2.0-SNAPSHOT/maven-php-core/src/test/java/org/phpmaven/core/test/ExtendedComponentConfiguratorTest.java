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

import org.codehaus.plexus.component.configurator.ComponentConfigurator;
import org.codehaus.plexus.configuration.DefaultPlexusConfiguration;
import org.phpmaven.core.test.comp.SomeComponentXpp3Dom;
import org.phpmaven.test.AbstractTestCase;

/**
 * Base test cases for the ExtendedComponentConfigurator.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.1
 */
public class ExtendedComponentConfiguratorTest extends AbstractTestCase {

    /**
     * Tests if the configurator is aware of non-xml configurations.
     *
     * @throws Exception thrown on errors
     */
    public void testNonXml() throws Exception {
        // look up
        final ComponentConfigurator config = lookup(ComponentConfigurator.class, "php-maven");
        final SomeComponentXpp3Dom component = new SomeComponentXpp3Dom();
        final DefaultPlexusConfiguration plexusConfig = new DefaultPlexusConfiguration("foo");
        plexusConfig.addChild(new DefaultPlexusConfiguration("xpp"));
        config.configureComponent(
                component,
                plexusConfig,
                this.getContainer().getLookupRealm());
    }

}