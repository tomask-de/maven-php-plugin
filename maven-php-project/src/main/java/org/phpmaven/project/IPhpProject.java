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

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.phpexec.library.PhpException;

/**
 * Helper to do actions on php projects.
 * 
 * <p>
 * Create an instance via {@link IComponentFactory}.
 * </p>
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.3
 */
public interface IPhpProject {
	
	/**
	 * Prepares the dependencies for given project
	 * @param log the logging
	 * @param targetDir default target dir (php-deps or php-test-deps)
	 * @param sourceScope the scope (test or compile)
	 * @throws MojoExecutionException thrown on errors
	 * @throws PhpException thrown on errors
	 */
	void prepareDependencies(final Log log, final File targetDir, String sourceScope) throws MojoExecutionException, PhpException;
	
	/**
	 * Validates the dependencies.
	 * @throws MojoExecutionException thrown if the dependency configuration are invalid
	 */
	void validateDependencies() throws MojoExecutionException;

}
