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
package org.phpmaven.httpd.control;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.plexus.util.FileUtils;

/**
 * Helper class for reading configuration files.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.1
 */
public final class ConfigUtils {
    
    /**
     * Hidden constructor.
     */
    private ConfigUtils() {
        // hidden constructor
    }

    /**
     * Reads the configuration file.
     * @param defaultCfg configuration file.
     * @return default config or null if the file cannot be read.
     */
    public static String readConfigFile(File defaultCfg) {
        try {
            String contents = FileUtils.fileRead(defaultCfg);
            final Pattern includePattern = Pattern.compile("/^\\s*include\\s+(.*)?$/i");
            final Matcher matcher = includePattern.matcher(contents);
            while (matcher.find()) {
                final MatchResult result = matcher.toMatchResult();
                String file = result.group(1);
                if (!new File(file).isAbsolute()) {
                    file = defaultCfg.getAbsolutePath() + File.separator + file;
                }
                final StringBuffer buffer = new StringBuffer();
                if (file.contains("*") || file.contains("?")) {
                    final File parent = new File(file).getParentFile();
                    final String filter = new File(file).getName().
                        replace("/", "\\/").
                        replace("\\", "\\\\").
                        replace(".", "\\.").
                        replace("*", ".*").
                        replace("?", ".").
                        replace("(", "\\(").
                        replace(")", "\\)").
                        replace("^", "\\^").
                        replace("$", "\\$").
                        replaceAll("\\[!(.*)?\\]", "\\[^$1\\]");
                    final Pattern filterPattern = Pattern.compile(filter);
                    for (final File chld : parent.listFiles(new FilenameFilter() {
                        
                        @Override
                        public boolean accept(File dir, String name) {
                            if (dir.equals(parent) && filterPattern.matcher(name).matches()) {
                                return true;
                            }
                            return false;
                        }
                    })) {
                        final String ccontents = FileUtils.fileRead(chld);
                        buffer.append(ccontents);
                    }
                } else {
                    final String ccontents = FileUtils.fileRead(file);
                    buffer.append(ccontents);
                }
                
                contents = contents.substring(0, result.start()) +
                        contents.toString() +
                        contents.substring(result.end());
            }
            
            return contents;
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }
    
}
