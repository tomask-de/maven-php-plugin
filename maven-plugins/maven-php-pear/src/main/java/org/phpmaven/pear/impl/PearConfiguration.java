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

package org.phpmaven.pear.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.settings.Proxy;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Configuration;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.phpmaven.core.BuildPluginConfiguration;
import org.phpmaven.core.ConfigurationParameter;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.exec.IPhpExecutableConfiguration;
import org.phpmaven.pear.IMavenPearUtility;
import org.phpmaven.pear.IPearConfiguration;
import org.phpmaven.pear.library.IPearProxy;

/**
 * The pear configuration implementation.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
@Component(role = IPearConfiguration.class, instantiationStrategy = "per-lookup")
@BuildPluginConfiguration(groupId = "org.phpmaven", artifactId = "maven-php-pear")
public class PearConfiguration implements IPearConfiguration {
    
    /**
	 * 
	 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
	 * @since 2.0.3
	 */
	private static final class ProxyWrapper implements IPearProxy {
		/**
		 * 
		 */
		private final Proxy proxy;

		/**
		 * @param proxy
		 */
		private ProxyWrapper(Proxy proxy) {
			this.proxy = proxy;
		}

		@Override
		public boolean isActive() {
			return this.proxy.isActive();
		}

		@Override
		public String getUsername() {
			return this.proxy.getUsername();
		}

		@Override
		public String getProtocol() {
			return this.proxy.getProtocol();
		}

		@Override
		public int getPort() {
			return this.proxy.getPort();
		}

		@Override
		public String getPassword() {
			return this.proxy.getPassword();
		}

		@Override
		public String getHost() {
			return this.proxy.getHost();
		}
	}

	/**
     * The pear utility implementation to be used.
     */
    @Configuration(name = "pearUtility", value = "PHP_EXE")
    private String pearUtility;
    
    /**
     * The pear installation directory.
     */
    @ConfigurationParameter(name = "installDir", expression = "${project.build.directory}/pear")
    private File installDir;
    
    /**
     * The maven session.
     */
    @ConfigurationParameter(name = "session", expression = "${session}")
    private MavenSession session;
    
    /**
     * The executable config.
     */
    private Xpp3Dom executableConfig;
    
    /**
     * The configuration factory.
     */
    @Requirement
    private IComponentFactory factory;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getPearUtility() {
        return this.pearUtility;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPearUtility(String utility) {
        this.pearUtility = utility;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IMavenPearUtility getUtility(Log logger) throws PlexusConfigurationException,
            ComponentLookupException {
    	final IPhpExecutableConfiguration execConfig = this.factory.lookup(
    			IPhpExecutableConfiguration.class,
    			this.executableConfig,
    			this.session);
    	
    	final List<IPearProxy> proxies = new ArrayList<IPearProxy>();
    	for (final Proxy proxy : this.session.getSettings().getProxies()) {
    		proxies.add(new ProxyWrapper(proxy));
    	}
    	
        final IMavenPearUtility result = this.factory.lookup(
        				IMavenPearUtility.class,
                        this.pearUtility,
                        IComponentFactory.EMPTY_CONFIG,
                        this.session);
        result.configure(this.installDir, execConfig, proxies);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInstallDir(File installDir) {
        this.installDir = installDir;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getInstallDir() {
        return this.installDir;
    }

}
