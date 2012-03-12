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

package org.phpmaven.pear.impl;

import org.phpmaven.pear.IMaintainer;

/**
 * Maintainer implementation.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
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
