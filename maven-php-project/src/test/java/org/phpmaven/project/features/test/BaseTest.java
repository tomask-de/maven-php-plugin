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

package org.phpmaven.project.features.test;

import java.util.Iterator;

import org.apache.maven.execution.MavenSession;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.project.IPhpFeature;
import org.phpmaven.project.IPhpFeatures;
import org.phpmaven.test.AbstractTestCase;

/**
 * test cases for PHP project support with features.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public class BaseTest extends AbstractTestCase {

    /**
     * Tests if the features class can be created
     *
     * @throws Exception thrown on errors
     */
    public void testCreation() throws Exception {
        // look up the component factory
        final IComponentFactory factory = lookup(IComponentFactory.class);
        
        // create the features class
        final MavenSession session = this.createSimpleSession(
                "project/features/ok");
        final IPhpFeatures features = factory.lookup(
                IPhpFeatures.class,
                IComponentFactory.EMPTY_CONFIG,
                session);
        // assert that it is not null
        assertNotNull(features);
    }

    /**
     * Tests if the features class can be created
     *
     * @throws Exception thrown on errors
     */
    public void testOK() throws Exception {
        // look up the component factory
        final IComponentFactory factory = lookup(IComponentFactory.class);
        
        // create the features class
        final MavenSession session = this.createSimpleSession(
                "project/features/ok");
        final IPhpFeatures features = factory.lookup(
                IPhpFeatures.class,
                IComponentFactory.EMPTY_CONFIG,
                session);
        // assert that it is not null
        assertNotNull(features);
        
        final Iterator<IPhpFeature> iter = features.getFeatures().iterator();
        assertTrue(iter.hasNext());
        final IPhpFeature feature = iter.next();
        assertFalse(iter.hasNext());
        assertTrue(feature instanceof TestFoo);
        
        final Iterator<TestFoo> iter2 = features.getFeatures(TestFoo.class).iterator();
        assertTrue(iter2.hasNext());
        final TestFoo foo = iter2.next();
        assertFalse(iter2.hasNext());
        assertNotNull(foo);
    }

    /**
     * Tests if the features class can be created
     *
     * @throws Exception thrown on errors
     */
    public void testFailed() throws Exception {
        // look up the component factory
        final IComponentFactory factory = lookup(IComponentFactory.class);
        
        // create the features class
        final MavenSession session = this.createSimpleSession(
                "project/features/failed");
        final IPhpFeatures features = factory.lookup(
                IPhpFeatures.class,
                IComponentFactory.EMPTY_CONFIG,
                session);
        // assert that it is not null
        assertNotNull(features);
        
        try {
	        features.getFeatures();
	        fail("Expected exception not thrown");
        } catch (IllegalStateException ex) {
        	// expected
	    }
    }
   
}