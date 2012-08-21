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
package org.phpmaven.httpd.control.w32;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Configuration;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.phpmaven.core.BuildPluginConfiguration;
import org.phpmaven.core.ConfigurationParameter;
import org.phpmaven.core.ExecutionUtils;
import org.phpmaven.httpd.control.ConfigUtils;
import org.phpmaven.httpd.control.IApacheController;
import org.phpmaven.httpd.control.IApacheService.APACHE_VERSION;

/**
 * A helper interface for accessing apache services.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.1
 */
@Component(role = IApacheController.class, instantiationStrategy = "per-lookup", hint = "V2.0")
@BuildPluginConfiguration(groupId = "org.phpmaven", artifactId = "maven-httpd-control-api")
public class W32Controller20 implements IApacheController {
    
    /**
     * Default value for parameter "defaultConfigFile".
     */
    private static final String DEFAULT_CONFIG_FILE = "conf/httpd.conf";
    
    /**
     * The apache executable.
     */
    @Configuration(name = "executable", value = "httpd.exe")
    @ConfigurationParameter(name = "executable", expression = "${apache.executable}")
    private String executable;
    
    /**
     * configuration file.
     */
    @ConfigurationParameter(name = "configFile", expression = "${project.build.directory}/apache/httpd.conf")
    private File configFile;
    
    /**
     * server directory.
     */
    @ConfigurationParameter(name = "serverDir", expression = "${project.build.directory}/apache")
    private File serverDir;
    
    /**
     * pid File.
     */
    @ConfigurationParameter(name = "serverDir", expression = "${project.build.directory}/apache/apache2.pid")
    private File pidFile;
    
    /**
     * server directory.
     */
    @Configuration(name = "defaultConfigFile", value = DEFAULT_CONFIG_FILE)
    @ConfigurationParameter(name = "defaultConfigFile", expression = "${apache.defaultConfig}")
    private String defaultConfigFile;
    
    /**
     * The apache process.
     */
    private Process apacheProcess;

    @Override
    public File getServerDir() {
        return this.serverDir;
    }

    @Override
    public void setServerDir(File dir) {
        this.serverDir = dir;
    }

    @Override
    public File getConfigFile() {
        return this.configFile;
    }

    @Override
    public void setConfigFile(File config) {
        this.configFile = config;
    }

    @Override
    public String getExecutable() {
        return this.executable;
    }

    @Override
    public String getDefaultConfig(Log log) throws CommandLineException {
        // look for the configuration file
        File defaultCfg = null;
        if (DEFAULT_CONFIG_FILE.equals(defaultConfigFile)) {
            final String conf = ExecutionUtils.executeCommand(log, "\"" + this.executable + "\" -V SERVER_CONFIG_FILE");
            final Pattern pattern = Pattern.compile("^\\s*-D\\s*SERVER_CONFIG_FILE=\"(.*)?\"$");
            final Matcher matcher = pattern.matcher(conf);
            final String result = matcher.group(1);
            defaultCfg = new File(result);
            if (defaultCfg.isAbsolute()) {
                if (defaultCfg.exists()) {
                    return ConfigUtils.readConfigFile(defaultCfg);
                }
            } else {
                final String exec = ExecutionUtils.searchExecutable(log, this.executable);
                if (exec != null) {
                    final File execFile = new File(exec);
                    File execDir = execFile.getParentFile();
                    if ("bin".equals(execDir.getName())) {
                        execDir = execDir.getParentFile();
                    }
                    File confDir = new File(execDir, "conf");
                    if (!confDir.exists()) {
                        confDir = execDir;
                    }
                    defaultCfg = new File(confDir, "httpd.conf");
                    if (defaultCfg.exists()) {
                        return ConfigUtils.readConfigFile(defaultCfg);
                    }
                }
            }
        } else {
            defaultCfg = new File(this.defaultConfigFile);
            if (defaultCfg.exists()) {
                return ConfigUtils.readConfigFile(defaultCfg);
            }
        }
        return null;
    }

    @Override
    public APACHE_VERSION getVersion() throws CommandLineException {
        return APACHE_VERSION.VERSION_2_0;
    }

    @Override
    public boolean isActive() throws CommandLineException {
        return this.isDaemonActive();
    }

    @Override
    public boolean isDaemonActive() throws CommandLineException {
        if (this.pidFile.exists()) {
            try {
                final String pid = FileUtils.fileRead(this.pidFile).trim();
                final String ps = ExecutionUtils.executeCommand(null, "tasklist");
                final Pattern pattern = Pattern.compile("^\\S+\\s+" + pid + ".*$");
                if (pattern.matcher(ps).matches()) {
                    return true;
                }
            } catch (IOException ex) {
                throw new CommandLineException("Error testing daemon", ex);
            }
        }
        return false;
    }

    @Override
    public void start() throws CommandLineException {
        this.startDaemon();
        // simply endless loop, await the process shutting down
        try {
            this.apacheProcess.waitFor();
        } catch (InterruptedException e) {
            throw new CommandLineException("Problems joining apache process", e);
        }
    }

    @Override
    public void startDaemon() throws CommandLineException {
        synchronized (this) {
            if (this.isDaemonActive()) {
                throw new CommandLineException("Apache already running");
            }
            if (!this.serverDir.exists()) {
                this.serverDir.mkdirs();
            }
            final ProcessBuilder builder = new ProcessBuilder(
                    this.executable,
                    "-f", this.configFile.getAbsolutePath(),
                    "-d", this.serverDir.getAbsolutePath(),
                    "-c", "PidFile \"" + this.pidFile.getAbsolutePath() + "\"",
                    "-k", "start");
            builder.directory(this.serverDir);
            try {
                this.apacheProcess = builder.start();
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    public void run() {
                        try {
                            W32Controller20.this.stop();
                        } catch (CommandLineException ex) {
                            // in shutdown hooks there may be some problem.
                            ex.printStackTrace();
                        }
                    }
                });
            } catch (IOException ex) {
                throw new CommandLineException("Error starting apache", ex);
            }
        }
    }

    @Override
    public void stop() throws CommandLineException {
        this.stopDaemon();
    }

    @Override
    public void stopDaemon() throws CommandLineException {
        synchronized (this) {
            if (!this.isDaemonActive()) {
                throw new CommandLineException("Apache not running");
            }
            ExecutionUtils.executeCommand(null,
                    "\"" + this.executable + "\" " +
                    "-f \"" + this.configFile.getAbsolutePath() + "\" " +
                    "-d \"" + this.serverDir.getAbsolutePath() + "\" " +
                    "-c \"PidFile \\\"" + this.pidFile.getAbsolutePath() + "\\\"\" " +
                    "-k stop");
        }
    }

    @Override
    public void restart() throws CommandLineException {
        synchronized (this) {
            if (!this.isDaemonActive()) {
                this.start();
            } else {
                final ProcessBuilder builder = new ProcessBuilder(
                        this.executable,
                        "-f", this.configFile.getAbsolutePath(),
                        "-d", this.serverDir.getAbsolutePath(),
                        "-c", "PidFile \"" + this.pidFile.getAbsolutePath() + "\"",
                        "-k", "restart");
                builder.directory(this.serverDir);
                try {
                    this.apacheProcess = builder.start();
                    Runtime.getRuntime().addShutdownHook(new Thread() {
                        public void run() {
                            try {
                                W32Controller20.this.stop();
                            } catch (CommandLineException ex) {
                                // in shutdown hooks there may be some problem.
                                ex.printStackTrace();
                            }
                        }
                    });
                } catch (IOException ex) {
                    throw new CommandLineException("Error starting apache", ex);
                }
                // simply endless loop, await the process shutting down
                try {
                    this.apacheProcess.waitFor();
                } catch (InterruptedException e) {
                    throw new CommandLineException("Problems joining apache process", e);
                }
            }
        }
    }

    @Override
    public void restartDaemon() throws CommandLineException {
        synchronized (this) {
            if (!this.isDaemonActive()) {
                this.startDaemon();
            } else {
                ExecutionUtils.executeCommand(null,
                        "\"" + this.executable + "\" " +
                        "-f \"" + this.configFile.getAbsolutePath() + "\" " +
                        "-d \"" + this.serverDir.getAbsolutePath() + "\" " +
                        "-c \"PidFile \\\"" + this.pidFile.getAbsolutePath() + "\\\"\" " +
                        "-k restart");
            }
        }
    }

}
