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
 * The pear category.
 * 
 * <p>
 * See <a href="http://pear.php.net/manual/en/core.rest.php#core.rest.fileformats.c-categoriesxml">
 * PEAR documentation</a>.
 * </p>
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 0.1.0
 */
public interface ICategory {
    
    /**
     * Returns the href.
     * @return href.
     */
    String getHRef();
    
    /**
     * Returns the name.
     * @return name.
     */
    String getName();
    
    /**
     * Sets the href.
     * @param href href.
     */
    void setHRef(String href);
    
    /**
     * Sets the name.
     * @param name name.
     */
    void setName(String name);
    
    /**
     * Returns the Alias name.
     * @return alias name.
     */
    String getAlias();
    
    /**
     * Sets the alias name.
     * @param alias alias name.
     */
    void setAlias(String alias);
    
    /**
     * Returns the description.
     * @return description.
     */
    String getDescription();
    
    /**
     * Set the description.
     * @param desc description.
     */
    void setDescription(String desc);
    
    /**
     * Returns the packages in this category.
     * @return packages in this category.
     */
    Iterable<IPackage> getPackages();
    
    /**
     * Adds a new package.
     * @param pkg the package to be added.
     */
    void addPackage(IPackage pkg);

}
