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
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.phpmaven.core.BuildPluginConfiguration;
import org.phpmaven.core.IComponentFactory;

/**
 * A php executable configuration.
 * 
 * <p>
 * This configuration is used to declare a php executable that PHP-Maven will use.
 * You can declare a path to an existing executable if it is not found via PATH variable.
 * Additional command line options, PHP.INI options and environment variables can be set.
 * </p>
 * 
 * <p>
 * Create an instance via {@link IComponentFactory}.
 * </p>
 * 
 * <p>
 * Configuration of the php executable can be done via either the goal you are executing
 * or via plugin configuration. Example of a configuration via build plugin:<br />
 * <pre>
 * &lt;build><br />
 * &nbsp;&nbsp;...<br />
 * &nbsp;&nbsp;&lt;plugins><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;...<br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;plugin><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;groupId>org.phpmaven&lt;/groupId><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;artifactId>maven-php-exec&lt;/artifactId><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;configuration><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;executable>/path/to/my/php.exe&lt;/executable><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/configuration><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/plugin><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;...<br />
 * &nbsp;&nbsp;&lt;/plugins><br />
 * &nbsp;&nbsp;...<br />
 * &lt/build><br />
 * </pre>
 * This example will use an alternative php executable.
 * </p>
 * 
 * <p>
 * Another (and better) way to configure the plugin is either setting the parameter at command line
 * (for example "mvn -Dphp.executable=/path/to/php.exe") or via settings.xml (property php.executable).
 * </p>
 * 
 * <p>
 * Available options:
 * </p>
 * 
 * <table border="1">
 * <tr><th>Name</th><th>Command line option</th><th>Property</th><th>Default</th><th>Description</th></tr>
 * <tr>
 *   <td>executable</td>
 *   <td>-Dphp.executable=</td>
 *   <td>php.executable</td>
 *   <td>"php" or "php.exe" (found on the PATH)</td>
 *   <td>The php.exe that will be used</td>
 * </tr>
 * <tr>
 *   <td>interpreter</td>
 *   <td>-Dphp.interpreter=</td>
 *   <td>php.interpreter</td>
 *   <td>PHP_EXE</td>
 *   <td>An alternative interpreter that will be used; additional plugins are able to install custom
 *       interpreters implementing the Interface {@link IPhpExecutable}. For example there may be interpreters
 *       written in java.
 *   </td>
 * </tr>
 * <tr>
 *   <td>useCache</td>
 *   <td>-Dphp.executable.useCache=</td>
 *   <td>php.executable.useCache</td>
 *   <td>true</td>
 *   <td>A boolean controlling the caching of php executables. Some information we get from PHP and some
 *       invocations may be cached. If you get any problems you should try to set this option to false.
 *   </td>
 * </tr>
 * <tr>
 *   <td>env</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>(none)</td>
 *   <td>Additional environment variables that are set during php invocation:
 *       &lt;env>&lt;myVar>Value&lt;/myVar>&lt;/env>
 *   </td>
 * </tr>
 * <tr>
 *   <td>phpDefines</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>(none)</td>
 *   <td>Additional defines that will be declared with command line option -d. Usage:
 *       &lt;phpDefines>&lt;myVar>Value&lt;/myVar>&lt;/phpDefines>
 *   </td>
 * </tr>
 * <tr>
 *   <td>includePath</td>
 *   <td>-</td>
 *   <td>-</td>
 *   <td>(none)</td>
 *   <td>Additional include paths that will be used by php. Notice: The include path within a php project
 *       will be always set from the maven-php-plugin mojos. You do not need to add target/classes manually.
 *       Usage: &lt;includePath>&lt;param>/path/to/additional/php/files&lt;/param>&lt;/includePath>
 *   </td>
 * </tr>
 * <tr>
 *   <td>additionalPhpParameters</td>
 *   <td>-Dphp.executable.additionalParameters=</td>
 *   <td>php.executable.additionalParameters</td>
 *   <td>(none)</td>
 *   <td>Additional command line arguments passed to the php executable.</td>
 * </tr>
 * <tr>
 *   <td>ignoreIncludeErrors</td>
 *   <td>-Dphp.executable.ignoreIncludeErrors=</td>
 *   <td>php.executable.ignoreIncludeErrors</td>
 *   <td>false</td>
 *   <td>A boolean controlling the behaviour of PHP-Maven. If set to true PHP-Maven will ignore any error
 *       for unknown/failed includes. Otherwise PHP-Maven will always generate an error if PHP reports an
 *       unknown include.
 *   </td>
 * </tr>
 * <tr>
 *   <td>logPhpOutput</td>
 *   <td>-Dphp.executable.logPhpOutput=</td>
 *   <td>php.executable.logPhpOutput</td>
 *   <td>false</td>
 *   <td>A boolean controlling the behaviour of PHP-Maven. If set to true PHP-Maven will display every
 *       output from PHP invocations. The output is display with log level INFO.
 *   </td>
 * </tr>
 * <tr>
 *   <td>temporaryScriptFile</td>
 *   <td>-Dphp.executable.temporaryScript=</td>
 *   <td>php.executable.temporaryScript</td>
 *   <td>${project.basedir}/target/snippet.php</td>
 *   <td>Some mojos will execute small script files. This parameter sets a filename that will be used for this
 *       script executions. PHP-Maven will put the script to be executed in this file and will execute the file.
 *   </td>
 * </tr>
 * </table>
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
@BuildPluginConfiguration(groupId = "org.phpmaven", artifactId = "maven-php-exec")
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
     * Returns the interpreter that will be used.
     * 
     * @return The php interpreter.
     */
    String getInterpreter();

    /**
     * Sets the php interpreter to be used.
     * 
     * @param interpreter the php interpreter.
     */
    void setInterpreter(String interpreter);

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
     * 
     * @throws PlexusConfigurationException thrown on errors while creating the executable
     * @throws ComponentLookupException  thrown on errors while creating the executable
     */
    IPhpExecutable getPhpExecutable(Log log) throws PlexusConfigurationException, ComponentLookupException;
    
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

}
