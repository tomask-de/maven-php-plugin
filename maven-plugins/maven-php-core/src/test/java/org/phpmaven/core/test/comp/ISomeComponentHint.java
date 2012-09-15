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

/**
 * Test component with some defaults.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public interface ISomeComponentHint {

    /**
     * Returns foo.
     * @return foo
     */
    String getFoo();

    /**
     * Sets foo.
     * @param foo foo
     */
    void setFoo(String foo);

    /**
     * Returns bar.
     * @return bar.
     */
    String getBar();

    /**
     * Sets bar.
     * @param bar bar
     */
    void setBar(String bar);

    /**
     * returns foobar.
     * @return foobar.
     */
    File getFooBar();

    /**
     * Sets foobar.
     * @param fooBar foobar
     */
    void setFooBar(File fooBar);
    
}
