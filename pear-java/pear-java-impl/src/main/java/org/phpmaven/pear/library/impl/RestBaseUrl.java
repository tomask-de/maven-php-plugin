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

import org.phpmaven.pear.library.IRestBaseUrl;

/**
 * Rest base url.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 0.1.0
 */
public class RestBaseUrl implements IRestBaseUrl {

    /** version. */
    private String version;
    
    /** base url. */
    private String baseUrl;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRestVersion() {
        return this.version;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRestVersion(String ver) {
        this.version = ver;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBaseUrl() {
        return this.baseUrl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBaseUrl(String url) {
        this.baseUrl = url;
    }

}
