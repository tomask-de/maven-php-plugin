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

import java.io.File;

import org.apache.maven.execution.MavenSession;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.core.test.comp.ISomeComponent;
import org.phpmaven.test.AbstractTestCase;

/**
 * Test case for the IComponentFactory class.
 * 
 * <p>Test the lookup methods by setting a configuration.</p>
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public class LookupWithConfigsTest extends AbstractTestCase {

    /**
     * Tests if the component lookup reads the project build config from parent poms
     * and overwrites a single value.
     *
     * @throws Exception thrown on errors
     */
    public void testComponentWithOverwrite() throws Exception {
        // look up the component factory
        final IComponentFactory factory = lookup(IComponentFactory.class);
        // create the session
        final MavenSession session = createSimpleSession("core/pom-with-buildconfig-childoverwrite");
        // lookup the sample
        final Xpp3Dom dom = new Xpp3Dom("configuration");
        final Xpp3Dom bar = new Xpp3Dom("bar");
        bar.setValue("MyBarValue");
        dom.addChild(bar);
        final ISomeComponent component = factory.lookup(ISomeComponent.class, dom, session);
        assertNotNull(component);
        // test defaults
        assertEquals(
                new File(session.getCurrentProject().getBasedir().getAbsolutePath(), "SomeFooBar").getAbsolutePath(),
                component.getFooBar().getAbsolutePath());
        assertEquals("OtherFoo", component.getFoo());
        assertEquals("MyBarValue", component.getBar());
    }

    /**
     * Tests if the component lookup reads the project build config from parent poms
     * and overwrites a single value.
     *
     * @throws Exception thrown on errors
     */
    public void testComponentWithArrayOverwrite() throws Exception {
        // look up the component factory
        final IComponentFactory factory = lookup(IComponentFactory.class);
        // create the session
        final MavenSession session = createSimpleSession("core/pom-with-buildconfig-childoverwrite");
        // lookup the sample
        final Xpp3Dom dom = new Xpp3Dom("configuration");
        final Xpp3Dom bar = new Xpp3Dom("bar");
        bar.setValue("MyBarValue");
        dom.addChild(bar);
        final Xpp3Dom dom2 = new Xpp3Dom("configuration");
        final Xpp3Dom bar2 = new Xpp3Dom("bar");
        bar2.setValue("MyBarValue2");
        dom2.addChild(bar2);
        final ISomeComponent component = factory.lookup(
                ISomeComponent.class, new Xpp3Dom[]{dom, dom2}, session);
        assertNotNull(component);
        // test defaults
        assertEquals(
                new File(session.getCurrentProject().getBasedir().getAbsolutePath(), "SomeFooBar").getAbsolutePath(),
                component.getFooBar().getAbsolutePath());
        assertEquals("OtherFoo", component.getFoo());
        assertEquals("MyBarValue2", component.getBar());
    }
    
    /**
     * Tests if the component fails with invalid configs.
     * 
     * @throws Exception thrown on errors
     */
    public void testComponentFailes() throws Exception {
        // look up the component factory
        final IComponentFactory factory = lookup(IComponentFactory.class);
        // create the session
        final MavenSession session = createSimpleSession("core/pom-with-buildconfig-childoverwrite");
        // lookup the sample
        final Xpp3Dom dom = new Xpp3Dom("configuration");
        final Xpp3Dom bar = new Xpp3Dom("bar");
        bar.setValue("MyBarValue");
        dom.addChild(bar);
        final Xpp3Dom dom2 = new Xpp3Dom("configuration");
        final Xpp3Dom bar2 = new Xpp3Dom("bar234");
        bar2.setValue("MyBarValue2");
        dom2.addChild(bar2);
        // lookup the sample
        try {
            factory.lookup(
                    ISomeComponent.class, new Xpp3Dom[]{dom, dom2}, session);
            fail("Exception expected");
        // CHECKSTYLE:OFF
        // checkstyle does not like empty catches
        } catch (PlexusConfigurationException ex) {
            // ignore; we expect this exception
        }
        // CHECKSTYLE:ON
    }
    
    /**
     * Tests if the component fails with invalid configs.
     * 
     * @throws Exception thrown on errors
     */
    public void testComponentFailes2() throws Exception {
        // look up the component factory
        final IComponentFactory factory = lookup(IComponentFactory.class);
        // create the session
        final MavenSession session = createSimpleSession("core/pom-with-buildconfig-childoverwrite");
        // lookup the sample
        final Xpp3Dom dom = new Xpp3Dom("configuration");
        final Xpp3Dom bar = new Xpp3Dom("bar");
        dom.addChild(bar);
        final Xpp3Dom bar2 = new Xpp3Dom("bar");
        bar2.setValue("MyBarValue2");
        bar.addChild(bar2);
        // lookup the sample
        try {
            factory.lookup(
                    ISomeComponent.class, new Xpp3Dom[]{dom}, session);
            fail("Exception expected");
        // CHECKSTYLE:OFF
        // checkstyle does not like empty catches
        } catch (PlexusConfigurationException ex) {
            // ignore; we expect this exception
        }
        // CHECKSTYLE:ON
    }

}