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

import java.lang.reflect.Field;

import org.apache.maven.execution.MavenSession;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.configurator.ComponentConfigurator;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.easymock.EasyMock;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.core.test.comp.ISomeComponent;
import org.phpmaven.core.test.comp.ISomeComponentHint;
import org.phpmaven.test.AbstractTestCase;

/**
 * Base test cases for the ComponentFactory.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.1
 */
public class ComponentFactoryTest extends AbstractTestCase {

    /**
     * Tests the lookup with null values.
     *
     * @throws Exception thrown on errors
     */
    public void testLookupNull() throws Exception {
        // look up the component factory
        final IComponentFactory factory = lookup(IComponentFactory.class);
        
        // set a dummy container
        final Field field = factory.getClass().getDeclaredField("plexusContainer");
        field.setAccessible(true);
        final PlexusContainer origContainer = (PlexusContainer) field.get(factory);
        
        // mocking
        final PlexusContainer container = EasyMock.createMock(PlexusContainer.class);
        field.set(factory, container);
        
        // tunnel
        EasyMock.expect(container.lookup(ISomeComponent.class)).andDelegateTo(origContainer);
        EasyMock.expect(container.getComponentDescriptor(ISomeComponent.class.getName(), "default")).andReturn(
                null);
        EasyMock.expect(container.getContainerRealm()).andDelegateTo(origContainer);
        // mocked behaviour
        EasyMock.expect(container.lookup(ComponentConfigurator.class, "php-maven")).
            andDelegateTo(origContainer).anyTimes();
        container.release(EasyMock.anyObject());
        EasyMock.expectLastCall().andDelegateTo(origContainer).anyTimes();
        
        // start test
        EasyMock.replay(container);
        
        // invoke configuration
        // create the session
        final MavenSession session = createSimpleEmptySession();
        // lookup the sample
        assertNotNull(factory.lookup(ISomeComponent.class, (Xpp3Dom[]) null, session));
        
        EasyMock.verify(container);
    }

    /**
     * Tests the lookup with null values.
     *
     * @throws Exception thrown on errors
     */
    public void testLookupHintNull() throws Exception {
        // look up the component factory
        final IComponentFactory factory = lookup(IComponentFactory.class);
        
        // set a dummy container
        final Field field = factory.getClass().getDeclaredField("plexusContainer");
        field.setAccessible(true);
        final PlexusContainer origContainer = (PlexusContainer) field.get(factory);
        
        // mocking
        final PlexusContainer container = EasyMock.createMock(PlexusContainer.class);
        field.set(factory, container);
        
        // tunnel
        EasyMock.expect(container.lookup(ISomeComponentHint.class, "hint1")).andReturn(
                origContainer.lookup(ISomeComponentHint.class, "hint1"));
        EasyMock.expect(container.getComponentDescriptor(ISomeComponentHint.class.getName(), "hint1")).andReturn(
                null);
        EasyMock.expect(container.getContainerRealm()).andReturn(
                origContainer.getContainerRealm());
        // mocked behaviour
        EasyMock.expect(container.lookup(ComponentConfigurator.class, "php-maven")).andReturn(
                origContainer.lookup(ComponentConfigurator.class, "php-maven")).anyTimes();
        container.release(EasyMock.anyObject());
        EasyMock.expectLastCall().andDelegateTo(origContainer).anyTimes();
        
        // start test
        EasyMock.replay(container);
        
        // invoke configuration
        // create the session
        final MavenSession session = createSimpleEmptySession();
        // lookup the sample
        assertNotNull(factory.lookup(ISomeComponentHint.class, "hint1", (Xpp3Dom[]) null, session));
        
        EasyMock.verify(container);
    }

    /**
     * Tests if the component factory is handling non-found configurators.
     *
     * @throws Exception thrown on errors
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testConfiguratorProblems() throws Exception {
        // look up the component factory
        final IComponentFactory factory = lookup(IComponentFactory.class);
        
        // set a dummy container
        final Field field = factory.getClass().getDeclaredField("plexusContainer");
        field.setAccessible(true);
        final PlexusContainer origContainer = (PlexusContainer) field.get(factory);
        
        // mocking
        final PlexusContainer container = EasyMock.createMock(PlexusContainer.class);
        field.set(factory, container);
        
        // tunnel
        EasyMock.expect(container.lookup(ISomeComponent.class)).andReturn(
                origContainer.lookup(ISomeComponent.class));
        EasyMock.expect(container.getComponentDescriptor(ISomeComponent.class.getName(), "default")).andReturn(
                (ComponentDescriptor) origContainer.getComponentDescriptor(ISomeComponent.class.getName(), "default"));
        EasyMock.expect(container.getContainerRealm()).andReturn(
                origContainer.getContainerRealm());
        // mocked behaviour
        EasyMock.expect(container.lookup(ComponentConfigurator.class, "php-maven")).
                andThrow(new ComponentLookupException("ex", "ex", "ex"));
        
        // start test
        EasyMock.replay(container);
        
        // invoke configuration
        // create the session
        final MavenSession session = createSimpleEmptySession();
        // lookup the sample
        try {
            factory.lookup(ISomeComponent.class, (Xpp3Dom) null, session);
            fail("expected exception not thrown");
        // CHECKSTYLE:OFF
        // checkstyle does not like empty catches
        } catch (ComponentLookupException ex) {
            // this exception is expected
        }
        // CHECKSTYLE:ON
        
        EasyMock.verify(container);
    }

    /**
     * Tests if the component factory is handling release failures.
     *
     * @throws Exception thrown on errors
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testReleaseProblems() throws Exception {
        // look up the component factory
        final IComponentFactory factory = lookup(IComponentFactory.class);
        
        // set a dummy container
        final Field field = factory.getClass().getDeclaredField("plexusContainer");
        field.setAccessible(true);
        final PlexusContainer origContainer = (PlexusContainer) field.get(factory);
        
        // mocking
        final PlexusContainer container = EasyMock.createMock(PlexusContainer.class);
        field.set(factory, container);
        
        // tunnel
        EasyMock.expect(container.lookup(ISomeComponent.class)).andReturn(
                origContainer.lookup(ISomeComponent.class));
        EasyMock.expect(container.getComponentDescriptor(ISomeComponent.class.getName(), "default")).andReturn(
                (ComponentDescriptor) origContainer.getComponentDescriptor(ISomeComponent.class.getName(), "default"));
        EasyMock.expect(container.getContainerRealm()).andReturn(
                origContainer.getContainerRealm());
        EasyMock.expect(container.lookup(ComponentConfigurator.class, "php-maven")).andReturn(
                origContainer.lookup(ComponentConfigurator.class, "php-maven")).anyTimes();
        // mocked behaviour
        container.release(EasyMock.anyObject());
        EasyMock.expectLastCall().andThrow(new ComponentLifecycleException("ex")).anyTimes();
        
        // start test
        EasyMock.replay(container);
        
        // invoke configuration
        // create the session
        final MavenSession session = createSimpleEmptySession();
        // lookup the sample
        final ISomeComponent comp = factory.lookup(ISomeComponent.class, (Xpp3Dom) null, session);
        assertNotNull(comp);
        
        EasyMock.verify(container);
    }
    
    

}