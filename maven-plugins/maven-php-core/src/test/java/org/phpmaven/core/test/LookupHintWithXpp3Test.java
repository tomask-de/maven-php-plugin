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
import org.phpmaven.core.test.comp.ISomeComponentHint;
import org.phpmaven.core.test.comp.SomeComponentXpp3Dom;
import org.phpmaven.test.AbstractTestCase;

/**
 * Test case for the IComponentFactory class.
 * 
 * <p>Test the lookup methods by setting a configuration.</p>
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public class LookupHintWithXpp3Test extends AbstractTestCase {

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
        final Xpp3Dom xpp = new Xpp3Dom("xpp");
        dom.setAttribute("foo", "bar");
        final Xpp3Dom xppc = new Xpp3Dom("child");
        xppc.setAttribute("baz", "bazvalue");
        xppc.setValue("someValue");
        xpp.addChild(xppc);
        dom.addChild(xpp);
        final ISomeComponentHint component = factory.lookup(ISomeComponentHint.class, "xpp3dom", dom, session);
        assertNotNull(component);
        // test defaults
        assertEquals(
                new File(session.getCurrentProject().getBasedir().getAbsolutePath(), "SomeFooBar").getAbsolutePath(),
                component.getFooBar().getAbsolutePath());
        assertEquals("OtherFoo", component.getFoo());
        assertEquals("MyBarValue", component.getBar());
        
        assertEquals(xpp, ((SomeComponentXpp3Dom) component).getXpp());
    }

}