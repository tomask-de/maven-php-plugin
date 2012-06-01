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

/**
 * A line in config file (single for directives or multiple for sections).
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.1
 */
class ConfigFileLineSection extends AbstractConfigContainer implements IConfigFileLineSection {

    /**
     * The section name.
     */
    private String sectionName;
    
    /**
     * The section data.
     */
    private String sectionData;

    /**
     * The tooling.
     */
    private AbstractConfigTool tool;
    
    /**
     * Constructor.
     * @param sectionName
     * @param sectionData
     * @param tool
     */
    public ConfigFileLineSection(String sectionName, String sectionData, AbstractConfigTool tool) {
        this.sectionName = sectionName;
        this.sectionData = sectionData;
        this.tool = tool;
    }

    @Override
    public String getSectionName() {
        return this.sectionName;
    }
    
    @Override
    public String toString() {
        return "<" + this.sectionName + " " + this.sectionData + ">\n" +
        	super.toString() + "</" + this.sectionName + ">\n";
    }

    @Override
    protected AbstractConfigTool getTooling() {
        return this.tool;
    }
    
}
