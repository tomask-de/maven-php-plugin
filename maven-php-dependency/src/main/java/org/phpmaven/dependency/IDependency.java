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

package org.phpmaven.dependency;


/**
 * A single php dependency configuration.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.1
 */
public interface IDependency {

    /**
     * Returns the group id
     * @return group id
     */
    String getGroupId();
    
    /**
     * Returns the artifact id
     * @return artifact id
     */
    String getArtifactId();
    
    /**
     * Returns the actions.
     * @return actions to be performed
     */
    Iterable<IAction> getActions();

}
