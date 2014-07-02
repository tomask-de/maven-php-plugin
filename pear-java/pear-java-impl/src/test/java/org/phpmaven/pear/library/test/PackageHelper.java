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
package org.phpmaven.pear.library.test;

import org.phpmaven.pear.library.impl.Package;

/**
 * Simple helper to access the maven conversion functions
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 0.1.0
 */
public class PackageHelper extends Package {
	
	/**
     * Converts a maven version to a pear version. (non-api method)
     * @param src maven version
     * @return pear version
     */
    public static String convertMavenVersionToPearVersion(String src) {
    	return Package.convertMavenVersionToPearVersion(src);
    }
    
    /**
     * Converts a pear version to a maven version. (non-api method)
     * @param src pear version.
     * @return maven version.
     */
    public static String convertPearVersionToMavenVersion(String src) {
    	return Package.convertPearVersionToMavenVersion(src);
    }

}
