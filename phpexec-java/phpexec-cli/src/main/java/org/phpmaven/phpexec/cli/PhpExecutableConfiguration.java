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

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.util.cli.StreamConsumer;
import org.phpmaven.phpexec.library.IPhpExecutable;
import org.phpmaven.phpexec.library.IPhpExecutableConfiguration;
import org.phpmaven.phpexec.library.PhpException;
import org.phpmaven.phpexec.library.PhpVersion;

/**
 * A php executable configuration.
 * 
 * <p>This configuration is used to declare a php executable that PHP-Maven will use.
 * You can declare a path to an existing executable if it is not found via PATH variable.
 * Additional command line options, PHP.INI options and environment variables can be set.</p>
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 0.1.8
 */
public class PhpExecutableConfiguration implements IPhpExecutableConfiguration {
	
	/**
     * Path to the php executable file.
     */
    private String executable = "php";
    
    /**
     * Flag that controls the activity of the php information cache.
     */
    private boolean useCache = true;
    
    /**
     * The work directory to be used.
     */
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
    private String additionalPhpParameters;

    /**
     * If true, errors triggered because of missing includes will be ignored.
     */
    private boolean ignoreIncludeErrors;

    /**
     * If the output of the php scripts will be written to the console.
     */
    private boolean logPhpOutput;
    
    /**
     * A temporary script file that can be used for php execution of small code snippets.
     */
    private File temporaryScriptFile = new File("snippet.php");
    
    /**
     * error reporting constant.
     */
    private String errorReporting = "E_ALL";
    
    public PhpExecutableConfiguration() {
    	try {
    		this.temporaryScriptFile = File.createTempFile("snippet", "php");
    		this.temporaryScriptFile.deleteOnExit();
    	} catch (IOException ex) {
    		// silently ignore
    	}
    }

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
    public IPhpExecutable getPhpExecutable() {
        final PhpExecutable result = new PhpExecutable();
        result.configure(this);
        if (this.useCache) {
            final IPhpExecutable cache = ExecCache.instance().get(this);
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
        public int execute(String arguments, OutputStream stdout,
        		OutputStream stderr) throws PhpException {
            return this.result.execute(arguments, stdout, stderr);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int execute(String arguments, File file, OutputStream stdout)
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

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setSilent(boolean silent) {
			this.result.setSilent(silent);
		}

		@Override
		public int execute(String arguments, File file, StreamConsumer stdout)
				throws PhpException {
			return this.result.execute(arguments, file, stdout);
		}

		@Override
		public int execute(String arguments, StreamConsumer stdout,
				StreamConsumer stderr) throws PhpException {
			return this.result.execute(arguments, stdout, stderr);
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
