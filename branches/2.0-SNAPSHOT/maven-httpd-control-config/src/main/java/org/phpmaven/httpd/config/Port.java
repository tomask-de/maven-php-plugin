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

import org.phpmaven.httpd.control.IApacheConfigPort;
import org.phpmaven.httpd.control.IApacheConfigVHost;
import org.phpmaven.httpd.control.IApacheConfigVHostSite;

/**
 * Abstract config tool for apache versions 2.0 - 2.4.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.1
 */
class Port extends ConfigFileLineDirective implements IApacheConfigPort {

    /**
     * the config tool.
     */
    private AbstractConfigTool tool;

    /**
     * Constructor.
     */
    public Port(int port, AbstractConfigTool tool) {
        super("Listen");
        this.setValue(String.valueOf(port));
        this.tool = tool;
    }

    @Override
    public int getPort() {
        return Integer.parseInt(this.getValue());
    }

    @Override
    public void redeclarePort(int newPort) {
        if (newPort != this.getPort()) {
            final String strNewPort = String.valueOf(newPort);
            for (final IApacheConfigVHost vh : this.tool.getVirtualHosts()) {
                final VHost vhost = (VHost) vh;
                if (vhost.getValue().split(":")[1].equals(this.getValue())) {
                    final String newVHost = vhost.getValue().split(":")[0] + ":" + strNewPort;
                    for (final IApacheConfigVHostSite vhs : vhost.getSites()) {
                        final VHostSite vhostsite = (VHostSite) vhs;
                        vhostsite.setVHostName(newVHost);
                    }
                    vhost.setValue(newVHost);
                }
            }
            this.setValue(strNewPort);
        }
    }

    @Override
    public boolean isSsl() {
        for (final IApacheConfigVHost vh : this.tool.getVirtualHosts()) {
            final VHost vhost = (VHost) vh;
            if (vhost.getValue().split(":")[1].equals(this.getValue())) {
                for (final IApacheConfigVHostSite vhs : vhost.getSites()) {
                    if (vhs.isSslEnabled()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    
}
