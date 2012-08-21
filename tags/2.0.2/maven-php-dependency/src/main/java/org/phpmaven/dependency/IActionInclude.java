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
 * Action to put the phar or a sub path on include path.
 * 
 * <p>If you do not provide additional configuration the phar itself is put on the include path.
 * You can specify a relative path inside the phar file. Example:</p>
 * 
 * <pre>
 * &nbsp;&nbsp;&lt;actions><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;include>&lt;pharPath>/my/local/path&lt;/pharPath>&lt;/include><br />
 * &nbsp;&nbsp;&lt;/actions><br />
 * </pre>
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.1
 */
public interface IActionInclude extends IAction {

    /**
     * Returns the local path inside the phar file
     * 
     * @return local path.
     */
    String getPharPath();

}
