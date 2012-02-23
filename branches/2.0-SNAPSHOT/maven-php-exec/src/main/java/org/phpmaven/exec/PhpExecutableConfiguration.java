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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Configuration;
import org.codehaus.plexus.util.cli.StreamConsumer;
import org.phpmaven.core.BuildPluginConfiguration;
import org.phpmaven.core.ConfigurationParameter;

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
@Component(role = IPhpExecutableConfiguration.class, instantiationStrategy = "per-lookup")
@BuildPluginConfiguration(groupId = "org.phpmaven", artifactId = "maven-php-exec")
public class PhpExecutableConfiguration implements IPhpExecutableConfiguration {

    /**
     * Path to the php executable file.
     * 
     * <p>
     * On Windows operating system the extension ".exe" will be automatically added if not specified.
     * Since this option is a machine dependend path it is recommended to not use it within the pom.xml.
     * Instead set a property in your settings.xml. The property "phpExecutable" will be mapped to this
     * executable configuration as soon as you use it.
     * </p>
     * 
     * <p>
     * Defaults to "php".
     * </p>
     * 
     * @parameter expression="${phpExecutable}" default-value="php"
     */
    @Configuration(name = "executable", value = "php")
    private String executable;
    
    /**
     * Flag that controls the activity of the php information cache.
     * 
     * <p>
     * The php cache helps to tune performance. Normally it is ok to let PHP-Maven cache some information
     * on the php installation. If there are any problems or if you expect that the php executable
     * behaves different depending on configuration and environment variables you should try to deactivate
     * the cache.
     * </p>
     * 
     * <p>
     * Defaults to true.
     * </p>
     * 
     * @parameter expression="${phpExecutableUseCache}" default-value="true"
     */
    private boolean useCache = true;
    
    /**
     * Additional environment variables.
     * 
     * <p>Use in the form &lt;env&gt;&lt;variable1&gt;value&lt;/variable1&gt;&lt;/env&gt;</p>
     * 
     * @parameter
     */
    private Map<String, String> env = new HashMap<String, String>();
    
    /**
     * Additional defines for php (command line option -D).
     * 
     * <p>
     * Support for additional command line defines given to PHP. Must not be used to declare the include path.
     * Use in the form: &lt;phpDefines&gt;&lt;variable1&gt;value&lt;/variable1&gt;&lt;/phpDefines&gt;
     * </p>
     * 
     * @parameter
     */
    private Map<String, String> phpDefines;
    
    /**
     * The include path that will be used for php.
     * 
     * @parameter
     */
    private List<String> includePath = new ArrayList<String>();

    /**
     * Additional PHP arguments.
     * 
     * <p>
     * Use php -h to get a list of all php compile arguments.
     * </p>
     * 
     * @parameter expression="${additionalPhpParameters}"
     */
    private String additionalPhpParameters;

    /**
     * If true, errors triggered because of missing includes will be ignored.
     * 
     * <p>Defaults to false.</p>
     * 
     * @parameter expression="${phpIgnoreIncludeErrors}" default-value="false"
     */
    private boolean ignoreIncludeErrors;

    /**
     * If the output of the php scripts will be written to the console.
     * 
     * <p>Defaults to false.</p>
     * 
     * @parameter expression="${logPhpOutput}" default-value="false"
     */
    private boolean logPhpOutput;
    
    /**
     * A temporary script file that can be used for php execution of small code snippets.
     * 
     * <p>Defauls to ${project.basedir}/target/snippet.php</p>
     */
    @ConfigurationParameter(name = "temporaryScriptFile", expression = "${project.basedir}/target/snippet.php")
    private File temporaryScriptFile;

    /**
     * Returns the executable that will be used.
     * 
     * @return The php executable path and file name.
     */
    @Override
    public String getExecutable() {
        return this.executable;
    }

    /**
     * Sets the php executable to be used.
     * 
     * @param executable the php executable path and file name.
     */
    @Override
    public void setExecutable(String executable) {
        this.executable = executable;
    }

    /**
     * Returns true if the php information cache can be used.
     * 
     * @return true if the cache can be used.
     */
    @Override
    public boolean isUseCache() {
        return this.useCache;
    }

    /**
     * Sets the flag to control the php information cache.
     * 
     * @param useCache true to use the cache.
     */
    @Override
    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }

    /**
     * Returns the environment variables to be used.
     * @return Map with environment variables.
     */
    @Override
    public Map<String, String> getEnv() {
        return this.env;
    }

    /**
     * Sets the environment variables.
     * @param env environment variables.
     */
    @Override
    public void setEnv(Map<String, String> env) {
        this.env = env;
    }

    /**
     * Returns the php defines to be used.
     * @return php defines.
     */
    @Override
    public Map<String, String> getPhpDefines() {
        return this.phpDefines;
    }

    /**
     * Sets the php defines to be used.
     * @param phpDefines php defines.
     */
    @Override
    public void setPhpDefines(Map<String, String> phpDefines) {
        this.phpDefines = phpDefines;
    }

    /**
     * Returns the include path.
     * @return include path.
     */
    @Override
    public List<String> getIncludePath() {
        return this.includePath;
    }

    /**
     * Sets the include path.
     * @param includePath include path.
     */
    @Override
    public void setIncludePath(List<String> includePath) {
        this.includePath = includePath;
    }
    
    /**
     * Returns additional php parameters.
     * @return additional php parameters.
     */
    @Override
    public String getAdditionalPhpParameters() {
        return this.additionalPhpParameters;
    }

    /**
     * Sets additional php parameters.
     * @param additionalPhpParameters additional php parameters.
     */
    @Override
    public void setAdditionalPhpParameters(String additionalPhpParameters) {
        this.additionalPhpParameters = additionalPhpParameters;
    }

    /**
     * Returns the flag to ignore include errors.
     * @return flag to ignore include errors.
     */
    @Override
    public boolean isIgnoreIncludeErrors() {
        return this.ignoreIncludeErrors;
    }

    /**
     * Sets the flag to ignore include errors.
     * @param ignoreIncludeErrors flag to ignore include errors.
     */
    @Override
    public void setIgnoreIncludeErrors(boolean ignoreIncludeErrors) {
        this.ignoreIncludeErrors = ignoreIncludeErrors;
    }

    /**
     * Returns true if php output will be logged.
     * @return true if php output will be logged.
     */
    @Override
    public boolean isLogPhpOutput() {
        return this.logPhpOutput;
    }

    /**
     * Sets the flag if php output will be logged.
     * @param logPhpOutput flag if php output will be logged.
     */
    @Override
    public void setLogPhpOutput(boolean logPhpOutput) {
        this.logPhpOutput = logPhpOutput;
    }

    /**
     * Returns the file to place temporary scripts that will be executed.
     * @return file to place temporary scripts that will be executed.
     */
    @Override
    public File getTemporaryScriptFile() {
        return this.temporaryScriptFile;
    }

    /**
     * Sets the file for temporary script executions.
     * @param temporaryScriptFile file for temporary scripts.
     */
    @Override
    public void setTemporaryScriptFile(File temporaryScriptFile) {
        this.temporaryScriptFile = temporaryScriptFile;
    }

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
    @Override
    public IPhpExecutable getPhpExecutable(Log log) {
        if (this.useCache) {
            final IPhpExecutable cache = ExecCache.instance().get(this.additionalPhpParameters, log);
            final IPhpExecutable result = new PhpExecutable(this, log);
            return new CachedExecutable(cache, result);
        }
        return new PhpExecutable(this, log);
    }
    
    /**
     * Helper implementation for a cached php executable.
     */
    private static final class CachedExecutable implements IPhpExecutable {
        private final IPhpExecutable cache;
        private final IPhpExecutable result;

        private CachedExecutable(IPhpExecutable cache, IPhpExecutable result) {
            this.cache = cache;
            this.result = result;
        }

        @Override
        public PhpVersion getVersion() throws PhpException {
            return this.cache.getVersion();
        }

        @Override
        public String executeCode(String arguments, String code,
                String codeArguments) throws PhpException {
            return this.result.executeCode(arguments, code, codeArguments);
        }

        @Override
        public String executeCode(String arguments, String code)
            throws PhpException {
            return this.result.executeCode(arguments, code);
        }

        @Override
        public int execute(String arguments, StreamConsumer stdout,
                StreamConsumer stderr) throws PhpException {
            return this.result.execute(arguments, stdout, stderr);
        }

        @Override
        public int execute(String arguments, File file, StreamConsumer stdout)
            throws PhpException {
            return this.result.execute(arguments, file, stdout);
        }

        @Override
        public String execute(String arguments, File file) throws PhpException {
            return this.result.execute(arguments, file);
        }
    }

}
