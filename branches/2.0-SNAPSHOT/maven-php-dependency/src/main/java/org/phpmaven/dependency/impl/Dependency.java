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

package org.phpmaven.dependency.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.codehaus.plexus.component.annotations.Configuration;
import org.phpmaven.dependency.IAction;
import org.phpmaven.dependency.IDependency;

/**
 * Dependency.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.1
 */
public class Dependency implements IDependency {
    
    /**
     * Actions.
     */
    @Configuration(name = "actions", value = "")
    private List<IAction> actions = new ArrayList<IAction>();
    
    /**
     * The group id.
     */
    @Configuration(name = "groupId", value = "")
    private String groupId;
    
    /**
     * The artifact id.
     */
    @Configuration(name = "artifactId", value = "")
    private String artifactId;

    
    @Override
    public String getGroupId() {
        return this.groupId;
    }


    @Override
    public String getArtifactId() {
        return this.artifactId;
    }


    @Override
    public Iterable<IAction> getActions() {
        return Collections.unmodifiableList(this.actions);
    }
    
}
