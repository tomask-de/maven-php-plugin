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
package org.phpmaven.plugin.phar;

import java.io.File;

/**
 * A phar directory entry.
 * 
 * @author Martin Eisengardt
 */
public class PharDirectory extends PharEntry {

    /**
     * The files directory.
     */
    private File directory;

    /**
     * The base directory.
     */
    private File baseDirectory;

    /**
     * Returns the directory to be packed.
     * 
     * @return directory
     */
    public File getDirectory() {
        return directory;
    }

    /**
     * Sets the directory to be packed.
     * 
     * @param directory directory
     */
    public void setDirectory(File directory) {
        this.directory = directory;
    }

    /**
     * Returns the base directory to build local relative file names.
     * 
     * @return the base directory.
     */
    public File getBaseDirectory() {
        return baseDirectory;
    }

    /**
     * Sets the base directory to build local relative file names.
     * 
     * @param baseDirectory base directory
     */
    public void setBaseDirectory(File baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

}
