/**
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

package org.phpmaven.phpdoc.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.apache.maven.plugin.logging.Log;
import org.phpmaven.phpdoc.IPhpdocEntry;
import org.phpmaven.phpdoc.IPhpdocEntry.EntryType;
import org.phpmaven.phpdoc.IPhpdocRequest;
import org.phpmaven.phpdoc.IPhpdocSupport;
import org.phpmaven.phpexec.library.PhpCoreException;

/**
 * A maven 2.0 plugin for generating phpdocumentor documentations. This plugin is
 * used in the <code>site</code> phase.
 *
 * @goal phpdocumentor
 * @phase site
 * @author Christian Wiedemann
 * @author Tobias Sarnowski
 */
abstract class AbstractPhpdocSupport implements IPhpdocSupport {

    /**
     * Writes an ini log file.
     * @param log log
     * @param request request
     * @param phpDocConfigFile phpdoc config file
     * @param generatedPhpDocConfigFile 
     * @throws IOException io exception
     * @throws PhpCoreException php core exception
     */
    protected void writeIni(Log log, IPhpdocRequest request, File phpDocConfigFile, File generatedPhpDocConfigFile)
        throws IOException,PhpCoreException {
        final Properties properties = new Properties();
        if (phpDocConfigFile.isFile()) {
            log.debug("generating phpdoc using config from " + phpDocConfigFile.getAbsolutePath());
            properties.load(new FileInputStream(phpDocConfigFile));
        } else {
            log.debug("config file " + phpDocConfigFile.getAbsolutePath() + " not found. ignoring.");
        }
        
        final Iterator<IPhpdocEntry> iter = request.getEntries().iterator();
        final IPhpdocEntry entry = iter.next();
        if (entry.getType() == EntryType.FILE) {
            log.error("Report generation for files not supported.");
            // TODO support it
            throw new PhpCoreException("Report generation for files not supported.");
        }
        if (iter.hasNext()) {
            log.error("Report generation for multiple source folders not supported.");
            // TODO support it
            throw new PhpCoreException("Report generation for multiple folders not supported.");
        }
        
        properties.put("directory", entry.getFile().getAbsolutePath());
        properties.put("target", request.getReportFolder().getAbsolutePath());

        this.writePropFile(properties, "[Parse Data]", generatedPhpDocConfigFile);
    }
    
    /**
     * Writes a xml log file.
     * @param log log
     * @param request request
     * @param phpDocConfigFile phpdoc config file
     * @param generatedPhpDocConfigFile 
     * @throws IOException io exception
     * @throws PhpCoreException php core exception
     */
    protected void writeXml(Log log, IPhpdocRequest request, File phpDocConfigFile, File generatedPhpDocConfigFile)
        throws IOException, PhpCoreException {

        // TODO read xml input file
//        final Properties properties = new Properties();
//        if (phpDocConfigFile.isFile()) {
//            log.debug("generating phpdoc using config from " + phpDocConfigFile.getAbsolutePath());
//            properties.load(new FileInputStream(phpDocConfigFile));
//        } else {
//            log.debug("config file " + phpDocConfigFile.getAbsolutePath() + " not found. ignoring.");
//        }
        
        final StringBuffer buffer = new StringBuffer();
        buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
        buffer.append("<phpdocumentor>\n");
        buffer.append("<transformer>\n");
        buffer.append("<target>" + request.getReportFolder().getAbsolutePath() + "</target>\n");
        buffer.append("</transformer>\n");
        final Iterator<IPhpdocEntry> iter = request.getEntries().iterator();
        final IPhpdocEntry entry = iter.next();
        if (entry.getType() == EntryType.FILE) {
            log.error("Report generation for files not supported.");
            // TODO support it
            throw new PhpCoreException("Report generation for files not supported.");
        }
        if (iter.hasNext()) {
            log.error("Report generation for multiple source folders not supported.");
            // TODO support it
            throw new PhpCoreException("Report generation for multiple folders not supported.");
        }
        buffer.append("<files>\n");
        buffer.append("<directory>" + entry.getFile().getAbsolutePath() + "</directory>\n");
        buffer.append("</files>\n");
        buffer.append("</phpdocumentor>\n");

        generatedPhpDocConfigFile.getParentFile().mkdirs();
        final FileWriter fileWriter = new FileWriter(generatedPhpDocConfigFile);
        fileWriter.append(buffer.toString());
        fileWriter.close();
    }

    /**
     * Creates a property file.
     *
     * @param properties the properties to use
     * @param preFileContent templates
     * @param generatedPhpDocConfigFile
     * @throws IOException if something goes wrong while writing
     */
    private void writePropFile(Properties properties, String preFileContent, File generatedPhpDocConfigFile)
        throws IOException {
        final String lineSeparator = System.getProperty("line.separator");
        generatedPhpDocConfigFile.getParentFile().mkdirs();
        final FileWriter fileWriter = new FileWriter(generatedPhpDocConfigFile);
        final Set<Object> keySet = properties.keySet();
        if (preFileContent != null)
            fileWriter.append(preFileContent + lineSeparator);

        final Iterator<Object> iterator = keySet.iterator();
        while (iterator.hasNext()) {
            final String key = (String) iterator.next();
            final String value = properties.getProperty(key);
            fileWriter.append(key + "=" + value + lineSeparator);
        }
        fileWriter.close();
    }
    
}
