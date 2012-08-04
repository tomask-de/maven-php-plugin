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
import java.util.List;

/**
 * A line in config file (single for directives or multiple for sections.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.1
 */
class ConfigFileLineDirective implements IConfigFileLineDirective {

    /**
     * The directive name.
     */
    private String key;
    
    /**
     * The values.
     */
    private List<String> values = new ArrayList<String>();
    
    /**
     * Constructor.
     * @param key the key.
     */
    public ConfigFileLineDirective(String key) {
        this.key = key;
    }

    public String getDirectiveName() {
        return this.key;
    }

    public void setValue(String value) {
        this.values.clear();
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(0, value.length() - 1);
        }
        this.values.add(value);
    }

    public String getValue() {
        return this.values.isEmpty() ? null : this.values.get(0);
    }

    public String getValue(int index) {
        return this.values.get(index);
    }

    public void setValue(int index, String value) {
        while (this.values.size() < index) {
            this.values.add("");
        }
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(0, value.length() - 1);
        }
        if (this.values.size() == index) {
            this.values.add(value);
        } else {
            this.values.set(index, value);
        }
    }
    
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append(this.key);
        for (final String value : this.values) {
            buffer.append(" ");
            if (value.length() == 0 || value.contains(" ") || value.contains("\"")) {
                buffer.append("\"");
                buffer.append(value.replace("\\", "\\\\").replace("\"", "\\\""));
                buffer.append("\"");
            } else {
                buffer.append(value.replace("\\", "\\\\"));
            }
        }
        buffer.append("\n");
        return buffer.toString();
    }
    
}
