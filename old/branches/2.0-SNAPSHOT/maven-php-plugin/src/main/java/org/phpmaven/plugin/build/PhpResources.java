/**
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

package org.phpmaven.plugin.build;

import java.io.File;


/**
 * php-validate execute the php with all php files under the source folder. 
 * All dependencies will be part of the include_path. 
 * The command line call looks like php {compileArgs} -d={generatedIncludePath} {sourceFile}
 *
 * @requiresDependencyResolution compile
 * @goal resources
 * @author Martin Eisengardt
 */
public final class PhpResources extends AbstractPhpResources {

    @Override
    protected File getSourceFolder() {
        return this.getSourceDirectory();
    }

    @Override
    protected File getTargetFolder() {
        return this.getTargetClassesDirectory();
    }
    
}
