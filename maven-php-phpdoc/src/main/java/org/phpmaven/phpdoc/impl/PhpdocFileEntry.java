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

package org.phpmaven.phpdoc.impl;

import java.io.File;

import org.phpmaven.phpdoc.IPhpdocEntry;

/**
 * A file entry for phpdoc requests.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public class PhpdocFileEntry implements IPhpdocEntry {

    /**
     * File.
     */
    private final File file;
    
    /**
     * Constructor to create a new file entry.
     * @param file the file.
     */
    public PhpdocFileEntry(File file) {
        this.file = file;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntryType getType() {
        return EntryType.FILE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getFile() {
        return this.file;
    }

}
