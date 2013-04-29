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

package org.phpmaven.project;

import org.phpmaven.core.IComponentFactory;

/**
 * Helper to fetch the features activated on a project.
 * 
 * <p>
 * Create an instance via {@link IComponentFactory}.
 * </p>
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.3
 */
public interface IPhpFeatures {
	
	/**
	 * Returns all features.
	 * @return all features that were configured in the project.
	 */
	Iterable<IPhpFeature> getFeatures();
	
	/**
	 * Returns the features implementing given interface/ extending given class.
	 * @param clazz the class to be implemented or extended
	 * @return the features for this project
	 */
	<T extends IPhpFeature> Iterable<T> getFeatures(Class<T> clazz);

}
