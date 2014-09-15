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

package org.phpmaven.pear.library.impl;

import org.phpmaven.pear.library.IMaintainer;

/**
 * Maintainer implementation.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 0.1.0
 */
public class Maintainer implements IMaintainer {
    
    /**
     * Nickname.
     */
    private String nick;
    
    /**
     * Real name.
     */
    private String name;

    /**
     * The url to be used.
     */
    private String url;

    /**
     * true if the maintainer is active.
     */
    private boolean isActive;

    /**
     * maintainer role.
     */
    private String role;
    
    /**
     * email address.
     */
    private String email;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNick() {
        return this.nick;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNick(String nick) {
        this.nick = nick;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUrl() {
        return this.url;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isActive() {
        return this.isActive;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setActive(boolean active) {
        this.isActive = active;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRole() {
        return this.role;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEmail() {
        return this.email;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEMail(String em) {
        this.email = em;
    }

}
