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

package org.phpmaven.statedb.test;

import org.apache.maven.execution.MavenSession;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.statedb.IStateDatabase;
import org.phpmaven.test.AbstractTestCase;

/**
 * test cases for statedb support.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public class BaseTest extends AbstractTestCase {

    /**
     * Tests if the state db can be created.
     *
     * @throws Exception thrown on errors
     */
    public void testCreation() throws Exception {
        // look up the component factory
        final IComponentFactory factory = lookup(IComponentFactory.class);
        final MavenSession session = this.createSimpleEmptySession();
        
        final IStateDatabase db = factory.lookup(
                IStateDatabase.class,
                IComponentFactory.EMPTY_CONFIG,
                session);
        assertNotNull(db);
        assertNotNull(db.getDbfile());
        assertNull(db.get("foo", "bar", "baz", String.class));
        assertFalse(db.getDbfile().exists());
    }
    
    /**
     * Tests the save of the database
     * 
     * @throws Exception
     */
    public void testSave() throws Exception {
        // look up the component factory
        final IComponentFactory factory = lookup(IComponentFactory.class);
        final MavenSession session = this.createSimpleEmptySession();
        
        IStateDatabase db = factory.lookup(
                IStateDatabase.class,
                IComponentFactory.EMPTY_CONFIG,
                session);
        assertNotNull(db);
        assertNotNull(db.getDbfile());
        assertNull(db.get("foo", "bar", "baz", String.class));
        assertFalse(db.getDbfile().exists());
        
        db.set("foo", "bar", "baz", "persistence");
        assertTrue(db.getDbfile().exists());
        assertEquals("persistence", db.get("foo", "bar", "baz", String.class));
        
        db.reload();
        assertEquals("persistence", db.get("foo", "bar", "baz", String.class));
    }
   
}