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
package org.phpmaven.httpd.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.component.annotations.Configuration;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.phpmaven.core.ConfigurationParameter;
import org.phpmaven.core.ExecutionUtils;
import org.phpmaven.httpd.control.ConfigUtils;
import org.phpmaven.httpd.control.IApacheConfig;
import org.phpmaven.httpd.control.IApacheConfigPort;
import org.phpmaven.httpd.control.IApacheConfigVHost;

/**
 * Abstract config tool for apache versions 2.0 - 2.4.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.1
 */
abstract class AbstractConfigTool extends AbstractConfigCommon implements IApacheConfig {
    
    /**
     * Default value for parameter "defaultConfigFile".
     */
    private static final String DEFAULT_CONFIG_FILE = "conf/httpd.conf";
    
    /**
     * The apache executable.
     */
    @Configuration(name = "executable", value = "apache2ctl")
    @ConfigurationParameter(name = "executable", expression = "${apache.executable}")
    private String executable;
    
    /**
     * configuration file.
     */
    @ConfigurationParameter(name = "configFile", expression = "${project.basedir}/target/apache/httpd.conf")
    private File configFile;
    
    /**
     * server directory.
     */
    @Configuration(name = "defaultConfigFile", value = DEFAULT_CONFIG_FILE)
    @ConfigurationParameter(name = "defaultConfigFile", expression = "${apache.defaultConfig}")
    private String defaultConfigFile;

    @Override
    public File getConfigFile() {
        return this.configFile;
    }

    @Override
    public void setConfigFile(File config) {
        this.configFile = config;
    }

    @Override
    public void loadDefaultConfig(Log log) throws CommandLineException {
        File defaultCfg = null;
        if (DEFAULT_CONFIG_FILE.equals(defaultConfigFile)) {
            final String result = ExecutionUtils.executeCommand(log, "\"" + this.executable + "\" -V");
            defaultCfg = new File(result);
            if (defaultCfg.isAbsolute()) {
                if (defaultCfg.exists()) {
                    this.parseContent(ConfigUtils.readConfigFile(defaultCfg));
                    return;
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
                        this.parseContent(ConfigUtils.readConfigFile(defaultCfg));
                        return;
                    }
                }
            }
        } else {
            defaultCfg = new File(this.defaultConfigFile);
            if (defaultCfg.exists()) {
                this.parseContent(ConfigUtils.readConfigFile(defaultCfg));
                return;
            }
        }
        throw new CommandLineException("Error loading/finding default configuration file");
    }

    @Override
    public void loadConfigFile(Log log) throws CommandLineException {
        if (this.configFile.exists()) {
            this.parseContent(ConfigUtils.readConfigFile(this.configFile));
        } else {
            throw new CommandLineException("Error loading/finding configuration file at " + this.configFile);
        }
    }

    @Override
    public void write() {
        if (!this.configFile.getParentFile().exists()) {
            this.configFile.getParentFile().mkdirs();
        }
        try {
            final FileWriter writer = new FileWriter(this.configFile);
            writer.write(this.getContents());
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            throw new IllegalStateException("Error writing config file", ex);
        }
    }

    @Override
    public String getServerRoot() {
        return this.getSingleDirectiveValue("ServerRoot");
    }

    @Override
    public void setServerRoot(String root) {
        this.overwriteSingleDirective("ServerRoot", root);
    }

    @Override
    public String getPidFile() {
        return this.getSingleDirectiveValue("PidFile");
    }

    @Override
    public void setPidFile(String file) {
        this.overwriteSingleDirective("PidFile", file);
    }

    @Override
    public String getUser() {
        return this.getSingleDirectiveValue("User");
    }

    @Override
    public void setUser(String user) {
        this.overwriteSingleDirective("User", user);
    }

    @Override
    public String getGroup() {
        return this.getSingleDirectiveValue("Group");
    }

    @Override
    public void setGroup(String group) {
        this.overwriteSingleDirective("Group", group);
    }

    @Override
    public Iterable<IApacheConfigVHost> getVirtualHosts() {
        final List<IApacheConfigVHost> vhosts = new ArrayList<IApacheConfigVHost>();
        for (final IConfigFileLineDirective line : this.getDirectives("NameVirtualHost")) {
            vhosts.add((IApacheConfigVHost) line);
        }
        return vhosts;
    }

    @Override
    public IApacheConfigVHost declareVirtualHost(String name) {
        final VHost host = new VHost(name, this);
        this.addDirective(host);
        return host;
    }

    @Override
    public IApacheConfigVHost getVirtualHost(String name) {
        for (final IConfigFileLineDirective line : this.getDirectives("NameVirtualHost")) {
            final IApacheConfigVHost vhost = (IApacheConfigVHost) line;
            if (vhost.getName().equals(name)) {
                return vhost;
            }
        }
        return null;
    }

    @Override
    public void removeVirtualHost(IApacheConfigVHost vhost) {
        this.removeDirective((IConfigFileLineDirective) vhost);
    }

    @Override
    public Iterable<IApacheConfigPort> getListeners() {
        final List<IApacheConfigPort> ports = new ArrayList<IApacheConfigPort>();
        for (final IConfigFileLineDirective directive : this.getDirectives("Listen")) {
            ports.add((IApacheConfigPort) directive);
        }
        return ports;
    }

    @Override
    public IApacheConfigPort declareListener(int port) {
        final Port portObject = new Port(port, this);
        this.addDirective(portObject);
        return portObject;
    }

    @Override
    public void removeListener(IApacheConfigPort listener) {
        this.removeDirective((Port) listener);
    }

    @Override
    protected AbstractConfigTool getTooling() {
        return this;
    }
    
}
