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

import org.phpmaven.pear.IVersion;

/**
 * A single version number.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public class Version implements IVersion {
    
    /**
     * The pear version number.
     */
    private String pearVersion;
    
    /**
     * The maven version number.
     */
    private String mavenVersion;

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
        this.mavenVersion = Package.convertPearVersionToMavenVersion(version);
        this.pearVersion = version;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMavenVersion() {
        return this.mavenVersion;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMavenVersion(String mavenVersion) {
        this.pearVersion = Package.convertMavenVersionToPearVersion(mavenVersion);
        this.mavenVersion = mavenVersion;
    }

}
