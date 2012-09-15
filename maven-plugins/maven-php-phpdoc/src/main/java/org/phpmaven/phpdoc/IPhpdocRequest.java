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
 * A request for phpdoc generation.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public interface IPhpdocRequest {
    
    /**
     * Returns the phpdoc arguments to be added to phpdoc invocation.
     * @return phpdoc arguments.
     */
    String getPhpdocArgs();
    
    /**
     * Sets the phpdoc arguments to be added to phpdoc invocation.
     * @param args phpdoc arguments.
     */
    void setPhpdocArgs(String args);
    
    /**
     * Returns the phpdoc report folder.
     * @return report folder.
     */
    File getReportFolder();
    
    /**
     * Sets the phpdoc report folder.
     * @param folder folder.
     */
    void setReportFolder(File folder);
    
    /**
     * Returns the install folder.
     * @return install folder.
     */
    File getInstallFolder();
    
    /**
     * Sets the install folder.
     * @param folder folder.
     */
    void setInstallFolder(File folder);
    
    /**
     * Returns the flag to install phpdoc.
     * @return true to install phpdoc.
     */
    boolean getInstallPhpdoc();
    
    /**
     * Sets the flag to install phpdoc.
     * @param install true to install phpdoc.
     */
    void setInstallPhpdoc(boolean install);
    
    /**
     * Adds a file to be included in report.
     * @param file file to be reported.
     */
    void addFile(File file);
    
    /**
     * Adds a folder to be included in report.
     * @param folder folder to be reported.
     */
    void addFolder(File folder);
    
    /**
     * Returns the entries of phpdoc report generation.
     * @return phpdoc report entries.
     */
    Iterable<IPhpdocEntry> getEntries();

}
