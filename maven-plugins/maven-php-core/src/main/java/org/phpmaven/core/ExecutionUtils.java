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
package org.phpmaven.core;

import java.io.File;

import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;

/**
 * A simple execution util to invoke cli commands.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.1
 */
public final class ExecutionUtils {
    
    /** windows suffixes. */
    private static final String[] WIN_SUFFIXES = new String[]{"exe", "cmd", "com", "bat"};
    
    /**
     * Hidden constructor.
     */
    private ExecutionUtils() {
        // hidden constructor
    }
    
    /**
     * Executes a command.
     * @param log the logger
     * @param command command line
     * @return result string.
     * @throws CommandLineException throw on execution errors.
     */
    public static String executeCommand(Log log, String command) throws CommandLineException {
        return executeCommand(log, command, null);
    }
    
    /**
     * Executes a command.
     * @param log the logger
     * @param command command line
     * @param workDir working directory
     * @return result string.
     * @throws CommandLineException throw on execution errors.
     */
    public static String executeCommand(Log log, String command, final File workDir) throws CommandLineException {
        final Commandline cli = new Commandline(command);
        if (log != null) {
            log.debug("Executing " + command);
        }
        
        if (workDir != null) {
            if (!workDir.exists()) {
                workDir.mkdirs();
            }
            cli.setWorkingDirectory(workDir);
        }
        
        final StringBuilder stdout = new StringBuilder();
        final StringBuilder stderr = new StringBuilder();
        final StreamConsumer systemOut = new StreamConsumer() {
                @Override
                public void consumeLine(String line) {
                    stdout.append(line);
                    stdout.append("\n");
                }
            };
        final StreamConsumer systemErr = new StreamConsumer() {
                @Override
                public void consumeLine(String line) {
                    stderr.append(line);
                    stderr.append("\n");
                }
            };
        try {
            final int result = CommandLineUtils.executeCommandLine(
                cli,
                systemOut,
                systemErr);
            if (result != 0) {
                if (log != null) {
                    log.warn("Error invoking command. Return code " + result +
                        "\n\nstd-out:\n" + stdout + "\n\nstd-err:\n" + stderr);
                }
                throw new CommandLineException("Error invoking command. Return code " + result);
            }
        } catch (CommandLineException ex) {
            if (log != null) {
                log.warn("Error invoking command\n\nstd-out:\n" + stdout + "\n\nstd-err:\n" + stderr);
            }
            throw ex;
        }
        if (log != null) {
            log.debug("stdout: " + stdout.toString());
        }
        return stdout.toString();
    }

    /**
     * Searches for the executable in system path.
     * @param log logging (maybe null)
     * @param executable executable name
     * @return executable path or null if it cannot be found
     */
    public static String searchExecutable(Log log, String executable) {
        return searchExecutable(log, executable,
                System.getProperty("java.library.path") + File.pathSeparator + System.getenv("PATH"));
    }

    /**
     * Searches for the executable in system path.
     * @param log logging (maybe null)
     * @param executable executable name
     * @return executable path or null if it cannot be found
     */
    public static String searchExecutable(Log log, String executable, String path) {
        if (log != null) {
            log.debug("searching for " + executable + " in PATH: " + path);
        }
        final String[] paths = path.split(File.pathSeparator);
        final File exec = new File(executable);
        if (exec.exists()) {
            return exec.getAbsolutePath();
        }
        
        for (int i = 0; i < paths.length; i++) {
            if (isWindows()) {
                for (final String suffix : WIN_SUFFIXES) {
                    final File file2 = new File(paths[i], executable + "." + suffix);
                    if (file2.isFile()) {
                        return file2.getAbsolutePath();
                    }
                }
            }
            final File file = new File(paths[i], executable);
            if (file.isFile()) {
                return file.getAbsolutePath();
            }
        }
        
        return null;
    }
    
    /**
     * Returns true if the operating system is windows.
     * @return true if this is windows.
     * @since 2.0.1
     */
    public static boolean isWindows() {
        final String os2 = System.getProperty("os.name");
        if (os2 != null && os2.toLowerCase().indexOf("windows") != -1) {
            return true;
        }
        return false;
    }

}
