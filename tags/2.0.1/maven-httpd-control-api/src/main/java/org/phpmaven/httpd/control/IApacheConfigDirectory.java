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
package org.phpmaven.httpd.control;



/**
 * common entries of a apache directory section.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.1
 */
public interface IApacheConfigDirectory {
    
    /**
     * Returns the path.
     * @return path
     */
    String getPath();
    
    /**
     * Returns the contents of the config file.
     * @return config file contents.
     */
    String getContents();
    
    /**
     * Sets the whole config file.
     * @param contents config file contents.
     */
    void setContents(String contents);
    
    /**
     * Appends content to the config file.
     * @param contents content to be added.
     */
    void append(String contents);

}
