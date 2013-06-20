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
package org.apache.httpd.config;

/**
 * A line in config file (single for directives or multiple for sections).
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.1
 */
interface IConfigFileLineDirective extends IConfigFileLine {
    
    /**
     * Returns the directive name.
     * @return directive name.
     */
    String getDirectiveName();

    /**
     * Sets the value.
     * @param value value for single valued directives
     */
    void setValue(String value);

    /**
     * Returns the value.
     * @return value.
     */
    String getValue();

    /**
     * Returns the value at given index.
     * @param index index.
     * @return value at given index.
     */
    String getValue(int index);

    /**
     * Sets the value at given index.
     * @param index index.
     * @param value value.
     */
    void setValue(int index, String value);
    
}
