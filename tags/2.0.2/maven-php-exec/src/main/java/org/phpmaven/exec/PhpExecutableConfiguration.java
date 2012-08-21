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

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Configuration;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.util.cli.StreamConsumer;
import org.phpmaven.core.ConfigurationParameter;
import org.phpmaven.core.IComponentFactory;

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
public class PhpExecutableConfiguration implements IPhpExecutableConfiguration {

    /**
     * Path to the php executable file.
     */
    @Configuration(name = "executable", value = "php")
    @ConfigurationParameter(name = "executable", expression = "${php.executable}")
    private String executable;

    /**
     * Path to the php interpreter.
     */
    @Configuration(name = "interpreter", value = "PHP_EXE")
    @ConfigurationParameter(name = "interpreter", expression = "${php.interpreter}")
    private String interpreter;
    
    /**
     * Flag that controls the activity of the php information cache.
     */
    @Configuration(name = "useCache", value = "true")
    @ConfigurationParameter(name = "useCache", expression = "${php.executable.useCache}")
    private boolean useCache;
    
    /**
     * The work directory to be used.
     */
    @ConfigurationParameter(name = "workDirectory", expression = "${project.build.directory}")
    private File workDirectory;
    
    /**
     * Additional environment variables.
     */
    private Map<String, String> env = new HashMap<String, String>();
    
    /**
     * Additional defines for php (command line option -D).
     */
    private Map<String, String> phpDefines = new HashMap<String, String>();
    
    /**
     * The include path that will be used for php.
     */
    private List<String> includePath = new ArrayList<String>();

    /**
     * Additional PHP arguments.
     */
    @ConfigurationParameter(name = "additionalPhpParameters", expression = "${php.executable.additionalParameters}")
    private String additionalPhpParameters;

    /**
     * If true, errors triggered because of missing includes will be ignored.
     */
    @Configuration(name = "ignoreIncludeErrors", value = "false")
    @ConfigurationParameter(name = "ignoreIncludeErrors", expression = "${php.executable.ignoreIncludeErrors}")
    private boolean ignoreIncludeErrors;

    /**
     * If the output of the php scripts will be written to the console.
     */
    @Configuration(name = "logPhpOutput", value = "false")
    @ConfigurationParameter(name = "logPhpOutput", expression = "${php.executable.logPhpOutput}")
    private boolean logPhpOutput;
    
    /**
     * A temporary script file that can be used for php execution of small code snippets.
     */
    @ConfigurationParameter(name = "temporaryScriptFile", expression = "${project.build.directory}/snippet.php")
    private File temporaryScriptFile;
    
    /**
     * The component factory.
     */
    @Requirement
    private IComponentFactory componentFactory;

    /**
     * The maven session.
     */
    @ConfigurationParameter(name = "session", expression = "${session}")
    private MavenSession session;

    /**
     * error reporting constant.
     */
    @Configuration(name = "errorReporting", value = "NONE")
    @ConfigurationParameter(name = "errorReporting", expression = "${php.error.reporting}")
    private String errorReporting;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExecutable() {
        return this.executable;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setExecutable(String executable) {
        this.executable = executable;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUseCache() {
        return this.useCache;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getEnv() {
        return this.env;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnv(Map<String, String> env) {
        this.env = env;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getPhpDefines() {
        return this.phpDefines;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPhpDefines(Map<String, String> phpDefines) {
        this.phpDefines = phpDefines;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getIncludePath() {
        return this.includePath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setIncludePath(List<String> includePath) {
        this.includePath = includePath;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getAdditionalPhpParameters() {
        return this.additionalPhpParameters;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAdditionalPhpParameters(String additionalPhpParameters) {
        this.additionalPhpParameters = additionalPhpParameters;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isIgnoreIncludeErrors() {
        return this.ignoreIncludeErrors;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setIgnoreIncludeErrors(boolean ignoreIncludeErrors) {
        this.ignoreIncludeErrors = ignoreIncludeErrors;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLogPhpOutput() {
        return this.logPhpOutput;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLogPhpOutput(boolean logPhpOutput) {
        this.logPhpOutput = logPhpOutput;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getTemporaryScriptFile() {
        return this.temporaryScriptFile;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTemporaryScriptFile(File temporaryScriptFile) {
        this.temporaryScriptFile = temporaryScriptFile;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getInterpreter() {
        return this.interpreter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInterpreter(String interpreter) {
        this.interpreter = interpreter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPhpExecutable getPhpExecutable(Log log) throws PlexusConfigurationException, ComponentLookupException {
        final IPhpExecutable result = this.componentFactory.lookup(
                IPhpExecutable.class,
                this.getInterpreter(),
                IComponentFactory.EMPTY_CONFIG,
                this.session);
        result.configure(this, log);
        if (this.useCache) {
            final IPhpExecutable cache = ExecCache.instance().get(this, log);
            return new CachedExecutable(cache, result);
        }
        return result;
    }
    
    /**
     * Helper implementation for a cached php executable.
     */
    private static final class CachedExecutable implements IPhpExecutable {
        
        /** cached executable. */
        private final IPhpExecutable cache;
        
        /** resulting php executable. */
        private final IPhpExecutable result;

        /**
         * Constructor.
         * @param cache
         * @param result
         */
        private CachedExecutable(IPhpExecutable cache, IPhpExecutable result) {
            this.cache = cache;
            this.result = result;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public PhpVersion getVersion() throws PhpException {
            return this.cache.getVersion();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String executeCode(String arguments, String code,
                String codeArguments) throws PhpException {
            return this.result.executeCode(arguments, code, codeArguments);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String executeCode(String arguments, String code)
            throws PhpException {
            return this.result.executeCode(arguments, code);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int execute(String arguments, StreamConsumer stdout,
                StreamConsumer stderr) throws PhpException {
            return this.result.execute(arguments, stdout, stderr);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int execute(String arguments, File file, StreamConsumer stdout)
            throws PhpException {
            return this.result.execute(arguments, file, stdout);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String execute(String arguments, File file) throws PhpException {
            return this.result.execute(arguments, file);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void configure(IPhpExecutableConfiguration config, Log log) {
            throw new IllegalStateException("Must not call this method twice. The executable is already configured.");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getStrVersion() throws PhpException {
            return this.cache.getStrVersion();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String execute(File file) throws PhpException {
            return this.result.execute(file);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getWorkDirectory() {
        return this.workDirectory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setWorkDirectory(File file) {
        this.workDirectory = file;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getErrorReporting() {
        return this.errorReporting;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setErrorReporting(String errorReporting) {
        this.errorReporting = errorReporting;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumErrorReporting() {
        if (this.errorReporting == null ||
                this.errorReporting.length() == 0 ||
                this.errorReporting.equalsIgnoreCase("NONE")) {
            return -1;
        }
        
        try {
            return Integer.parseInt(this.errorReporting);
        } catch (NumberFormatException ex) {
            // ignore it
        }
        
        int result = 0;
        int i = 0;
        if (Character.isDigit(this.errorReporting.charAt(0))) {
            final String[] numSplit = this.errorReporting.split("\\D", 2);
            result = Integer.parseInt(numSplit[0]);
            i += numSplit[0].length();
        } else if (this.errorReporting.charAt(0) == 'E') {
            final String[] wrdSplit = this.errorReporting.split("[^\\w_]", 2);
            result = Enum.valueOf(ERROR_REPORTING.class, wrdSplit[0]).getNumericValue();
            i += wrdSplit[0].length();
        } else {
            throw new IllegalStateException("error reporting expression not supported or too complex: " +
                    this.errorReporting);
        }
        
        String reporting = this.errorReporting.substring(i);
        boolean isAnd = false;
        boolean isOr = false;
        boolean isNot = false;
        while (reporting.length() > 0) {
            switch (reporting.charAt(0)) {
                case ' ':
                case '\t':
                    // skip whitespaces
                    reporting = reporting.substring(1);
                    continue;
                case '&':
                    // and
                    isAnd = true;
                    reporting = reporting.substring(1);
                    continue;
                case '|':
                    // or
                    isOr = true;
                    reporting = reporting.substring(1);
                    continue;
                case '!':
                    // not
                    isNot = true;
                    reporting = reporting.substring(1);
                    continue;
                default:
                    // fall through
                    break;
            }
            if (!isAnd && !isOr) {
                throw new IllegalStateException("error reporting expression not supported or too complex: " +
                        this.errorReporting);
            }
            int operand = 0;
            if (Character.isDigit(reporting.charAt(0))) {
                final String[] numSplit = reporting.split("\\D", 2);
                operand = Integer.parseInt(numSplit[0]);
                reporting = reporting.substring(numSplit[0].length());
            } else if (reporting.charAt(0) == 'E') {
                final String[] wrdSplit = reporting.split("[^\\w_]", 2);
                operand = Enum.valueOf(ERROR_REPORTING.class, wrdSplit[0]).getNumericValue();
                reporting = reporting.substring(wrdSplit[0].length());
            } else {
                throw new IllegalStateException("error reporting expression not supported or too complex: " +
                        this.errorReporting);
            }
            if (isNot) {
                operand = Integer.MAX_VALUE - operand;
            }
            if (isAnd) {
                result &= operand;
            } else if (isOr) {
                result &= operand;
            }
            isNot = false;
            isAnd = false;
            isOr = false;
        }
        return result;
    }

}
