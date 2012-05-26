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
 * A phar file entry.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public class PharFile extends PharEntry {

    /**
     * The file.
     */
    private File file;

    /**
     * The local name.
     */
    private String localName;

    /**
     * Returns the file to be packed.
     * 
     * @return file
     */
    public File getFile() {
        return file;
    }

    /**
     * Sets the file to be packed.
     * 
     * @param file the file
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * Returns the local name within phar.
     * 
     * @return local name
     */
    public String getLocalName() {
        return localName;
    }

    /**
     * Sets the local name within phar.
     * 
     * @param localName local name
     */
    public void setLocalName(String localName) {
        this.localName = localName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntryType getType() {
        return EntryType.FILE;
    }

}
