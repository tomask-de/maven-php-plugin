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

package org.phpmaven.phar;

import java.io.File;

import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.phpmaven.exec.PhpException;

/**
 * A phar packager used to package, extract and read phars.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public interface IPharPackager {

    /**
     * Package a phar file.
     * 
     * @param request The packaging request.
     * @param log The logger.
     * 
     * @throws PhpException thrown if the packaging failed.
     * @throws ComponentLookupException thrown if the configuration failed.
     * @throws PlexusConfigurationException thrown if the configuration failed.
     */
    void packagePhar(IPharPackagingRequest request, Log log)
        throws PhpException, ComponentLookupException, PlexusConfigurationException;
    
    /**
     * Reads the stub file from given phar package.
     * 
     * @param pharPackage the phar package.
     * @param log The logger.
     * @return contents of the phar stub.
     * 
     * @throws PhpException thrown if the reading failed.
     * @throws ComponentLookupException thrown if the configuration failed.
     * @throws PlexusConfigurationException thrown if the configuration failed.
     */
    String readStub(File pharPackage, Log log)
        throws PhpException, ComponentLookupException, PlexusConfigurationException;
    
    /**
     * Extracts the phar file to given package.
     * 
     * @param pharPackage the phar package.
     * @param targetDirectory the target directory.
     * @param log The logger.
     * 
     * @throws PhpException thrown if the extraction failed.
     * @throws ComponentLookupException thrown if the configuration failed.
     * @throws PlexusConfigurationException thrown if the configuration failed.
     */
    void extractPharTo(File pharPackage, File targetDirectory, Log log)
        throws PhpException, ComponentLookupException, PlexusConfigurationException;
    
    /**
     * Lists all files of a phar archive.
     * 
     * @param pharPackage phar package.
     * @param log logger.
     * @return iterable with file names that are packed into the phar.
     * 
     * @throws PhpException thrown if the reading failed.
     * @throws ComponentLookupException thrown if the configuration failed.
     * @throws PlexusConfigurationException thrown if the configuration failed.
     */
    Iterable<String> listFiles(File pharPackage, Log log)
        throws PhpException, ComponentLookupException, PlexusConfigurationException;

}
