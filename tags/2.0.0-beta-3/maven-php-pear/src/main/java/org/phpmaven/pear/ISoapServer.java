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

package org.phpmaven.pear;

/**
 * A soap server definition.
 *  
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public interface ISoapServer {
    
    /**
     * Returns the path.
     * @return path.
     */
    String getPath();
    
    /**
     * Sets the path.
     * @param path path.
     */
    void setPath(String path);
    
    /**
     * Returns the soap functions.
     * @return soap functions.
     */
    Iterable<ISoapFunction> getFunctions();
    
    /**
     * Adds the soap function.
     * @param function soap function.
     */
    void addFunction(ISoapFunction function);

}
