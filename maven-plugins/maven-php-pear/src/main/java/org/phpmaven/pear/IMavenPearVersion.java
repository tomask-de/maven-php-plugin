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

import org.phpmaven.pear.library.IVersion;

/**
 * A single package version (extended version for maven).
 * 
 * <p>
 * The package version supports mapping between pear versions and maven versions.
 * </p>
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public interface IMavenPearVersion extends IVersion {
    
    /**
     * Returns the maven version.
     * @return maven version.
     */
    String getMavenVersion();
    
    /**
     * Sets the maven version.
     * @param mavenVersion maven version.
     */
    void setMavenVersion(String mavenVersion);

}
