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
 * Base interface for actions to be performed.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.1
 */
public interface IAction {

    /**
     * The action type
     */
    enum ActionType {
        /** classic action (extract to php-deps/php-test-deps) */
        ACTION_CLASSIC,
        /** special extraction instruction */
        ACTION_EXTRACT,
        /** ignore action */
        ACTION_IGNORE,
        /** put phar on include path */
        ACTION_INCLUDE,
        /** extract and include instruction */
        ACTION_EXTRACT_INCLUDE,
        /** install through pear */
        ACTION_PEAR
    }
    
    /**
     * Returns the action type
     * @return action type
     */
    ActionType getType();

}
