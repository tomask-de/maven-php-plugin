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

package org.phpmaven.dependency.test;

import java.util.Iterator;

import org.apache.maven.execution.MavenSession;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.dependency.IAction;
import org.phpmaven.dependency.IActionClassic;
import org.phpmaven.dependency.IActionExtract;
import org.phpmaven.dependency.IActionExtractAndInclude;
import org.phpmaven.dependency.IActionIgnore;
import org.phpmaven.dependency.IActionInclude;
import org.phpmaven.dependency.IActionPear;
import org.phpmaven.dependency.IDependency;
import org.phpmaven.dependency.IDependencyConfiguration;
import org.phpmaven.test.AbstractTestCase;

/**
 * test cases for dependency management.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public class BaseTest extends AbstractTestCase {

    /**
     * Tests if the dependencies are correctly configured.
     *
     * @throws Exception thrown on errors
     */
    public void testBasic() throws Exception {
        // look up the component factory
        final IComponentFactory factory = lookup(IComponentFactory.class);
        // create the execution config
        final MavenSession session = this.createSimpleSession("dependency/simple");
        final IDependencyConfiguration depConfig = factory.lookup(
                IDependencyConfiguration.class,
                IComponentFactory.EMPTY_CONFIG,
                session);
        assertNotNull(depConfig);
        
        IDependency dep = this.findDep(depConfig, "org.group1", "classic1");
        Iterator<IAction> actionIter = dep.getActions().iterator();
        assertTrue(actionIter.hasNext());
        IAction action = actionIter.next();
        assertEquals(action.getType(), IAction.ActionType.ACTION_CLASSIC);
        assertTrue(action instanceof IActionClassic);
        assertFalse(actionIter.hasNext());
        
        dep = this.findDep(depConfig, "org.group1", "ignore1");
        actionIter = dep.getActions().iterator();
        assertTrue(actionIter.hasNext());
        action = actionIter.next();
        assertEquals(action.getType(), IAction.ActionType.ACTION_IGNORE);
        assertTrue(action instanceof IActionIgnore);
        assertFalse(actionIter.hasNext());
        
        dep = this.findDep(depConfig, "org.group1", "pear");
        actionIter = dep.getActions().iterator();
        assertTrue(actionIter.hasNext());
        action = actionIter.next();
        assertEquals(action.getType(), IAction.ActionType.ACTION_PEAR);
        assertTrue(action instanceof IActionPear);
        assertFalse(actionIter.hasNext());
        
        dep = this.findDep(depConfig, "org.group1", "extract1");
        actionIter = dep.getActions().iterator();
        assertTrue(actionIter.hasNext());
        action = actionIter.next();
        assertEquals(action.getType(), IAction.ActionType.ACTION_EXTRACT);
        assertTrue(action instanceof IActionExtract);
        assertEquals("/some/path", ((IActionExtract)action).getPharPath());
        assertEquals(session.getCurrentProject().getBuild().getDirectory() + "/somepath", ((IActionExtract)action).getTargetPath());
        assertFalse(actionIter.hasNext());
        
        dep = this.findDep(depConfig, "org.group1", "extractAndInclude1");
        actionIter = dep.getActions().iterator();
        assertTrue(actionIter.hasNext());
        action = actionIter.next();
        assertEquals(action.getType(), IAction.ActionType.ACTION_EXTRACT_INCLUDE);
        assertTrue(action instanceof IActionExtractAndInclude);
        assertEquals("/some/path", ((IActionExtractAndInclude)action).getPharPath());
        assertEquals(session.getCurrentProject().getBuild().getDirectory() + "/somepath", ((IActionExtractAndInclude)action).getTargetPath());
        assertEquals("/some/other/path", ((IActionExtractAndInclude)action).getIncludePath());
        assertFalse(actionIter.hasNext());
        
        dep = this.findDep(depConfig, "org.group1", "include1");
        actionIter = dep.getActions().iterator();
        assertTrue(actionIter.hasNext());
        action = actionIter.next();
        assertEquals(action.getType(), IAction.ActionType.ACTION_INCLUDE);
        assertTrue(action instanceof IActionInclude);
        assertEquals("/some/path", ((IActionInclude)action).getPharPath());
        assertFalse(actionIter.hasNext());
        
        dep = this.findDep(depConfig, "org.group1", "extractDefaults");
        actionIter = dep.getActions().iterator();
        assertTrue(actionIter.hasNext());
        action = actionIter.next();
        assertEquals(action.getType(), IAction.ActionType.ACTION_EXTRACT);
        assertTrue(action instanceof IActionExtract);
        assertEquals("/", ((IActionExtract)action).getPharPath());
        assertEquals("", ((IActionExtract)action).getTargetPath());
        assertFalse(actionIter.hasNext());
        
        dep = this.findDep(depConfig, "org.group1", "extractAndIncludeDefaults");
        actionIter = dep.getActions().iterator();
        assertTrue(actionIter.hasNext());
        action = actionIter.next();
        assertEquals(action.getType(), IAction.ActionType.ACTION_EXTRACT_INCLUDE);
        assertTrue(action instanceof IActionExtractAndInclude);
        assertEquals("/", ((IActionExtractAndInclude)action).getPharPath());
        assertEquals("", ((IActionExtractAndInclude)action).getTargetPath());
        assertEquals("/", ((IActionExtractAndInclude)action).getIncludePath());
        assertFalse(actionIter.hasNext());
        
        dep = this.findDep(depConfig, "org.group1", "includeDefaults");
        actionIter = dep.getActions().iterator();
        assertTrue(actionIter.hasNext());
        action = actionIter.next();
        assertEquals(action.getType(), IAction.ActionType.ACTION_INCLUDE);
        assertTrue(action instanceof IActionInclude);
        assertEquals("/", ((IActionInclude)action).getPharPath());
        assertFalse(actionIter.hasNext());
    }
    
    private IDependency findDep(IDependencyConfiguration config, String groupId, String artifactId) {
        for (final IDependency dep : config.getDependencies()) {
            if (dep.getGroupId().equals(groupId) && dep.getArtifactId().equals(artifactId)) {
                return dep;
            }
        }
        fail("dependency " + groupId + ":" + artifactId + " not found");
        return null;
    }
    
}