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

package org.phpmaven.phpunit.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.component.annotations.Component;
import org.phpmaven.phpunit.IPhpunitEntry;
import org.phpmaven.phpunit.IPhpunitTestRequest;

/**
 * A phpunit test request implementation.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
@Component(role = IPhpunitTestRequest.class, instantiationStrategy = "per-lookup")
public class PhpunitTestRequest implements IPhpunitTestRequest {
    
    /**
     * Test entries.
     */
    private List<IPhpunitEntry> entries = new ArrayList<IPhpunitEntry>();
    
    /**
     * The phpunit xml file to be used.
     */
    private File phpUnitXml;

    /**
     * {@inheritDoc}
     */
    @Override
    public void addTestFile(File fileToTest) {
        this.entries.add(new PhpunitFileEntry(fileToTest));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addTestFolder(File folderToTest) {
        this.entries.add(new PhpunitFolderEntry(folderToTest));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<IPhpunitEntry> getEntries() {
        return this.entries;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public File getPhpunitXml() {
        return this.phpUnitXml;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setPhpunitXml(File phpUnitXml) {
        this.phpUnitXml = phpUnitXml;
    }

}
