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
package org.phpmaven.httpd.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.phpmaven.httpd.control.IApacheConfigCommon;
import org.phpmaven.httpd.control.IApacheConfigDirectory;


/**
 * Abstract config container that can contain and build configuration contents.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.1
 */
abstract class AbstractConfigCommon extends AbstractConfigContainer implements IApacheConfigCommon {


    @Override
    public String getContents() {
        return this.toString();
    }

    @Override
    public void setContents(String contents) {
        this.parseContent(contents);
    }

    @Override
    public void append(String contents) {
        this.parseContent(this.toString() + contents);
    }

    @Override
    public String getAccessFileName() {
        return this.getSingleDirectiveValue("AccessFileName");
    }

    @Override
    public void setAccessFileName(String filename) {
        this.overwriteSingleDirective("AccessFileName", filename);
    }

    @Override
    public String getErrorLog() {
        return this.getSingleDirectiveValue("ErrorLog");
    }

    @Override
    public void setErrorLog(String errorLog) {
        this.overwriteSingleDirective("ErrorLog", errorLog);
    }

    @Override
    public String getCustomLogFormat() {
        final IConfigFileLineDirective value = this.getSingleDirective("CustomLog");
        if (value != null) {
            return value.getValue(1);
        }
        return null;
    }

    @Override
    public void setCustomLogFormat(String format) {
        IConfigFileLineDirective value = this.getSingleDirective("CustomLog");
        if (value == null) {
            value = this.addDirective("CustomLog");
            value.setValue(0, "access.log");
        }
        value.setValue(1, format);
    }

    @Override
    public String getCustomLogFile() {
        final IConfigFileLineDirective value = this.getSingleDirective("CustomLog");
        if (value != null) {
            return value.getValue(0);
        }
        return null;
    }

    @Override
    public void setCustomLogFile(String file) {
        IConfigFileLineDirective value = this.getSingleDirective("CustomLog");
        if (value == null) {
            value = this.addDirective("CustomLog");
            value.setValue(1, "common");
        }
        value.setValue(0, file);
    }

    @Override
    public Map<String, String> getErrorDocuments() {
        final Map<String, String> result = new HashMap<String, String>();
        for (final IConfigFileLineDirective dir : this.getDirectives("ErrorDocument")) {
            result.put(dir.getValue(0), dir.getValue(1));
        }
        return result;
    }

    @Override
    public void setErrorDocument(String code, String document) {
        for (final IConfigFileLineDirective dir : this.getDirectives("ErrorDocument")) {
            if (code.equals(dir.getValue(0))) {
                dir.setValue(1, document);
                return;
            }
        }
        final IConfigFileLineDirective dir = this.addDirective("ErrorDocument");
        dir.setValue(0, code);
        dir.setValue(1, document);
    }

    @Override
    public void removeErrorDocument(String code) {
        for (final IConfigFileLineDirective dir : this.getDirectives("ErrorDocument")) {
            if (code.equals(dir.getValue(0))) {
                this.removeDirective(dir);
                return;
            }
        }
    }

    @Override
    public String getDocumentRoot() {
        return this.getSingleDirectiveValue("DocumentRoot");
    }

    @Override
    public void setDocumentRoot(String docRoot) {
        this.overwriteSingleDirective("DocumentRoot", docRoot);
    }

    @Override
    public String getServerName() {
        return this.getSingleDirectiveValue("ServerName");
    }

    @Override
    public void setServerName(String name) {
        this.overwriteSingleDirective("ServerName", name);
    }

    @Override
    public Iterable<IApacheConfigDirectory> getDirectories() {
        final List<IApacheConfigDirectory> result = new ArrayList<IApacheConfigDirectory>();
        for (final IConfigFileLineSection section : this.getDirectiveSections("Directory")) {
            result.add((IApacheConfigDirectory) section);
        }
        return result;
    }

    @Override
    public IApacheConfigDirectory declareDirectory(String directory) {
        final ConfigDirectory section = new ConfigDirectory(directory, this.getTooling());
        this.addSection(section);
        return section;
    }

    @Override
    public void removeDirectory(IApacheConfigDirectory directory) {
        this.removeSection((IConfigFileLineSection) directory);
    }
    
}
