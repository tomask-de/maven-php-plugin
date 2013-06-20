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
import org.phpmaven.dependency.IActionInclude;

/**
 * Include action
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public class Include implements IActionInclude {
    
    /**
     * The phar local path id.
     */
    @Configuration(name = "pharPath", value = "/")
    private String pharPath = "/";
    
    @Override
    public ActionType getType() {
        return ActionType.ACTION_INCLUDE;
    }

    @Override
    public String getPharPath() {
        return this.pharPath;
    }
    
}
