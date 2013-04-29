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

package org.phpmaven.project.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.phpmaven.core.BuildPluginConfiguration;
import org.phpmaven.core.ConfigurationParameter;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.project.IPhpFeature;
import org.phpmaven.project.IPhpFeatures;

/**
 * The php features implementation.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.3
 */
@Component(role = IPhpFeatures.class, instantiationStrategy = "per-lookup")
@BuildPluginConfiguration(groupId = "org.phpmaven", artifactId = "maven-php-project", filter = {"dependenciesDir", "testDependenciesDir", "executableConfig"})
public class PhpFeatures implements IPhpFeatures {
	
	/**
	 * The features configuration.
	 */
	private List<String> features = new ArrayList<String>();
	
	/**
	 * The php features.
	 */
	private List<IPhpFeature> phpFeatures;
	
	/**
	 * Cached exception while building the php features.
	 */
	private RuntimeException cachedException;

	/**
	 * the component factory
	 */
	@Requirement
	private IComponentFactory factory;
	
	/**
	 * The maven session
	 */
	@ConfigurationParameter(name="session", expression="${session}")
	private MavenSession session;
	
	/**
	 * Initializes the php features.
	 */
	private void init() {
		// rethrow a new exception with same info than cached exception
		if (this.cachedException != null) {
			throw new IllegalStateException(this.cachedException.getMessage(), this.cachedException.getCause());
		}
		
		if (this.phpFeatures == null) {
			this.phpFeatures = new ArrayList<IPhpFeature>();
			for (final String name : this.features) {
				try {
					final IPhpFeature phpFeature = this.factory.lookup(IPhpFeature.class, name, IComponentFactory.EMPTY_CONFIG, this.session);
					this.phpFeatures.add(phpFeature);
				} catch (PlexusConfigurationException ex) {
					this.cachedException = new IllegalStateException("Unable to lookup php feature '" + name + "'", ex);
					throw this.cachedException;
				} catch (ComponentLookupException ex) {
					this.cachedException = new IllegalStateException("Unable to lookup php feature '" + name + "'", ex);
					throw this.cachedException;
				}
			}
		}
	}
	
	/**
	 * @see org.phpmaven.project.IPhpFeatures#getFeatures()
	 */
	@Override
	public Iterable<IPhpFeature> getFeatures() {
		this.init();
		return Collections.unmodifiableList(this.phpFeatures);
	}

	/**
	 * @see org.phpmaven.project.IPhpFeatures#getFeatures(java.lang.Class)
	 */
	@Override
	public <T extends IPhpFeature> Iterable<T> getFeatures(Class<T> clazz) {
		this.init();
		final List<T> result = new ArrayList<T>();
		for (final IPhpFeature feature : this.phpFeatures) {
			if (clazz.isInstance(feature)) {
				result.add(clazz.cast(feature));
			}
		}
		return Collections.unmodifiableList(result);
	}

}
