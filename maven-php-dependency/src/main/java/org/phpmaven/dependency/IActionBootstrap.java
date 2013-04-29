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
 * Bootstrap action: invoke a bootstrap file on dependencies.
 * 
 * <p>There is no additional configuration needed for this action but the dependency configuration needs information about a bootstrap file. Example:</p>
 * 
 * <pre>
 * &nbsp;&nbsp;&lt;actions><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;bootstrap/><br />
 * &nbsp;&nbsp;&lt;/actions><br />
 * </pre>
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.3
 */
public interface IActionBootstrap extends IAction {
    
    // empty

}
