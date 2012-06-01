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

import java.util.ArrayList;
import java.util.List;

import org.phpmaven.httpd.control.IApacheConfigVHost;
import org.phpmaven.httpd.control.IApacheConfigVHostSite;

/**
 * Abstract config tool for apache versions 2.0 - 2.4.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.1
 */
class VHost extends ConfigFileLineDirective implements IApacheConfigVHost {

    /**
     * The tooling.
     */
    private AbstractConfigTool tool;

    /**
     * Constructor.
     */
    public VHost(String name, AbstractConfigTool tool) {
        super("NameVirtualHost");
        this.setValue(name);
        this.tool = tool;
    }

    @Override
    public String getName() {
        return this.getValue();
    }

    @Override
    public Iterable<IApacheConfigVHostSite> getSites() {
        final List<IApacheConfigVHostSite> result = new ArrayList<IApacheConfigVHostSite>();
        for (final IConfigFileLineSection section : tool.getDirectiveSections("VirtualHost")) {
            final VHostSite site = (VHostSite) section;
            if (this.getName().equals(site.getVHostName())) {
                result.add(site);
            }
        }
        return result;
    }

    @Override
    public IApacheConfigVHostSite declareSite() {
        final VHostSite site = new VHostSite(this.getName(), this.tool);
        this.tool.addSection(site);
        return site;
    }

    @Override
    public void removeSite(IApacheConfigVHost site) {
        this.tool.removeSection((VHostSite) site);
    }
    
}
