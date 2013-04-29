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

package org.phpmaven.phar;

import java.io.File;

/**
 * A phar directory entry.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public class PharDirectory extends PharEntry {

    /**
     * The relative path.
     */
    private String relativePath;

    /**
     * The directory to be packed.
     */
    private File pathToPack;

    /**
     * Returns the relative path inside the phar.
     * 
     * @return relative path
     */
    public String getRelativePath() {
        return relativePath;
    }

    /**
     * Sets the relative path inside the phar.
     * 
     * @param relativePath relative path
     */
    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    /**
     * Returns the path to be packed.
     * 
     * @return the directory.
     */
    public File getPathToPack() {
        return pathToPack;
    }

    /**
     * Sets the path to be packed.
     * 
     * @param pathToPack directory to be packed
     */
    public void setPathToPack(File pathToPack) {
        this.pathToPack = pathToPack;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntryType getType() {
        return EntryType.DIRECTORY;
    }

}
