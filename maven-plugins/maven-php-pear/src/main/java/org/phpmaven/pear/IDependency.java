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
 * A pear dependency information.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public interface IDependency {
    
    /**
     * The types of dependencies.
     */
    enum DependencyType {
        /** the php dependency. */
        PHP,
        /** the pear installer dependency. */
        PEARINSTALLER,
        /** dependency to another package. */
        PACKAGE,
        /** subpackage dependency. */
        SUBPACKAGE,
        /** sapi dependency. */
        SAPI,
        /** extension dependency. */
        PHP_EXTENSION,
        /** operating system dependency. */
        OS
    }
    
    /**
     * Returns the dependency type.
     * @return type of dependency.
     */
    DependencyType getType();
    
    /**
     * Sets the dependency type.
     * @param type type of dependency.
     */
    void setType(DependencyType type);
    
    /**
     * Returns the name of the dependency for package dependencies.
     * @return package name.
     */
    String getPackageName();
    
    /**
     * Sets the name of the dependency.
     * @param name package name.
     */
    void setPackageName(String name);
    
    /**
     * Returns the channel name.
     * @return channel name.
     */
    String getChannelName();
    
    /**
     * Sets the channel name.
     * @param name channel name.
     */
    void setChannelName(String name);
    
    /**
     * Returns the minimum version.
     * @return version the minimum version.
     */
    String getMin();
    
    /**
     * Sets the minimum version.
     * @param min minimum version.
     */
    void setMin(String min);
    
    /**
     * Returns true if the minimum version is included.
     * @return boolean.
     */
    boolean getMinExcluded();
    
    /**
     * Sets the flag to include the minimum version.
     * @param excluded boolean.
     */
    void setMinExcluded(boolean excluded);
    
    /**
     * Returns the maximum version.
     * @return version the maximum version.
     */
    String getMax();
    
    /**
     * Sets the maximum version.
     * @param max maximum version.
     */
    void setMax(String max);
    
    /**
     * Returns true if the maximum version is included.
     * @return boolean.
     */
    boolean getMaxExcluded();
    
    /**
     * Sets the flag to include the maximum version.
     * @param excluded boolean.
     */
    void setMaxExcluded(boolean excluded);

}
