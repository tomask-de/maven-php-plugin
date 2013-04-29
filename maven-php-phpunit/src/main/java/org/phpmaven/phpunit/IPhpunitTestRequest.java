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

package org.phpmaven.phpunit;

import java.io.File;

/**
 * A request for phpunit execution.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public interface IPhpunitTestRequest {
    
    /**
     * Adds a test file.
     * @param fileToTest file to be tested.
     */
    void addTestFile(File fileToTest);
    
    /**
     * Ass a test folder.
     * @param folderToTest folder to be tested.
     */
    void addTestFolder(File folderToTest);
    
    /**
     * Returns the entries of this request.
     * @return request entries.
     */
    Iterable<IPhpunitEntry> getEntries();
    
    /**
     * Returns the phpunit xml file to be used.
     * @return phpunit xml file or {@code null} to create a default test suite
     */
    File getPhpunitXml();
    
    /**
     * Sets the phpunit xml file to be used.
     * @param phpUnitXml phpunit xml file to be used.
     */
    void setPhpunitXml(File phpUnitXml);

}
