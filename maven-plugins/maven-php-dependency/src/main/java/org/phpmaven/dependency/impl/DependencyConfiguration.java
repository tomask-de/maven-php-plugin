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

package org.phpmaven.dependency.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Configuration;
import org.phpmaven.dependency.IAction;
import org.phpmaven.dependency.IDependency;
import org.phpmaven.dependency.IDependencyConfiguration;

/**
 * Dependency configuration.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.1
 */
@Component(role = IDependencyConfiguration.class, instantiationStrategy = "per-lookup")
public class DependencyConfiguration implements IDependencyConfiguration {
    
    /**
     * Dependencies.
     */
    @Configuration(name = "dependencies", value = "")
    private ArrayList<IDependency> dependencies = new ArrayList<IDependency>();
    
    /**
     * A bootstrap file for bootstrap dependency actions.
     */
    private File bootstrapFile;
    
    /**
     * The default actions.
     */
    @Configuration(name = "defaults", value = "")
    private ArrayList<IAction> defaults = new ArrayList<IAction>();

    
    /**
     * @see org.phpmaven.dependency.IDependencyConfiguration#getDependencies()
     */
    @Override
    public Iterable<IDependency> getDependencies() {
        return Collections.unmodifiableList(this.dependencies);
    }


	@Override
	public File getBootstrapFile() {
		return this.bootstrapFile;
	}


	@Override
	public Iterable<IAction> getDefaults() {
		return this.defaults;
	}
    
}
