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

package org.phpmaven.phpexec.library;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * A php executable configuration.
 * 
 * <p>
 * This configuration is used to declare a php executable that PHP-Maven will use.
 * You can declare a path to an existing executable if it is not found via PATH variable.
 * Additional command line options, PHP.INI options and environment variables can be set.
 * </p>
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 0.1.6
 */
public interface IPhpExecutableConfiguration {

    /**
     * Returns the executable that will be used.
     * 
     * @return The php executable path and file name.
     */
    String getExecutable();

    /**
     * Sets the php executable to be used.
     * 
     * @param executable the php executable path and file name.
     */
    void setExecutable(String executable);

    /**
     * Returns true if the php information cache can be used.
     * 
     * @return true if the cache can be used.
     */
    boolean isUseCache();

    /**
     * Sets the flag to control the php information cache.
     * 
     * @param useCache true to use the cache.
     */
    void setUseCache(boolean useCache);

    /**
     * Returns the environment variables to be used.
     * @return Map with environment variables.
     */
    Map<String, String> getEnv();

    /**
     * Sets the environment variables.
     * @param env environment variables.
     */
    void setEnv(Map<String, String> env);

    /**
     * Returns the php defines to be used.
     * @return php defines.
     */
    Map<String, String> getPhpDefines();

    /**
     * Sets the php defines to be used.
     * @param phpDefines php defines.
     */
    void setPhpDefines(Map<String, String> phpDefines);

    /**
     * Returns the include path.
     * @return include path.
     */
    List<String> getIncludePath();

    /**
     * Sets the include path.
     * @param includePath include path.
     */
    void setIncludePath(List<String> includePath);
    
    /**
     * Returns additional php parameters.
     * @return additional php parameters.
     */
    String getAdditionalPhpParameters();

    /**
     * Sets additional php parameters.
     * @param additionalPhpParameters additional php parameters.
     */
    void setAdditionalPhpParameters(String additionalPhpParameters);

    /**
     * Returns the flag to ignore include errors.
     * @return flag to ignore include errors.
     */
    boolean isIgnoreIncludeErrors();

    /**
     * Sets the flag to ignore include errors.
     * @param ignoreIncludeErrors flag to ignore include errors.
     */
    void setIgnoreIncludeErrors(boolean ignoreIncludeErrors);

    /**
     * Returns true if php output will be logged.
     * @return true if php output will be logged.
     */
    boolean isLogPhpOutput();

    /**
     * Sets the flag if php output will be logged.
     * @param logPhpOutput flag if php output will be logged.
     */
    void setLogPhpOutput(boolean logPhpOutput);

    /**
     * Returns the file to place temporary scripts that will be executed.
     * @return file to place temporary scripts that will be executed.
     */
    File getTemporaryScriptFile();

    /**
     * Sets the file for temporary script executions.
     * @param temporaryScriptFile file for temporary scripts.
     */
    void setTemporaryScriptFile(File temporaryScriptFile);

    /**
     * Returns the php executable that uses the configuration.
     * 
     * <p>
     * Will not respect any changes that are done after invoking this method.
     * After doing a configuration change you must receive a new executable.
     * </p>
     * 
     * @return php executable.
     * 
     * @throws IllegalStateException thrown if there were any problems receiving the executable
     */
    IPhpExecutable getPhpExecutable();
    
    /**
     * The work directory.
     * @return work directory.
     */
    File getWorkDirectory();
    
    /**
     * Sets the work directory.
     * @param file work directory.
     */
    void setWorkDirectory(File file);
    
    /**
     * Returns the error reporting as string.
     * @return error reporting
     */
    String getErrorReporting();
    
    /**
     * Sets the error reporting as string.
     * @param errorReporting error reporting
     */
    void setErrorReporting(String errorReporting);
    
    /**
     * Returns the numeric error reporting.
     * @return numeric error reporting
     */
    int getNumErrorReporting();
    
    /**
     * Error reporting constants.
     */
    enum ERROR_REPORTING {
        /** E_ERROR {@see http://www.php.net/manual/en/errorfunc.constants.php}. */
        E_ERROR(1),
        /** E_WARNING {@see http://www.php.net/manual/en/errorfunc.constants.php}. */
        E_WARNING(2),
        /** E_PARSE {@see http://www.php.net/manual/en/errorfunc.constants.php}. */
        E_PARSE(4),
        /** E_NOTICE {@see http://www.php.net/manual/en/errorfunc.constants.php}. */
        E_NOTICE(8),
        /** E_CORE_ERROR {@see http://www.php.net/manual/en/errorfunc.constants.php}. */
        E_CORE_ERROR(16),
        /** E_CORE_WARNING {@see http://www.php.net/manual/en/errorfunc.constants.php}. */
        E_CORE_WARNING(32),
        /** E_COMPILE_ERROR {@see http://www.php.net/manual/en/errorfunc.constants.php}. */
        E_COMPILE_ERROR(64),
        /** E_COMPILE_WARNING {@see http://www.php.net/manual/en/errorfunc.constants.php}. */
        E_COMPILE_WARNING(128),
        /** E_USER_ERROR {@see http://www.php.net/manual/en/errorfunc.constants.php}. */
        E_USER_ERROR(256),
        /** E_USER_WARNING {@see http://www.php.net/manual/en/errorfunc.constants.php}. */
        E_USER_WARNING(512),
        /** E_USER_NOTICE {@see http://www.php.net/manual/en/errorfunc.constants.php}. */
        E_USER_NOTICE(1024),
        /** E_STRICT {@see http://www.php.net/manual/en/errorfunc.constants.php}. */
        E_STRICT(2048),
        /** E_RECOVERABLE_ERROR {@see http://www.php.net/manual/en/errorfunc.constants.php}. */
        E_RECOVERABLE_ERROR(4096),
        /** E_DEPRECATED {@see http://www.php.net/manual/en/errorfunc.constants.php}. */
        E_DEPRECATED(8192),
        /** E_USER_DEPRECATED {@see http://www.php.net/manual/en/errorfunc.constants.php}. */
        E_USER_DEPRECATED(16384),
        /** E_ALL {@see http://www.php.net/manual/en/errorfunc.constants.php}. */
        E_ALL(32767);
        
        /**
         * Numeric value.
         */
        private int num;
        
        /**
         * Constructor.
         * @param num
         */
        private ERROR_REPORTING(int num) {
            this.num = num;
        }
        
        /**
         * Returns the number.
         * @return numeric value of the error reporting constant
         */
        public int getNumericValue() {
            return this.num;
        }
    }

}
