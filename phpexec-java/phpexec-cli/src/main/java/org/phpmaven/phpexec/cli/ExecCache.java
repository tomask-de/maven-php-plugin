/**
 * Copyright 2010-2012 by PHP-maven.org
 *
 * This file is part of phpexec-java.
 *
 * phpexec-java is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * phpexec-java is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with phpexec-java.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.phpmaven.phpexec.cli;

import java.util.HashMap;
import java.util.Map;

import org.phpmaven.phpexec.library.IPhpExecutable;

/**
 * A simple cache to re-use information of php executables.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 0.1.8
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
     * @return the executable.
     */
    public IPhpExecutable get(PhpExecutableConfiguration config) {
        synchronized (this.cache) {
            if (this.cache.containsKey(config.getExecutable())) {
                return this.cache.get(config.getExecutable());
            }
            final PhpExecutable result = new PhpExecutable();
            this.cache.put(config.getExecutable(), result);
            result.configure(config);
            return result;
        }
    }

}
