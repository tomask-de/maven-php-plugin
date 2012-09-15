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

import org.phpmaven.httpd.control.IApacheConfigVHostSite;

/**
 * Abstract config tool for apache versions 2.0 - 2.4.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.1
 */
class VHostSite extends AbstractConfigCommon implements IApacheConfigVHostSite, IConfigFileLineSection {

    /**
     * config file name.
     */
    private String name;
    
    /**
     * The tooling.
     */
    private AbstractConfigTool tool;

    /**
     * Constructor.
     * @param name
     * @param tool
     */
    public VHostSite(String name, AbstractConfigTool tool) {
        this.name = name;
        this.tool = tool;
    }

    @Override
    public boolean isSslEnabled() {
        final String value = this.getSingleDirectiveValue("SSLEngine");
        return "on".equalsIgnoreCase(value);
    }

    /**
     * Sets the virtual host name.
     * @param newVHost vhost name.
     */
    public void setVHostName(String newVHost) {
        this.name = newVHost;
    }
    
    /**
     * Returns the name of the vhost.
     * @return vhost name.
     */
    public String getVHostName() {
        return this.name;
    }

    @Override
    public String getSectionName() {
        return "VirtualHost";
    }
    
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append("<VirtualHost ");
        buffer.append(this.name);
        buffer.append("\">\n");
        buffer.append(super.toString());
        buffer.append("</VirtualHost>\n");
        return buffer.toString();
    }

    @Override
    protected AbstractConfigTool getTooling() {
        return this.tool;
    }
    
}
