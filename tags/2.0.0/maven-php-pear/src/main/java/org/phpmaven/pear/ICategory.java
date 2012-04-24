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
 * The pear category.
 * 
 * <p>
 * See <a href="http://pear.php.net/manual/en/core.rest.php#core.rest.fileformats.c-categoriesxml">
 * PEAR documentation</a>.
 * </p>
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
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
