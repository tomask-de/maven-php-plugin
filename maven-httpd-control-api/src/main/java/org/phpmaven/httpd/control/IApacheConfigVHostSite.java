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
 * A helper interface to manipulate and write apache configuration files (virtual host declarations).
 * 
 * Receive a new instance through IApacheConfig.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.1
 */
public interface IApacheConfigVHostSite extends IApacheConfigCommon {
    
    /**
     * Returns true if ssl was enabled.
     * @return true for ssl engine enabled.
     */
    boolean isSslEnabled();

}
