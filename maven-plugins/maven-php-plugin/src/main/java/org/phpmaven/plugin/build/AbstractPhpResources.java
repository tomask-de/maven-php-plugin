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
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.phpmaven.plugin.lint.LintExecution;
import org.phpmaven.plugin.lint.LintHelper;
import org.phpmaven.plugin.php.AbstractPhpWalkHelper;
import org.phpmaven.plugin.php.IPhpWalkConfigurationMojo;
import org.phpmaven.plugin.php.MultiException;
import org.phpmaven.plugin.php.PhpErrorException;
import org.phpmaven.plugin.php.PhpException;


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
public abstract class AbstractPhpResources extends AbstractPhpMojo {

    /**
     * A list of files which will not be validated but they will also be part of the result.
     *
     * @parameter
     */
    private String[] excludeFromValidation = new String[0];
    
    /**
     * Flag to use the runkit lint check.
     * 
     * May tune performance during lint check of large projects, but requires extension unkit to be installed.
     * 
     * @parameter default-value="false" expression="${useRunkit}"
     */
    private boolean useRunkit;

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
     * The php files to check.
     */
    private List<String> phpFiles = new ArrayList<String>();
    
    /**
     * Lint helper
     */
    private LintHelper lintHelper;

    /**
     * Returns if the PHP validation should be skipped.
     *
     * @return if the validation should be skipped
     */
    private boolean isIgnoreValidate() {
        return ignoreValidate;
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
        // trigger to automatically check for supported PHP version
        try {
            this.getPhpHelper().getPhpVersion();
        } catch (PhpException e) {
            throw new MojoExecutionException("PHP not usable", e);
        }

        if (isIgnoreValidate()) {
            getLog().info("Validation of php sources is disabled.");
        }
        if (!isIncludeInJar()) {
            getLog().info("Not including php sources in resulting output.");
        }
        
        getLog().info("Unpacking dependencies");
        
        try {
            // TODO Is this correct?!?
            if (!isIgnoreValidate()) {
                this.getPhpHelper().prepareCompileDependencies();
            }
            new PhpWalkHelper(this).goRecursiveAndCall(this.getSourceFolder());
        } catch (MultiException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        } catch (PhpException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
        
        if (this.useRunkit) {
            // see http://php.net/manual/de/function.runkit-lint.php
            performLintWithRunkit();
        }
        else {
            boolean hadFailure = false;
            for (final LintExecution failure : this.lintHelper.waitAndReturnFailures()) {
                getLog().info("Lint check failure for " + failure.getFile(), failure.getException());
                hadFailure = true;
            }
            if (hadFailure) {
                throw new MojoExecutionException("Lint check failures.");
            }
        }
    }

    /**
     * Performs the lint check by using the runkit extension.
     * 
     * @throws MojoExecutionException mojo execution exception
     */
    private void performLintWithRunkit() throws MojoExecutionException {
        final StringBuffer fileContent = new StringBuffer(
                "<?php \n" +
                "$files = array(");
        for (final String file : this.phpFiles) {
            fileContent.append("\n    '").append(file.replace("\\", "\\\\")).append("',");
        }
        // trim last comma
        fileContent.setLength(fileContent.length() - 1);
        fileContent.append("\n" +
                ");\n" +
                "foreach ($files as $file) {" +
                "    echo \"Syntax check for $file\".PHP_EOL;\n" +
                "    if (!runkit_lint_file($file)) {\n" +
                "        // force include so that we display the error\n" +
                "        require $file;\n" +
                "        die('Parse error: '.$file);\n" +
                "    }\n" +
                "}");
        try {
            getLog().debug("Validating all files using runkit");
            try {
                this.getPhpHelper().executeCode(null, fileContent.toString());
            } catch (PhpErrorException e) {
                // try to extract the parse error message
                final String message = e.getMessage();
                final int pos = message.indexOf("Parse error: ");
                if (pos != -1) {
                    throw new MojoExecutionException(
                            "syntax check failure: \n"
                            + message.substring(pos));
                }
                throw e;
            }
        } catch (PhpException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    /**
     * Helper to walk through php resources.
     */
    private final class PhpWalkHelper extends AbstractPhpWalkHelper {
        private PhpWalkHelper(IPhpWalkConfigurationMojo config) {
            super(config);
            if (!useRunkit) {
                lintHelper = new LintHelper(getLog(), getPhpHelper());
            }
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
            
            if (useRunkit) {
                phpFiles.add(file.getAbsolutePath());
                return;
            }
            
            lintHelper.addFile(file);
        }
    }
    
}
