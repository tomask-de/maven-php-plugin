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

/**
 * pack a phar file using the contents of the library.
 *
 * @requiresDependencyResolution compile
 * @goal phar
 * @author Martin Eisengardt
 */
public final class PhpPhar extends AbstractPharMojo {
    
    @Override
    protected PharContentEntry[] getDefaultEntries() {
        final PharContentEntry entry = new PharContentEntry();
        entry.setFile(this.getTargetClassesDirectory());
        entry.setRelPath(this.getTargetClassesDirectory().getAbsolutePath());
        return new PharContentEntry[]{entry};
    }

    @Override
    protected String getDefaultFilename() {
        return this.getProject().getBuild().getFinalName() + ".phar";
    }

    
}
