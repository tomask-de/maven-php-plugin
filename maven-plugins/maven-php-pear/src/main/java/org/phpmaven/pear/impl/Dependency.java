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

import org.phpmaven.pear.IDependency;

/**
 * Dependency implementation.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
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
