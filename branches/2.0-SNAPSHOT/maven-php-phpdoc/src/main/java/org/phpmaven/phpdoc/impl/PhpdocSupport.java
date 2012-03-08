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

package org.phpmaven.phpdoc.impl;

import org.codehaus.plexus.component.annotations.Component;
import org.phpmaven.phpdoc.IPhpdocRequest;
import org.phpmaven.phpdoc.IPhpdocSupport;

/**
 * Implementation of phpdoc support invoking the phpdoc batch.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
@Component(role = IPhpdocSupport.class, instantiationStrategy = "per-lookup", hint = "PHP_EXE")
public class PhpdocSupport implements IPhpdocSupport {

    /**
     * {@inheritDoc}
     */
    @Override
    public void generateReport(IPhpdocRequest request) {
        // TODO Auto-generated method stub

    }

}
