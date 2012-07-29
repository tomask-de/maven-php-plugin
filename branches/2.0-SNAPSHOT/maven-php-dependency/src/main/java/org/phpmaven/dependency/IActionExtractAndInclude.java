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
 * A php dependency configuration.
 * 
 * <p>If you do not provide additional configuration this action behaves the same as classic action.
 * You can specify a relative path inside the phar file or an alternative path (or both). The
 * include path is relative to target path. Example:</p>
 * 
 * <pre>
 * &nbsp;&nbsp;&lt;actions><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;extract><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;pharPath>/my/local/path/inside/phar&lt;/pharPath><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;targetPath>${project.build.directory}/anotherPath&lt;/targetPath><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;includePath>/special&lt;/includePath><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/extract><br />
 * &nbsp;&nbsp;&lt;/actions><br />
 * </pre>
 * 
 * <p>This example will extract the "/my/local/path/inside/phar" into the "target/anotherPath" folder and
 * will add "target/anotherPath/special" onto php include path.</p>
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.1
 */
public interface IActionExtractAndInclude extends IAction {

    /**
     * Returns the local path inside the phar file
     * 
     * @return local path.
     */
    String getPharPath();

    /**
     * Returns the target path
     * 
     * @return target path.
     */
    String getTargetPath();

    /**
     * Returns the include path
     * 
     * @return target path.
     */
    String getIncludePath();

}
