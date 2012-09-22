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
 *   <td>${project.build.directory}/snippet.php</td>
 *   <td>Some mojos will execute small script files. This parameter sets a filename that will be used for this
 *       script executions. PHP-Maven will put the script to be executed in this file and will execute the file.
 *   </td>
 * </tr>
 * <tr>
 *   <td>errorReporting</td>
 *   <td>-Dphp.error.reporting=</td>
 *   <td>php.error.reporting</td>
 *   <td>NONE</td>
 *   <td>Overwrite error reporting of php ({@see http://www.php.net/error_reporting}). You can use the term "NONE"
 *       to use the original value of php.ini as well as numeric values or the well known php constants
 *       (f.e. "E_ALL & ~E_NOTICE")
 *   </td>
 * </tr>
 * </table>
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
@BuildPluginConfiguration(groupId = "org.phpmaven", artifactId = "maven-php-exec")
public interface IPhpExecutableConfiguration extends org.phpmaven.phpexec.library.IPhpExecutableConfiguration {
	
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

}
