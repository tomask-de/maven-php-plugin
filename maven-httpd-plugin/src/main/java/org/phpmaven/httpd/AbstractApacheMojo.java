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
package org.phpmaven.httpd;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.httpd.control.IApacheConfig;
import org.phpmaven.httpd.control.IApacheConfigDirectory;
import org.phpmaven.httpd.control.IApacheConfigPort;
import org.phpmaven.httpd.control.IApacheConfigVHost;
import org.phpmaven.httpd.control.IApacheConfigVHostSite;
import org.phpmaven.httpd.control.IApacheService;

/**
 * Base abstract apache mojo with helpers for invoking apache ctl.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.1
 */
public abstract class AbstractApacheMojo extends AbstractMojo {
    
    /**
     * List of include files to be appended to the apache configuration.
     * @parameter
     */
    private String appendConfig;
    
    /**
     * true to generate the configuration file. Should be set to false if there is a static httpd.conf to be used.
     * @parameter default-value="true" expression="${configGeneration}"
     */
    private boolean configGeneration;
    
    /**
     * The document root to be used. Defaults to ${project.build.outputDirectory}
     * @parameter default-value="${project.build.outputDirectory}" expression="${documentRoot}"
     */
    private File documentRoot;
    
    /**
     * The port number to be used.
     * @parameter default-value="10080" expression="${httpPort}"
     */
    private int httpPort;
    
    /**
     * The ssl port number to be used.
     * @parameter default-value="10443" expression="${sslPort}"
     */
    private int sslPort;
    
    /**
     * True to use ssl.
     * @parameter default-value="false" expression="${useSsl}"
     */
    private boolean useSsl;
    
    /**
     * True to remove all listeners for other ports from default config. Only respected if the configuration file is
     * generated.
     * @parameter default-value="true" expression="${configRemoveListeners}"
     */
    private boolean configRemoveListeners;
    
    /**
     * True to generate a default listen config with the ports.
     * @parameter default-value="true" expression="${generateListenConfig}"
     */
    private boolean generateListenConfig;
    
    /**
     * True to remove all virtual hosts from default config. Only respected if the configuration file is
     * generated.
     * @parameter default-value="true" expression="${configRemoveVHosts}"
     */
    private boolean configRemoveVHosts;
    
    /**
     * Additional configuration for the directory. If set it overwrites the complete per-directory configuration.
     * @parameter
     */
    private String directoryConfig;
    
    /**
     * True to generate a default directory config for the documentation root.
     * @parameter default-value="true" expression="${generateDirectoryConfig}"
     */
    private boolean generateDirectoryConfig;
    
    /**
     * Additional virtual host configuration.
     * @parameter
     */
    private List<VirtualHostConfig> vhost = new ArrayList<VirtualHostConfig>();
    
    /**
     * True to generate a default virtual host config for the documentation root.
     * @parameter default-value="true" expression="${generateVHostConfig}"
     */
    private boolean generateVHostConfig; 
    
    /**
     * The name of the server as used in the vhost configuration section.
     * @parameter default-value="localhost" expression="${serverName}"
     */
    private String serverName; 
    
    /**
     * The configuration factory.
     * @component
     * @required
     */
    private IComponentFactory factory;
    
    /**
     * The Maven project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;
    
    /**
     * The Maven session.
     *
     * @parameter expression="${session}"
     * @required
     * @readonly
     */
    private MavenSession session;
    
    /**
     * Returns the apache service to be used.
     * 
     * @return apache service.
     * @throws ComponentLookupException thrown on lookup errors.
     * @throws PlexusConfigurationException thrwn on configuration errors.
     */
    protected IApacheService getService() throws ComponentLookupException, PlexusConfigurationException {
        return this.factory.lookup(
                IApacheService.class,
                IComponentFactory.EMPTY_CONFIG,
                this.session);
    }
    
    /**
     * Returns the parsed configuration.
     * @param service the apache service to be used.
     * @return parsed configuration object.
     * @throws CommandLineException thrown on invocation errors.
     * @throws MojoExecutionException thrown on execution errors.
     * @throws ComponentLookupException thrown for factory problems.
     * @throws PlexusConfigurationException thrown for factory problems.
     */
    protected IApacheConfig getParsedConfig(IApacheService service)
        throws CommandLineException, MojoExecutionException,
               ComponentLookupException, PlexusConfigurationException {
        final IApacheConfig config = service.getConfigTool(this.getLog());
        if (this.configGeneration) {
            config.loadDefaultConfig(this.getLog());
            
            if (this.configRemoveListeners) {
                for (final IApacheConfigPort port : config.getListeners()) {
                    config.removeListener(port);
                }
            }
            if (this.configRemoveListeners || this.configRemoveVHosts) {
                for (final IApacheConfigVHost vh : config.getVirtualHosts()) {
                    config.removeVirtualHost(vh);
                }
            }
            if (this.generateListenConfig) {
                config.declareListener(this.httpPort);
                if (this.useSsl) {
                    config.declareListener(this.sslPort);
                }
            }
            
            if (this.generateDirectoryConfig) {
                final IApacheConfigDirectory dir = config.declareDirectory(this.documentRoot.getAbsolutePath());
                if (this.directoryConfig != null && this.directoryConfig.length() > 0) {
                    dir.setContents(this.directoryConfig);
                } else {
                    dir.setContents(
                        "AllowOverride All\n" +
                        "allow from all\n" +
                        "Options +Indexes\n");
                }
            }
            
            if (this.generateVHostConfig) {
                String vhostName = "*:" + this.httpPort;
                IApacheConfigVHost vh = config.getVirtualHost(vhostName);
                if (vh == null) {
                    vh = config.declareVirtualHost(vhostName);
                }
                IApacheConfigVHostSite site = vh.declareSite();
                site.setDocumentRoot(this.documentRoot.getAbsolutePath());
                site.setServerName(this.serverName);
                
                if (useSsl) {
                    vhostName = "*:" + this.sslPort;
                    vh = config.getVirtualHost(vhostName);
                    if (vh == null) {
                        vh = config.declareVirtualHost(vhostName);
                    }
                    site = vh.declareSite();
                    site.setDocumentRoot(this.documentRoot.getAbsolutePath());
                    site.setServerName(this.serverName);
                    site.append("SslEngine on");
                }
            }
            
            for (final VirtualHostConfig vhconfig : this.vhost) {
                final String vhostName = vhconfig.getVhostName();
                IApacheConfigVHost vh = config.getVirtualHost(vhostName);
                if (vh == null) {
                    vh = config.declareVirtualHost(vhostName);
                }
                final IApacheConfigVHostSite site = vh.declareSite();
                site.setDocumentRoot(vhconfig.getDocumentRoot().getAbsolutePath());
                site.setServerName(vhconfig.getServerName());
                site.append(vhconfig.getConfig());
            }
            
            config.setDocumentRoot(this.documentRoot.getAbsolutePath());
            if (this.appendConfig != null && this.appendConfig.length() > 0) {
                config.append(this.appendConfig);
            }
        } else {
            config.loadConfigFile(this.getLog());
        }
        return config;
    }

    protected File getDocumentRoot() {
        return this.documentRoot;
    }

    protected int getHttpPort() {
        return this.httpPort;
    }

    protected int getSslPort() {
        return this.sslPort;
    }

    protected boolean isUseSsl() {
        return this.useSsl;
    }

    protected String getServerName() {
        return this.serverName;
    }

    protected IComponentFactory getFactory() {
        return this.factory;
    }

    protected MavenProject getProject() {
        return this.project;
    }

    protected MavenSession getSession() {
        return this.session;
    }

}
