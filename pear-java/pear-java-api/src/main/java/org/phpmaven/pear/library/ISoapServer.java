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
 * A soap server definition.
 *  
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 0.1.0
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
