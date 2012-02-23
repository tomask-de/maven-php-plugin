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

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.logging.Log;

/**
 * A php executable configuration.
 * 
 * <p>This configuration is used to declare a php executable that PHP-Maven will use.
 * You can declare a path to an existing executable if it is not found via PATH variable.
 * Additional command line options, PHP.INI options and environment variables can be set.</p>
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
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
     * @param log The logger.
     * 
     * @return php executable.
     */
    IPhpExecutable getPhpExecutable(Log log);

}
