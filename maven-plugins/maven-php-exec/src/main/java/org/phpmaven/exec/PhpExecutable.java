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

package org.phpmaven.exec;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;

import com.google.common.base.Preconditions;

/**
 * Implementation of a php executable.
 * 
 * 
 * @author Christian Wiedemann
 * @author Tobias Sarnowski
 * @author Martin Eisengardt
 * @since 2.0.0
 */
@Component(role = IPhpExecutable.class, hint = "PHP_EXE" , instantiationStrategy = "per-lookup")
public final class PhpExecutable implements IPhpExecutable {

    
    /**
     * Parameter to let PHP print out its version.
     */
    private static final String PHP_FLAG_VERSION = "-v";

    /**
     * Parameter to specify the include paths for PHP.
     */
    private static final String PHP_FLAG_INCLUDES = "-d include_path";

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
     * The used PHP version (cached after initial call of {@link #getPhpVersion()}.
     */
    private PhpVersion phpVersion;
    
    /**
     * The used PHP version (cached after initial call of {@link #getPhpVersion()}.
     */
    private String strPhpVersion;
    
    /**
     * The log to be used for logging php output.
     */
    private Log log;

    /**
     * Environment variables.
     */
    private Map<String, String> env;

    /**
     * The include path.
     */
    private List<String> includePath;

    /**
     * The php defines.
     */
    private Map<String, String> phpDefines;

    /**
     * true if this executable is already configured.
     */
    private boolean configured;

    private File workDirectory;

    private int errorReporting;

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
     * {@inheritDoc}
     */
    @Override
    public int execute(String arguments, StreamConsumer stdout, StreamConsumer stderr) throws PhpException {
        Preconditions.checkNotNull(arguments, "Arguments");
        Preconditions.checkNotNull(stdout, "stdout");
        Preconditions.checkNotNull(stderr, "stderr");

        String command;
        if (this.additionalPhpParameters != null) {
            command = phpExecutable + " " + this.additionalPhpParameters;
        } else {
            command = phpExecutable;
        }
        for (final Map.Entry<String, String> phpDefine : this.phpDefines.entrySet()) {
            command += " -d ";
            command += phpDefine.getKey() + "=\"" + phpDefine.getValue() + "\"";
        }
        
        if (this.errorReporting != -1) {
            command += " -d error_reporting=" + this.errorReporting;
        }
        
        if (this.includePath.size() > 0) {
            final String[] includePaths = this.includePath.toArray(new String[this.includePath.size()]);
            command += " " + this.includePathParameter(includePaths);
        }
        
        command += " " + arguments;

        final Commandline commandLine = new Commandline(command);
        for (final Map.Entry<String, String> envVar : this.env.entrySet()) {
            commandLine.addEnvironment(envVar.getKey(), envVar.getValue());
        }
        
        if (this.workDirectory != null) {
            if (!this.workDirectory.exists()) {
                this.workDirectory.mkdirs();
            }
            commandLine.setWorkingDirectory(this.workDirectory);
        }

        try {
            this.log.debug("Executing " + commandLine);
            return CommandLineUtils.executeCommandLine(commandLine, stdout, stderr);
        } catch (CommandLineException e) {
            throw new PhpCoreException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
                        PhpExecutable.this.log.info("php.out: " + line);
                    } else {
                        PhpExecutable.this.log.debug("php.out: " + line);
                    }

                    stdout.consumeLine(line);

                    final boolean error = isError(line);
                    final boolean warning = isWarning(line);
                    if (error || warning) {
                        if (PhpExecutable.this.ignoreIncludeErrors
                                && !line.contains("require_once(")
                                && !line.contains("include_once(")
                                && !line.contains("require(")
                                && !line.contains("include(")
                            ) {
                            stderr.append(line);
                            stderr.append("\n");
                            if (error) throwError.set(true);
                            if (warning) throwWarning.set(true);
                        } else if (!PhpExecutable.this.ignoreIncludeErrors) {
                            stderr.append(line);
                            stderr.append("\n");
                            if (error) throwError.set(true);
                            if (warning) throwWarning.set(true);
                        }
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
     * {@inheritDoc}
     */
    @Override
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
     * Generates a string which can be used as a parameter for the PHP
     * executable defining the include paths to use.
     *
     * @param paths a list of paths
     * @return the complete parameter for PHP
     */
    private String includePathParameter(String[] paths) {
        final StringBuilder strIncludePath = new StringBuilder();
        strIncludePath.append(PHP_FLAG_INCLUDES);
        strIncludePath.append("=\"");
        for (String path : paths) {
            strIncludePath.append(File.pathSeparator);
            strIncludePath.append(path);
        }
        strIncludePath.append("\"");
        return strIncludePath.toString();
    }

    /**
     * Retrieves the used PHP version.
     * @throws PhpException is the php version is not resolvable or supported
     */
    private void getVersionEx() throws PhpException {

        // already found out?
        if (phpVersion != null) {
            return;
        }

        // execute PHP
        execute(PHP_FLAG_VERSION,
            (File) null,
            new StreamConsumer() {
                @Override
                public void consumeLine(String line) {
                    if (phpVersion == null && line.startsWith("PHP ")) {
                        strPhpVersion = line.substring(4, line.indexOf(" ", 4));
                        final String version = line.substring(4, 5);
                        if ("6".equals(version)) {
                            phpVersion = PhpVersion.PHP6;
                            PhpExecutable.this.log.warn("PHP6 is not supported yet!");
                        } else if ("5".equals(version)) {
                            phpVersion = PhpVersion.PHP5;
                        } else if ("4".equals(version)) {
                            phpVersion = PhpVersion.PHP4;
                            PhpExecutable.this.log.warn("PHP4 will not be supported anymore!");
                        } else {
                            phpVersion = PhpVersion.UNKNOWN;
                            PhpExecutable.this.log.error("Cannot find out PHP version: " + line);
                        }
                    }
                }
            }
        );
        
        if (phpVersion == null) {
            throw new PhpCoreException("Problems while evaluating php version.");
        }

        this.log.debug("PHP version: " + phpVersion.name());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String executeCode(String arguments, String code) throws PhpException {
        return this.executeCode(arguments, code, null);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String executeCode(String arguments, String code, String codeArguments) throws PhpException {
        final File snippet = this.temporaryScriptFile;
        if (!snippet.getParentFile().exists()) {
            snippet.getParentFile().mkdirs();
        }
        if (snippet.exists()) {
            snippet.delete();
        }
        
        try {
            final FileWriter w = new FileWriter(snippet);
            w.write("<?php \n" + code);
            w.close();
        } catch (IOException ex) {
            throw new PhpErrorException(snippet, "Error writing php temporary code snippet to file");
        }
        
        String command = "";
        
        if (arguments != null && arguments.length() > 0) {
            command += arguments + " ";
        }
        command += "\"" + snippet.getAbsolutePath() + "\"";
        if (codeArguments != null && codeArguments.length() > 0) {
            command += " " + codeArguments;
        }
        return this.execute(command, snippet);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void configure(IPhpExecutableConfiguration config, Log logger) {
        if (this.configured) {
            throw new IllegalStateException("Must not call this method twice. The executable is already configured.");
        }
        this.configured = true;
        this.additionalPhpParameters = config.getAdditionalPhpParameters();
        this.ignoreIncludeErrors = config.isIgnoreIncludeErrors();
        this.log = logger;
        this.logPhpOutput = config.isLogPhpOutput();
        this.phpExecutable = config.getExecutable();
        this.temporaryScriptFile = config.getTemporaryScriptFile();
        this.env = new HashMap<String, String>(config.getEnv());
        this.includePath = new ArrayList<String>(config.getIncludePath());
        this.phpDefines = new HashMap<String, String>(config.getPhpDefines());
        this.workDirectory = config.getWorkDirectory();
        this.errorReporting = config.getNumErrorReporting();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PhpVersion getVersion() throws PhpException {
        this.getVersionEx();
        return this.phpVersion;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStrVersion() throws PhpException {
        this.getVersionEx();
        return this.strPhpVersion;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String execute(File file) throws PhpException {
        return this.execute("\"" + file.getAbsolutePath() + "\"", file);
    }

}
