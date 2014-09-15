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
 * A pear package maintainer.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 0.1.0
 */
public interface IMaintainer {

    /**
     * Returns the nick.
     * @return nick.
     */
    String getNick();
    
    /**
     * Sets the nick.
     * @param nick nick.
     */
    void setNick(String nick);
    
    /**
     * Returns the name.
     * @return the name.
     */
    String getName();
    
    /**
     * Sets the name.
     * @param name name.
     */
    void setName(String name);
    
    /**
     * Returns the email.
     * @return the email.
     */
    String getEmail();
    
    /**
     * Sets the email.
     * @param email email.
     */
    void setEMail(String email);
    
    /**
     * Returns the url.
     * @return url.
     */
    String getUrl();
    
    /**
     * Sets the url.
     * @param url url.
     */
    void setUrl(String url);
    
    /**
     * Returns true if the maintainer is active.
     * @return true if the maintainer is active.
     */
    boolean isActive();
    
    /**
     * Sets the active flag of this maintainer.
     * @param isActive true if the maintainer is active.
     */
    void setActive(boolean isActive);
    
    /**
     * Returns the role of the maintainer.
     * @return the role.
     */
    String getRole();
    
    /**
     * Sets the role of the maintainer.
     * @param role maintainer role.
     */
    void setRole(String role);
    
}
