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

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.phpmaven.core.BuildPluginConfiguration;
import org.phpmaven.httpd.control.IApacheConfig;
import org.phpmaven.httpd.control.IApacheService.APACHE_VERSION;

/**
 * Abstract config tool for apache versions 2.0 - 2.4.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.1
 */
@Component(role = IApacheConfig.class, instantiationStrategy = "per-lookup", hint = "V2.2")
@BuildPluginConfiguration(groupId = "org.phpmaven", artifactId = "maven-httpd-control-api")
public class ConfigTool22 extends AbstractConfigTool {

    @Override
    public APACHE_VERSION getVersion() throws CommandLineException {
        return APACHE_VERSION.VERSION_2_2;
    }
    
}
