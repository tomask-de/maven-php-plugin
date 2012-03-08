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

package org.phpmaven.phpdoc;

import java.io.File;

/**
 * A single entry for phpdoc.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public interface IPhpdocEntry {
    
    /**
     * The entry type.
     */
    enum EntryType {
        /** file type. */
        FILE,
        /** folder type. */
        FOLDER
    }
    
    /**
     * Returns the entry type.
     * @return entry type.
     */
    EntryType getType();
    
    /**
     * Returns the java file.
     * @return java file (either file or folder).
     */
    File getFile();

}
