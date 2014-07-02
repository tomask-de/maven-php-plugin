/**
 * Copyright 2010-2012 by PHP-maven.org
 * 
 * This file is part of pear-java.
 *
 * pear-java is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * pear-java is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with pear-java.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.phpmaven.pear.library;

/**
 * A pear dependency information.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 0.1.0
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
