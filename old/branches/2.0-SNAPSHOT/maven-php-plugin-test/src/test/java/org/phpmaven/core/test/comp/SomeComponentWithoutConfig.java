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

package org.phpmaven.core.test.comp;

import java.io.File;

import org.codehaus.plexus.component.annotations.Component;


/**
 * A sample component.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.1
 */
@Component(role = ISomeComponentHint.class, hint = "without-config", instantiationStrategy = "per-lookup")
public class SomeComponentWithoutConfig implements ISomeComponentHint {
    
    /** foo. */
    private String foo;
    
    /** bar. */
    private String bar;

    /** foobar. */
    private File fooBar;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFoo() {
        return this.foo;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setFoo(String foo) {
        this.foo = foo;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getBar() {
        return this.bar;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setBar(String bar) {
        this.bar = bar;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public File getFooBar() {
        return this.fooBar;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setFooBar(File fooBar) {
        this.fooBar = fooBar;
    }
    
}
