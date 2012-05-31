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

package org.phpmaven.core;

import org.apache.maven.execution.MavenSession;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.phpmaven.core.comp.ISomeComponentHint;

import java.io.File;

/**
 * Test case for the IComponentFactory class.
 * 
 * <p>Test the lookup methods with role-hints and with build configuration filter.</p>
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.1
 */
public class LookupWithFilterTest extends AbstractTestCase {

    /**
     * Tests if the component lookup initializes the defaults.
     *
     * @throws Exception thrown on errors
     */
    public void testComponentLookupDefaults() throws Exception {
        // look up the component factory
        final IComponentFactory factory = lookup(IComponentFactory.class);
        // create the session
        final MavenSession session = createSimpleSession("core/pom-with-buildconfig");
        // lookup the sample
        final ISomeComponentHint component = factory.lookup(
                ISomeComponentHint.class,
                "with-filter",
                (Xpp3Dom) null,
                session);
        assertNotNull(component);
        // test defaults
        assertEquals(
                new File(session.getCurrentProject().getBasedir().getAbsolutePath(), "SomeFooBar").getAbsolutePath(),
                component.getFooBar().getAbsolutePath());
        assertEquals("default-foo", component.getFoo());
        assertEquals("default-bar", component.getBar());
    }

    /**
     * Tests if the component lookup initializes the defaults.
     *
     * @throws Exception thrown on errors
     */
    public void testComponentLookupDefaultsArray() throws Exception {
        // look up the component factory
        final IComponentFactory factory = lookup(IComponentFactory.class);
        // create the session
        final MavenSession session = createSimpleSession("core/pom-with-buildconfig");
        // lookup the sample
        final ISomeComponentHint component = factory.lookup(
                ISomeComponentHint.class,
                "with-filter",
                (Xpp3Dom[]) null,
                session);
        assertNotNull(component);
        // test defaults
        assertEquals(
                new File(session.getCurrentProject().getBasedir().getAbsolutePath(), "SomeFooBar").getAbsolutePath(),
                component.getFooBar().getAbsolutePath());
        assertEquals("default-foo", component.getFoo());
        assertEquals("default-bar", component.getBar());
    }

    /**
     * Tests if the component lookup initializes the defaults.
     *
     * @throws Exception thrown on errors
     */
    public void testComponentLookupPath() throws Exception {
        // look up the component factory
        final IComponentFactory factory = lookup(IComponentFactory.class);
        // create the session
        final MavenSession session = createSimpleSession("core/pom-with-path");
        // lookup the sample
        final ISomeComponentHint component = factory.lookup(
                ISomeComponentHint.class,
                "with-path",
                (Xpp3Dom) null,
                session);
        assertNotNull(component);
        // test defaults
        assertEquals(
                new File(session.getCurrentProject().getBasedir().getAbsolutePath(), "SomeFooBar").getAbsolutePath(),
                component.getFooBar().getAbsolutePath());
        assertEquals("default-foo", component.getFoo());
        assertEquals("default-bar", component.getBar());
    }

    /**
     * Tests if the component lookup initializes the defaults.
     *
     * @throws Exception thrown on errors
     */
    public void testComponentLookupPathEmpty() throws Exception {
        // look up the component factory
        final IComponentFactory factory = lookup(IComponentFactory.class);
        // create the session
        final MavenSession session = createSimpleSession("core/empty-pom");
        // lookup the sample
        final ISomeComponentHint component = factory.lookup(
                ISomeComponentHint.class,
                "with-path",
                (Xpp3Dom) null,
                session);
        assertNotNull(component);
        // test defaults
        assertEquals(
                new File(session.getCurrentProject().getBasedir().getAbsolutePath(), "fooBar").getAbsolutePath(),
                component.getFooBar().getAbsolutePath());
        assertEquals("default-foo", component.getFoo());
        assertEquals("default-bar", component.getBar());
    }

    /**
     * Tests if the component lookup initializes the defaults.
     *
     * @throws Exception thrown on errors
     */
    public void testComponentLookupPathArray() throws Exception {
        // look up the component factory
        final IComponentFactory factory = lookup(IComponentFactory.class);
        // create the session
        final MavenSession session = createSimpleSession("core/pom-with-path");
        // lookup the sample
        final ISomeComponentHint component = factory.lookup(
                ISomeComponentHint.class,
                "with-path",
                (Xpp3Dom[]) null,
                session);
        assertNotNull(component);
        // test defaults
        assertEquals(
                new File(session.getCurrentProject().getBasedir().getAbsolutePath(), "SomeFooBar").getAbsolutePath(),
                component.getFooBar().getAbsolutePath());
        assertEquals("default-foo", component.getFoo());
        assertEquals("default-bar", component.getBar());
    }

    /**
     * Tests if the component lookup initializes the defaults.
     *
     * @throws Exception thrown on errors
     */
    public void testComponentLookupPathEmptyArray() throws Exception {
        // look up the component factory
        final IComponentFactory factory = lookup(IComponentFactory.class);
        // create the session
        final MavenSession session = createSimpleSession("core/empty-pom");
        // lookup the sample
        final ISomeComponentHint component = factory.lookup(
                ISomeComponentHint.class,
                "with-path",
                (Xpp3Dom[]) null,
                session);
        assertNotNull(component);
        // test defaults
        assertEquals(
                new File(session.getCurrentProject().getBasedir().getAbsolutePath(), "fooBar").getAbsolutePath(),
                component.getFooBar().getAbsolutePath());
        assertEquals("default-foo", component.getFoo());
        assertEquals("default-bar", component.getBar());
    }

}