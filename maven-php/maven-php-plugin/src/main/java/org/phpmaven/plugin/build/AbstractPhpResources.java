/**
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

package org.phpmaven.plugin.build;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.lint.ILintChecker;
import org.phpmaven.lint.ILintExecution;
import org.phpmaven.plugin.php.AbstractPhpWalkHelper;
import org.phpmaven.plugin.php.IPhpWalkConfigurationMojo;
import org.phpmaven.plugin.php.MultiException;


/**
 * php-validate execute the php with all php files under the source folder. 
 * All dependencies will be part of the include_path. 
 * The command line call looks like php {compileArgs} -d={generatedIncludePath} {sourceFile}
 *
 * @requiresDependencyResolution compile
 * @author Tobias Sarnowski
 * @author Christian Wiedemann
 * @author Martin Eisengardt
 */
public abstract class AbstractPhpResources extends AbstractPhpWalkMojo {

    /**
     * A list of files which will not be validated but they will also be part of the result.
     *
     * @parameter
     */
    private String[] excludeFromValidation = new String[0];

    /**
     * If true the validation will be skipped and the source files will be moved to the target/classes
     * folder wihtout validation.
     *
     * @parameter default-value="false" expression="${ignoreValidate}"
     */
    private boolean ignoreValidate;

    /**
     * If true, php maven will allways overwrite existing php files in the classes folder
     * even if the files in the target folder are newer or at the same date.
     *
     * @parameter default-value="false" expression="${forceOverwrite}"
     */
    private boolean forceOverwrite;

    /**
     * The lint checker.
     */
    private ILintChecker checker;
    
    /**
     * The directory containing generated test classes of the project being tested. This will be included at the
     * beginning of the test classpath.
     * 
     * @parameter default-value="${project.build.testOutputDirectory}"
     * @readonly
     */
    private File targetTestClassesDirectory;
    
    /**
     * The directory containing generated classes of the project being tested. This will be included after the test
     * classes in the test classpath.
     * 
     * @parameter default-value="${project.build.outputDirectory}"
     * @readonly
     */
    private File targetClassesDirectory;

    /**
     * Returns if the PHP validation should be skipped.
     *
     * @return if the validation should be skipped
     */
    private boolean isIgnoreValidate() {
        return ignoreValidate;
    }
    
    /**
     * Where the sources should get copied to.
     *
     * @return where the jar inclusion directory is
     */
    public File getTargetClassesDirectory() {
        return this.targetClassesDirectory;
    }

    /**
     * The target directory where to copy the test sources to.
     *
     * @return where the test-jar inclusion directory is
     */
    public File getTargetTestClassesDirectory() {
        return this.targetTestClassesDirectory;
    }
    
    /**
     * Returns the source folder to be used.
     * @return source folder
     */
    protected abstract File getSourceFolder();
    
    /**
     * Returns the target folder to be used.
     * @return target folder
     */
    protected abstract File getTargetFolder();

    /**
     * Do not care about file timestamps and copy every time.
     *
     * @return forces target files to be overwritten
     */
    public boolean isForceOverwrite() {
        return forceOverwrite;
    }
    
    /**
     * Checks a file if it should be excluded from processing.
     *
     * @param file
     * @return if the file should be excluded from validation
     */
    private boolean isExcluded(File file) {
        for (String excluded : excludeFromValidation) {
            if (file.getAbsolutePath().replace("\\", "/").endsWith(excluded.replace("\\", "/"))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void execute() throws MojoExecutionException {
        if (isIgnoreValidate()) {
            getLog().info("Validation of php sources is disabled.");
        }
        if (!isIncludeInJar()) {
            getLog().info("Not including php sources in resulting output.");
        }
        
        // resolve wildcards in excludeFromValidation
        excludeFromValidation = FileHelper.getWildcardMatches(excludeFromValidation, getSourceDirectory(), false);

        try {
            getLog().info("Copying source files and performing LINT validation...");
            this.checker = this.factory.lookup(ILintChecker.class, IComponentFactory.EMPTY_CONFIG, this.getSession());
            new PhpWalkHelper(this).goRecursiveAndCall(this.getSourceFolder());
        } catch (MultiException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        } catch (ComponentLookupException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        } catch (PlexusConfigurationException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
        
        boolean hadFailure = false;
        for (final ILintExecution failure : this.checker.run(this.getLog())) {
            getLog().info("Lint check failure for " + failure.getFile(), failure.getException());
            hadFailure = true;
        }
        if (hadFailure) {
            throw new MojoExecutionException("Lint check failures.");
        }
    }

    /**
     * Helper to walk through php resources.
     */
    private final class PhpWalkHelper extends AbstractPhpWalkHelper {
        private PhpWalkHelper(IPhpWalkConfigurationMojo config) {
            super(config);
        }

        @Override
        protected void handleProcessedFile(File file) throws MojoExecutionException {
            if (!isIncludeInJar()) {
                return;
            }

            try {
                FileHelper.copyToFolder(
                        getSourceFolder(),
                        getTargetFolder(),
                        file,
                        isForceOverwrite());
            } catch (IOException e) {
                throw new MojoExecutionException("Failed to copy source file to target directory", e);
            }
        }

        @Override
        protected void handlePhpFile(File file) throws MojoExecutionException {
            if (isIgnoreValidate() || isExcluded(file)) {
                return;
            }
            
            checker.addFileToCheck(file);
        }
    }
    
}
