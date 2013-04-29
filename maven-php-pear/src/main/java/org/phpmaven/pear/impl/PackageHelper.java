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

import org.phpmaven.pear.library.impl.Package;

/**
 * Package helper to access the maven conversion functions.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public class PackageHelper extends Package {
    
    /**
     * Converts a maven version to a pear version.
     * @param src maven version
     * @return pear version
     */
    public static String convertMavenVersionToPearVersion(String src) {
    	return Package.convertMavenVersionToPearVersion(src);
    }
    
    /**
     * Converts a pear version to a maven version.
     * @param src pear version.
     * @return maven version.
     */
    public static String convertPearVersionToMavenVersion(String src) {
        return Package.convertPearVersionToMavenVersion(src);
    }

}
