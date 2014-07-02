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

import org.phpmaven.pear.library.IDependency;

/**
 * Dependency implementation.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 0.1.0
 */
public class Dependency implements IDependency {
    
    /** the dependency type. */
    private DependencyType type;
    
    /** the package name. */
    private String packageName;

    /** the channel name. */
    private String channelName;

    /** min version. */
    private String min;

    /** min excluded. */
    private boolean minExluded;

    /** max version. */
    private String max;

    /** max excluded. */
    private boolean maxExcluded;

    /**
     * {@inheritDoc}
     */
    @Override
    public DependencyType getType() {
        return this.type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setType(DependencyType type) {
        this.type = type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPackageName() {
        return this.packageName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPackageName(String name) {
        this.packageName = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getChannelName() {
        return this.channelName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setChannelName(String name) {
        this.channelName = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMin() {
        return this.min;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMin(String min) {
        this.min = min;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getMinExcluded() {
        return this.minExluded;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMinExcluded(boolean excluded) {
        this.minExluded = excluded;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMax() {
        return this.max;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMax(String max) {
        this.max = max;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getMaxExcluded() {
        return this.maxExcluded;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMaxExcluded(boolean excluded) {
        this.maxExcluded = excluded;
    }

}
