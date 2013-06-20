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

package org.phpmaven.plugin.php;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.ProjectBuildingRequest;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.plugin.build.FileHelper;

import com.google.common.base.Preconditions;

/**
 * Helper class to execute PHP scripts and PHP commands. Will be used by various mojos.
 * 
 * @author Christian Wiedemann
 * @author Tobias Sarnowski
 * @author Martin Eisengardt
 * @author Erik Dannenberg
 */
public class PhpMojoHelper implements IPhpExecution {

    /**
     * This list describes all keywords which will be printed out by PHP
     * if an error occurs.
     */
    private static final String[] ERROR_IDENTIFIERS = new String[]{
        "Fatal error",
        "Error",
        "Parse error"
    };

    /**
     * This list describes all keywords which will be printed out by PHP
     * if a warrning occurs.
     */
    private static final String[] WARNING_IDENTIFIERS = new String[]{
        "Warning",
        "Notice"
    };

    /**
     * Path to the php executable.
     */
    private String phpExecutable;

    /**
     * PHP arguments. Use php -h to get a list of all php compile arguments.
     */
    private String additionalPhpParameters;

    /**
     * Where the php dependency files will be written to.
     */
    private File dependenciesTargetDirectory;

    /**
     * Where the php test dependency files will be written to.
     */
    private File testDependenciesTargetDirectory;

    /**
     * If true, errors triggered because of missing includes will be ignored.
     */
    private boolean ignoreIncludeErrors;

    /**
     * If the output of the php scripts will be written to the console.
     */
    private boolean logPhpOutput;
    
    /**
     * A temporary script file that can be used for php execution of small code snippets.
     */
    private File temporaryScriptFile;
    
    /**
     * The directory containing generated test classes of the project being tested. This will be included at the
     * beginning of the test classpath.
     */
    private File targetTestClassesDirectory;
    
    /**
     * The directory containing generated classes of the project being tested. This will be included after the test
     * classes in the test classpath.
     */
    private File targetClassesDirectory;
    
    /**
     * The log to be used for logging php output.
     */
    private Log log;

    /**
     * The Maven project.
     */
    private MavenProject project;
    
    /**
     * The maven project builder.
     */
    private ProjectBuilder mavenProjectBuilder;
    
    /**
     * The Maven session.
     */
    private MavenSession session;
    
    /**
     * Constructor to create the helper.
     * 
     * @param config The configuration aware mojo.
     */
    public PhpMojoHelper(IPhpConfigurationMojo config) {
        this.additionalPhpParameters = config.getAdditionalPhpParameters();
        this.dependenciesTargetDirectory = config.getDependenciesTargetDirectory();
        this.ignoreIncludeErrors = config.isIgnoreIncludeErrors();
        this.log = config.getLog();
        this.logPhpOutput = config.isLogPhpOutput();
        this.phpExecutable = config.getPhpExecutable();
        this.project = config.getProject();
        this.targetClassesDirectory = config.getTargetClassesDirectory();
        this.targetTestClassesDirectory = config.getTargetTestClassesDirectory();
        this.temporaryScriptFile = config.getTemporaryScriptFilename();
        this.testDependenciesTargetDirectory = config.getTestDependenciesTargetDirectory();
        this.mavenProjectBuilder = config.getMavenProjectBuilder();
        this.session = config.getSession();
    }

    /**
     * Checks if a line (string) contains a PHP error message.
     *
     * @param line output line
     * @return if the line contains php error messages
     */
    private boolean isError(String line) {
        final String trimmedLine = line.trim();
        for (String errorIdentifier : ERROR_IDENTIFIERS) {
            if (trimmedLine.startsWith(errorIdentifier + ":")
                || trimmedLine.startsWith("<b>" + errorIdentifier + "</b>:")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a line (string) contains a PHP warning message.
     *
     * @param line output line
     * @return if the line contains php warning messages
     */
    private boolean isWarning(String line) {
        final String trimmedLine = line.trim();
        for (String warningIdentifier : WARNING_IDENTIFIERS) {
            if (trimmedLine.startsWith(warningIdentifier + ":")
                || trimmedLine.startsWith("<b>" + warningIdentifier + "</b>:")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Executes PHP with the given arguments.
     *
     * @param arguments string of arguments for PHP
     * @param stdout handler for stdout lines
     * @param stderr handler for stderr lines
     * @return the return code of PHP
     * @throws PhpException if the executions fails
     */
    public int execute(String arguments, StreamConsumer stdout, StreamConsumer stderr) throws PhpException {
        Preconditions.checkNotNull(arguments, "Arguments");
        Preconditions.checkNotNull(stdout, "stdout");
        Preconditions.checkNotNull(stderr, "stderr");

        final String command;
        if (this.additionalPhpParameters != null) {
            command = phpExecutable + " " + this.additionalPhpParameters + " " + arguments;
        } else {
            command = phpExecutable + " " + arguments;
        }

        final Commandline commandLine = new Commandline(command);

        try {
            this.log.debug("Executing " + commandLine);
            return CommandLineUtils.executeCommandLine(commandLine, stdout, stderr);
        } catch (CommandLineException e) {
            throw new PhpCoreException(e);
        }
    }

    /**
     * Executes PHP with the given arguments and throws an IllegalStateException if the
     * execution fails.
     *
     * @param arguments string of arguments for PHP
     * @param file a hint which file will be processed
     * @param stdout handler for stdout lines
     * @return the returncode of PHP
     * @throws PhpException if the execution failed
     */
    public int execute(String arguments, File file, final StreamConsumer stdout) throws PhpException {
        final StringBuilder stderr = new StringBuilder();

        final AtomicBoolean throwError = new AtomicBoolean(false);
        final AtomicBoolean throwWarning = new AtomicBoolean(false);

        final int returnCode = execute(
            arguments,
            new StreamConsumer() {
                @Override
                public void consumeLine(String line) {
                    if (logPhpOutput) {
                        PhpMojoHelper.this.log.info("php.out: " + line);
                    } else {
                        PhpMojoHelper.this.log.debug("php.out: " + line);
                    }

                    stdout.consumeLine(line);

                    final boolean error = isError(line);
                    final boolean warning = isWarning(line);
                    if (error || warning) {
                        if (PhpMojoHelper.this.ignoreIncludeErrors
                            && !line.contains("require_once")
                            && !line.contains("include_once")
                            ) {
                            stderr.append(line);
                            stderr.append("\n");
                        } else if (!PhpMojoHelper.this.ignoreIncludeErrors) {
                            stderr.append(line);
                            stderr.append("\n");
                        }
                        if (error) throwError.set(true);
                        if (warning) throwWarning.set(true);
                    }
                }
            },
            new StreamConsumer() {
                @Override
                public void consumeLine(String line) {
                    stderr.append(line);
                    stderr.append("\n");
                    throwError.set(true);
                }
            }
        );
        final String error = stderr.toString();
        if (returnCode == 0 && !throwError.get() && !throwWarning.get()) {
            return returnCode;
        } else {
            String message = "Failed to execute PHP with arguments '" + arguments + "' [Return: " + returnCode + "]";
            if (error.length() > 0) {
                message = message + ":\n" + error;
            }

            if (throwWarning.get()) {
                throw new PhpWarningException(file, message);
            } else if (throwError.get()) {
                throw new PhpErrorException(file, message);
            } else {
                throw new PhpCoreException(message);
            }
        }
    }

    /**
     * Executes PHP with the given arguments and returns its output.
     *
     * @param arguments string of arguments for PHP
     * @param file a hint which file will be processed
     * @return the output string
     * @throws PhpException if the execution failed
     */
    public String execute(String arguments, File file) throws PhpException {
        final StringBuilder stdout = new StringBuilder();
        try {
            execute(arguments, file, new StreamConsumer() {
                @Override
                public void consumeLine(String line) {
                    stdout.append(line);
                    stdout.append("\n");
                }
            });
        } catch (PhpException e) {
            e.appendOutput(stdout.toString());
            throw e;
        }
        return stdout.toString();
    }
    
    /**
     * Returns the maven project from given artifact.
     * @param a artifact
     * @return maven project
     * @throws ProjectBuildingException thrown if there are problems creating the project
     */
    protected MavenProject getProjectFromArtifact(final Artifact a) throws ProjectBuildingException {
        final ProjectBuildingRequest request = session.getProjectBuildingRequest();
        request.setLocalRepository(session.getLocalRepository());
        request.setRemoteRepositories(this.project.getRemoteArtifactRepositories());
        request.setProcessPlugins(false);
        return this.mavenProjectBuilder.build(a, request).getProject();
    }

    /**
     * Unzips all dependency sources.
     * 
     * @param factory Component factory
     * @param session maven session
     * @param targetDir target directory
     * @param sourceScope dependency scope to unpack from
     *
     * @throws IOException if something goes wrong while prepareing the dependencies
     * @throws PhpException php exceptions can fly everywhere..
     */
    public void prepareDependencies(IComponentFactory factory, MavenSession session, File targetDir, String sourceScope)
            throws IOException, PhpException, MojoExecutionException {
        final List<String> packedElements = new ArrayList<String>();
        final Set<Artifact> deps = this.project.getArtifacts();
        for (final Artifact dep : deps) {
            if (!sourceScope.equals(dep.getScope())) {
                continue;
            }
            this.log.info(dep.getFile().getAbsolutePath());
            try {
                if (this.getProjectFromArtifact(dep).getFile() != null) {
                    // Reference to a local project; should only happen in IDEs or multi-project-poms
                    this.log.debug("Dependency resolved to a local project. skipping.");
                    // the maven-php-project plugin will fix it by adding the include paths
                    continue;
                }
            } catch (ProjectBuildingException ex) {
                throw new IOException("Problems creating maven project from dependency", ex);
            }
            packedElements.add(dep.getFile().getAbsolutePath());
        }
        FileHelper.unzipElements(this.log, targetDir, packedElements, factory, session);
    }

}
