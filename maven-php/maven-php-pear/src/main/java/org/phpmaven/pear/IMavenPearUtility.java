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

import org.phpmaven.pear.library.IPearUtility;
import org.phpmaven.phpexec.library.PhpException;


/**
 * The pear utility (extended version for maven).
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public interface IMavenPearUtility extends IPearUtility {
    
    /**
     * Converts a maven version to a pear version.
     * @param src maven version
     * @return pear version
     */
    String convertMavenVersionToPearVersion(String src);
    
    /**
     * Converts a pear version to a maven version.
     * @param src pear version.
     * @return maven version.
     */
    String convertPearVersionToMavenVersion(String src);
    
    /**
     * Installs given artifact from repository (ignores core packages).
     * @param groupId maven group id.
     * @param artifactId maven artifact id.
     * @param version maven version.
     * @throws PhpException the php exception is thrown on installation errors.
     */
    void installFromMavenRepository(final String groupId, final String artifactId, final String version)
        throws PhpException;
    
    /**
     * Installs given artifact from repository (includes core packages).
     * @param groupId maven group id.
     * @param artifactId maven artifact id.
     * @param version maven version.
     * @throws PhpException the php exception is thrown on installation errors.
     */
    void installCoreFromMavenRepository(final String groupId, final String artifactId, final String version)
        throws PhpException;
    
    /**
     * Returns true if given Package is a core package; core packages are automatically
     * installed while pear itself is installed. 
     * @param groupId group id
     * @param artifactId artifact id
     * @return true if this is a core package.
     */
    boolean isMavenCorePackage(final String groupId, final String artifactId);

}
