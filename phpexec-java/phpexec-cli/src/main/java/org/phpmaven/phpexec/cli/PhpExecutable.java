/**
 * Copyright 2010-2012 by PHP-maven.org
 *
 * This file is part of phpexec-java.
 *
 * phpexec-java is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * phpexec-java is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with phpexec-java.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.phpmaven.phpexec.cli;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;
import org.phpmaven.phpexec.library.IPhpExecutable;
import org.phpmaven.phpexec.library.IPhpExecutableConfiguration;
import org.phpmaven.phpexec.library.PhpCoreException;
import org.phpmaven.phpexec.library.PhpErrorException;
import org.phpmaven.phpexec.library.PhpException;
import org.phpmaven.phpexec.library.PhpVersion;
import org.phpmaven.phpexec.library.PhpWarningException;

/**
 * Implementation of a php executable.
 * 
 * 
 * @author Christian Wiedemann
 * @author Tobias Sarnowski
 * @author Martin Eisengardt
 * @since 0.1.8
 */
public final class PhpExecutable implements IPhpExecutable {

	/**
	 * The logger
	 */
	private static final Logger LOGGER = Logger.getLogger(PhpExecutable.class.getName());
    
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

	private boolean isSilent;

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
    	if (arguments == null) throw new NullPointerException("Arguments");
    	if (stdout == null) throw new NullPointerException("stdout");
    	if (stderr == null) throw new NullPointerException("stderr");

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
        	if (!this.isSilent) {
        		LOGGER.fine("Executing " + commandLine);
        	}
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
                    	LOGGER.info("php.out: " + line);
                    } else {
                    	LOGGER.fine("php.out: " + line);
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
                        } else if ("5".equals(version)) {
                            phpVersion = PhpVersion.PHP5;
                        } else if ("4".equals(version)) {
                            phpVersion = PhpVersion.PHP4;
                        } else {
                            phpVersion = PhpVersion.UNKNOWN;
                        }
                    }
                }
            }
        );
        
        if (phpVersion == null) {
            throw new PhpCoreException("Problems while evaluating php version.");
        }

        LOGGER.fine("PHP version: " + phpVersion.name());
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

    protected void configure(IPhpExecutableConfiguration config) {
        if (this.configured) {
            throw new IllegalStateException("Must not call this method twice. The executable is already configured.");
        }
        this.configured = true;
        this.additionalPhpParameters = config.getAdditionalPhpParameters();
        this.ignoreIncludeErrors = config.isIgnoreIncludeErrors();
        this.logPhpOutput = config.isLogPhpOutput();
        this.phpExecutable = config.getExecutable();
        this.temporaryScriptFile = config.getTemporaryScriptFile().getAbsoluteFile();
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

	@Override
	public void setSilent(boolean silent) {
		this.isSilent = silent;
	}

	@Override
	public int execute(String arguments, File file, final OutputStream stdout)
			throws PhpException {
		return this.execute(arguments, file, new StreamConsumer() {
			
			@Override
			public void consumeLine(String arg0) {
				try {
					stdout.write(arg0.getBytes());
					stdout.write("\n".getBytes());
				} catch (IOException ex) {
					throw new IllegalStateException("IOException while consuming stdout", ex);
				}
			}
		});
	}

	@Override
	public int execute(String arguments, final OutputStream stdout,
			final OutputStream stderr) throws PhpException {
		return this.execute(arguments, new StreamConsumer() {
			
			@Override
			public void consumeLine(String arg0) {
				try {
					stdout.write(arg0.getBytes());
					stdout.write("\n".getBytes());
				} catch (IOException ex) {
					throw new IllegalStateException("IOException while consuming stdout", ex);
				}
			}
		}, new StreamConsumer() {
			
			@Override
			public void consumeLine(String arg0) {
				try {
					stderr.write(arg0.getBytes());
					stderr.write("\n".getBytes());
				} catch (IOException ex) {
					throw new IllegalStateException("IOException while consuming stderr", ex);
				}
			}
		});
	}

}
