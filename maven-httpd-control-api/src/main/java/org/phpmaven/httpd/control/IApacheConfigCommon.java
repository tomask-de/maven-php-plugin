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

import java.util.Map;


/**
 * common entries of a apache log.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.1
 */
public interface IApacheConfigCommon {
    
    /**
     * Returns the contents of the config file.
     * @return config file contents.
     */
    String getContents();
    
    /**
     * Sets the whole config file.
     * @param contents config file contents.
     */
    void setContents(String contents);
    
    /**
     * Appends content to the config file.
     * @param contents content to be added.
     */
    void append(String contents);
    
    /**
     * Returns the htaccess file name.
     * @return htaccess file name.
     */
    String getAccessFileName();
    
    /**
     * Sets the htaccess file name.
     * @param filename htaccess file name.
     */
    void setAccessFileName(String filename);
    
    /**
     * Returns the error log.
     * @return error log file name.
     */
    String getErrorLog();
    
    /**
     * Sets the error log.
     * @param errorLog error log filename.
     */
    void setErrorLog(String errorLog);
    
    /**
     * Returns the log format.
     * @return custom log format.
     */
    String getCustomLogFormat();
    
    /**
     * Sets the custom log format.
     * @param format custom log format.
     */
    void setCustomLogFormat(String format);
    
    /**
     * Returns the log file.
     * @return log file.
     */
    String getCustomLogFile();
    
    /**
     * Sets the log file.
     * @param file log file.
     */
    void setCustomLogFile(String file);
    
    /**
     * Returns the error documents.
     * @return key is the http error code, value is the document.
     */
    Map<String, String> getErrorDocuments();
    
    /**
     * Sets an error document.
     * @param code http error code.
     * @param document document.
     */
    void setErrorDocument(String code, String document);
    
    /**
     * Removes an error document.
     * @param code http error code.
     */
    void removeErrorDocument(String code);
    
    /**
     * Returns the document root.
     * @return document root.
     */
    String getDocumentRoot();
    
    /**
     * Sets the document root.
     * @param docRoot document root.
     */
    void setDocumentRoot(String docRoot);
    
    /**
     * Returns the server name.
     * @return server name.
     */
    String getServerName();
    
    /**
     * Sets the server name.
     * @param name server name.
     */
    void setServerName(String name);
    
    /**
     * Returns the directory declarations.
     * @return directory declarations.
     */
    Iterable<IApacheConfigDirectory> getDirectories();
    
    /**
     * Declares a new directory.
     * @param directory path.
     * @return declared directory object.
     */
    IApacheConfigDirectory declareDirectory(String directory);
    
    /**
     * Removes an existing directory.
     * @param directory directory.
     */
    void removeDirectory(IApacheConfigDirectory directory);

}
