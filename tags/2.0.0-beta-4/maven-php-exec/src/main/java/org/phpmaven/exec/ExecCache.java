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

package org.phpmaven.exec;

import java.util.HashMap;
import java.util.Map;

import org.apache.maven.plugin.logging.Log;

/**
 * A simple cache to re-use information of php executables.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
final class ExecCache {
    
    /**
     * Singleton instance.
     */
    private static final ExecCache INSTANCE = new ExecCache();
    
    /**
     * Cache of php executables depending on the path and name of the executable file.
     */
    private Map<String, IPhpExecutable> cache = new HashMap<String, IPhpExecutable>();
    
    /**
     * Hidden constructor (singleton).
     */
    private ExecCache() {
        // empty
    }
    
    /**
     * Returns the instance of this singleton.
     * 
     * @return instance singleton.
     */
    public static ExecCache instance() {
        return INSTANCE;
    }
    
    /**
     * Returns the executable from given filename.
     * @param config configuration.
     * @param log The logger.
     * @return the executable.
     */
    public IPhpExecutable get(PhpExecutableConfiguration config, Log log) {
        synchronized (this.cache) {
            if (this.cache.containsKey(config.getExecutable())) {
                return this.cache.get(config.getExecutable());
            }
            final IPhpExecutable result = new PhpExecutable();
            this.cache.put(config.getExecutable(), result);
            result.configure(config, log);
            return result;
        }
    }

}
