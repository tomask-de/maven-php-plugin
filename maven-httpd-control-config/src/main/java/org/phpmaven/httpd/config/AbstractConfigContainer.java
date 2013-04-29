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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Abstract config container that can contain and build configuration contents.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.1
 */
abstract class AbstractConfigContainer {
    
    /**
     * Pattern to match configuration sections.
     */
    private static final Pattern SECTION_PATTERN = Pattern.compile("<(\\S+)?\\s+(.*)?>");

    /**
     * Pattern to match configuration directives.
     */
    private static final Pattern DIRECTIVE_PATTERN = Pattern.compile("(\\S+)?\\s+(.*)?");

    /**
     * The whole config file.
     */
    private List<IConfigFileLine> lines;
    
    /**
     * Mapping from directives to config file lines.
     */
    private Map<String, List<IConfigFileLineDirective>> directives;
    
    /**
     * Configuration sections. Key is name of the section and value is a list of config file lines.
     */
    private Map<String, List<IConfigFileLineSection>> sections;
    
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        for (final IConfigFileLine line : this.lines) {
            buffer.append(line.toString());
        }
        return buffer.toString();
    }
    
    /**
     * Removes a directive from file.
     * @param line line to be removed.
     */
    void removeDirective(IConfigFileLineDirective line) {
        this.lines.remove(line);
        this.directives.remove(line.getDirectiveName());
    }
    
    /**
     * Returns the directive value.
     * @param key key of the directive
     * @return value
     */
    String getSingleDirectiveValue(String key) {
        final List<IConfigFileLineDirective> directiveList = this.directives.get(key);
        if (directiveList != null && !directiveList.isEmpty()) {
            return directiveList.get(directiveList.size() - 1).getValue();
        }
        return null;
    }
    
    /**
     * Returns the directives value.
     * @param key key of the directive
     * @return values
     */
    @SuppressWarnings("unchecked")
    List<IConfigFileLineDirective> getDirectives(String key) {
        final List<IConfigFileLineDirective> directiveList = this.directives.get(key);
        if (directiveList != null && !directiveList.isEmpty()) {
            return Collections.unmodifiableList(directiveList);
        }
        return Collections.EMPTY_LIST;
    }
    
    /**
     * Returns the directive value.
     * @param key key of the directive
     * @return value
     */
    IConfigFileLineDirective getSingleDirective(String key) {
        final List<IConfigFileLineDirective> directiveList = this.directives.get(key);
        if (directiveList != null && !directiveList.isEmpty()) {
            return directiveList.get(directiveList.size() - 1);
        }
        return null;
    }
    
    /**
     * Removes a directive from file.
     * @param line line to be removed.
     */
    void removeSection(IConfigFileLineSection line) {
        this.lines.remove(line);
        this.sections.get(line.getSectionName()).remove(line);
    }
    
    /**
     * Removes a directive from file.
     * @param line line to be removed.
     */
    void removeDirective(String key) {
        final List<IConfigFileLineDirective> directiveList = this.directives.get(key);
        if (directiveList != null) {
            this.lines.removeAll(directiveList);
            directiveList.clear();
        }
    }
    
    /**
     * Adds a new config line section
     * @param line line
     */
    void addSection(IConfigFileLineSection line) {
        this.lines.add(line);
        List<IConfigFileLineSection> list = this.sections.get(line.getSectionName());
        if (list == null) {
            list = new ArrayList<IConfigFileLineSection>();
            this.sections.put(line.getSectionName(), list);
        }
        list.add(line);
    }
    
    /**
     * Overwrites the given directive.
     * @param key key
     * @param value value
     */
    void overwriteSingleDirective(String key, String value) {
        List<IConfigFileLineDirective> directiveList = this.directives.get(key);
        IConfigFileLineDirective directive = null;
        if (directiveList == null) {
            directiveList = new ArrayList<IConfigFileLineDirective>();
            this.directives.put(key, directiveList);
        }
        if (directiveList.isEmpty()) {
            directive = new ConfigFileLineDirective(key);
            directiveList.add(directive);
            lines.add(directive);
        } else {
            directive = directiveList.get(directiveList.size() - 1);
        }
        directive.setValue(value);
    }
    
    /**
     * Adds a directive.
     * @param key key
     * @return directive object
     */
    IConfigFileLineDirective addDirective(String key) {
        List<IConfigFileLineDirective> directiveList = this.directives.get(key);
        IConfigFileLineDirective directive = null;
        if (directiveList == null) {
            directiveList = new ArrayList<IConfigFileLineDirective>();
            this.directives.put(key, directiveList);
        }
        directive = new ConfigFileLineDirective(key);
        directiveList.add(directive);
        lines.add(directive);
        return directive;
    }
    
    /**
     * Adds a directive.
     * @param key key
     * @return directive object
     */
    IConfigFileLineDirective addDirective(IConfigFileLineDirective directive) {
        List<IConfigFileLineDirective> directiveList = this.directives.get(directive.getDirectiveName());
        if (directiveList == null) {
            directiveList = new ArrayList<IConfigFileLineDirective>();
            this.directives.put(directive.getDirectiveName(), directiveList);
        }
        directiveList.add(directive);
        lines.add(directive);
        return directive;
    }
    
    /**
     * Returns the directives value.
     * @param key key of the directive
     * @return values
     */
    @SuppressWarnings("unchecked")
    List<IConfigFileLineSection> getDirectiveSections(String key) {
        final List<IConfigFileLineSection> directiveList = this.sections.get(key);
        if (directiveList != null && !directiveList.isEmpty()) {
            return Collections.unmodifiableList(directiveList);
        }
        return Collections.EMPTY_LIST;
    }
    
    /**
     * Parses the contents.
     * @param contents.
     */
    void parseContent(String contents) {
        this.directives.clear();
        this.sections.clear();
        this.lines.clear();
        final StringTokenizer tokenizer = new StringTokenizer(contents, "\n");
        while (tokenizer.hasMoreTokens()) {
            final String line = tokenizer.nextToken();
            final String trimmed = line.trim();
            consumeLine(tokenizer, this, line, trimmed);
        }
    }

    /**
     * Consumes a line.
     * @param sectionPattern
     * @param tokenizer
     * @param line
     * @param trimmed
     */
    private void consumeLine(final StringTokenizer tokenizer, final AbstractConfigContainer container, final String line, final String trimmed) {
        if (trimmed.startsWith("#") || trimmed.length() == 0) {
            this.lines.add(new ConfigFileLine(line));
            return;
        }
        final Matcher sectionMatcher = SECTION_PATTERN.matcher(trimmed);
        if (sectionMatcher.matches()) {
            // starting a section
            final String sectionName = sectionMatcher.group(1);
            final String sectionData = sectionMatcher.group(2);
            IConfigFileLineSection section = null;
            if ("Directory".equals(sectionName)) {
                section = this.consumeDirectory(tokenizer, sectionData);
            } else if ("VirtualHost".equals(sectionName)) {
                section = this.consumeVHostSite(tokenizer, sectionData);
            } else {
                section = this.consumeSection(tokenizer, sectionName, sectionData);
            }
            container.addSection(section);
            return;
        }
        
        final Matcher directiveMatcher = DIRECTIVE_PATTERN.matcher(trimmed);
        if (directiveMatcher.matches()) {
            final String dirName = directiveMatcher.group(1);
            final String dirValue = directiveMatcher.group(2);
            IConfigFileLineDirective directive = null;
            if ("Listen".equals(dirName)) {
                directive = new Port(Integer.parseInt(dirValue), this.getTooling());
            } else if ("NameVirtualHost".equals(dirName)) {
                directive = new VHost(dirValue, this.getTooling());
            } else {
                directive = new ConfigFileLineDirective(dirName);
                // parse the values.
                int index = 0;
                final StringBuffer buffer = new StringBuffer();
                boolean insideNormal = false;
                boolean insideComplex = false;
                boolean insideEscaped = false;
                for (final char c : dirValue.toCharArray()) {
                    switch (c) {
                        case '\\':
                            if (insideEscaped) {
                                buffer.append(c);
                                insideEscaped = false;
                            } else {
                                insideEscaped = true;
                            }
                            break;
                        case '"':
                            if (insideEscaped) {
                                insideEscaped = false;
                                buffer.append(c);
                            } else if (insideComplex) {
                                directive.setValue(index, buffer.toString());
                                buffer.setLength(0);
                                insideComplex = false;
                                index++;
                            } else if (insideNormal) {
                                throw new IllegalStateException("Directive syntax error");
                            } else {
                                insideComplex = true;
                            }
                            break;
                        case ' ':
                            if (insideEscaped) {
                                insideEscaped = false;
                                buffer.append(c);
                            } else if (insideComplex) {
                                buffer.append(c);
                            } else if (insideNormal) {
                                directive.setValue(index, buffer.toString());
                                buffer.setLength(0);
                                insideNormal = false;
                                index++;
                            } else {
                                // silently ignore
                            }
                            break;
                        default:
                            if (!insideNormal) {
                                insideNormal = true;
                            }
                            buffer.append(c);
                            break;
                    }
                }
            }
            this.addDirective(directive);
            return;
        }
        
        this.lines.add(new ConfigFileLine(line));
    }

    private IConfigFileLineSection consumeSection(StringTokenizer tokenizer, String sectionName, String sectionData) {
        final ConfigFileLineSection section = new ConfigFileLineSection(sectionName, sectionData, this.getTooling());
        final String endTag = "</" + sectionName + ">";
        while (tokenizer.hasMoreTokens()) {
            final String line = tokenizer.nextToken();
            final String trimmed = line.trim();
            if (trimmed.equals(endTag)) {
                return section;
            }
            this.consumeLine(tokenizer, section, line, trimmed);
        }
        throw new IllegalStateException("Invalid configuration file. Found <" + sectionName + "> without </" + sectionName + ">.");
    }

    /**
     * Consumes a directory section.
     * @param tokenizer tokenizer to be used.
     * @param path the directory path
     * @return the directory.
     */
    private IConfigFileLineSection consumeDirectory(StringTokenizer tokenizer, String path) {
        final ConfigDirectory section = new ConfigDirectory(path, this.getTooling());
        while (tokenizer.hasMoreTokens()) {
            final String line = tokenizer.nextToken();
            final String trimmed = line.trim();
            if (trimmed.equals("</Directory>")) {
                return section;
            }
            this.consumeLine(tokenizer, section, line, trimmed);
        }
        throw new IllegalStateException("Invalid configuration file. Found <Directory> without </Directory>.");
    }

    /**
     * Consumes a vhost site section.
     * @param tokenizer tokenizer to be used.
     * @param name the vhost site name
     * @return the vhost site.
     */
    private IConfigFileLineSection consumeVHostSite(StringTokenizer tokenizer, String name) {
        final VHostSite section = new VHostSite(name, this.getTooling());
        while (tokenizer.hasMoreTokens()) {
            final String line = tokenizer.nextToken();
            final String trimmed = line.trim();
            if (trimmed.equals("</VirtualHost>")) {
                return section;
            }
            this.consumeLine(tokenizer, section, line, trimmed);
        }
        throw new IllegalStateException("Invalid configuration file. Found <VirtualHost> without </VirtualHost>.");
    }
    
    /**
     * Returns the tooling container.
     * @return tooling.
     */
    protected abstract AbstractConfigTool getTooling();
    
}
