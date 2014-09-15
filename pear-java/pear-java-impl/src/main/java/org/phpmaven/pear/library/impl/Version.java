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

import org.phpmaven.pear.library.IVersion;

/**
 * A single version number.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 0.1.0
 */
public class Version implements IVersion {
    
    /**
     * The pear version number.
     */
    private String pearVersion;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPearVersion() {
        return this.pearVersion;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPearVersion(String version) {
        this.pearVersion = version;
    }

}
