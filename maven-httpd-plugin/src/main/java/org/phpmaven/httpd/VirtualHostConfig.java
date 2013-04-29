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

/**
 * Base abstract apache mojo with helpers for invoking apache ctl.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.1
 */
public class VirtualHostConfig {
    
    /**
     * Name of the virtual host (f.e. *:10080).
     */
    private String vhostName;
    
    /**
     * The config as found in the virtual host section.
     */
    private String config;
    
    /**
     * The server name.
     */
    private String serverName;
    
    /**
     * The document root.
     */
    private File documentRoot;

    public File getDocumentRoot() {
        return this.documentRoot;
    }

    public void setDocumentRoot(File documentRoot) {
        this.documentRoot = documentRoot;
    }

    public String getVhostName() {
        return vhostName;
    }

    public void setVhostName(String vhostName) {
        this.vhostName = vhostName;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
    
}
