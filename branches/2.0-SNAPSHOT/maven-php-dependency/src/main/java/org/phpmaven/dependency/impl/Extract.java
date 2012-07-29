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

package org.phpmaven.dependency.impl;

import org.codehaus.plexus.component.annotations.Configuration;
import org.phpmaven.core.ConfigurationParameter;
import org.phpmaven.dependency.IActionExtract;

/**
 * Extract action
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.1
 */
public class Extract implements IActionExtract {
    
    /**
     * The phar local path id.
     */
    @Configuration(name = "pharPath", value = "/")
    private String pharPath = "/";
    
    /**
     * The target path.
     */
    @Configuration(name = "targetPath", value = "")
    private String targetPath = "";
    
    @Override
    public ActionType getType() {
        return ActionType.ACTION_EXTRACT;
    }

    @Override
    public String getPharPath() {
        return this.pharPath;
    }

    @Override
    public String getTargetPath() {
        return this.targetPath;
    }
    
}
