/*******************************************************************************
 * Copyright (c) 2011 PHP-Maven.org
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     PHP-Maven.org
 *******************************************************************************/

package org.phpmaven.eclipse.core.internal.archetype;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.compiler.util.Util;
import org.phpmaven.eclipse.core.PhpmavenCorePlugin;
import org.phpmaven.eclipse.core.archetype.IArchetypeRegistryDefinition;
import org.phpmaven.eclipse.core.archetype.IArchetypeRegistrySettings;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Implementation of the archetype registry settings.
 * 
 * @author mepeisen
 */
public class ArchetypeRegistrySettings implements IArchetypeRegistrySettings {
    
    /**
     * Name of the xml attribute for the last id that was used.
     */
    private static final String XML_ATTRIBUTE_LASTID = "lastId"; //$NON-NLS-1$

    /**
     * Name of the xml core attribute.
     */
    private static final String XML_ATTRIBUTE_CORE = "core"; //$NON-NLS-1$

    /**
     * Name of the registry settings xml file.
     */
    private static final String XML_REGISTRY_FILENAME = "archetype.registry.settings.xml"; //$NON-NLS-1$
    
    /**
     * XML node for entries: id
     */
    protected static final String XML_ENTRY_ID = "id";//$NON-NLS-1$
    /**
     * XML node for entries: title
     */
    protected static final String XML_ENTRY_TITLE = "title";//$NON-NLS-1$
    /**
     * XML node for entries: url
     */
    protected static final String XML_ENTRY_URL = "url";//$NON-NLS-1$
    /**
     * XML node for entries: last cached
     */
    protected static final String XML_ENTRY_LASTCACHED = "lastCached";//$NON-NLS-1$
    /**
     * XML node for entries: cache timeout
     */
    protected static final String XML_ENTRY_CACHE_TIMEOUT = "cacheTimeout";//$NON-NLS-1$
    /**
     * XML node for entries: failure
     */
    protected static final String XML_ENTRY_FAILURE = "failure";//$NON-NLS-1$
    /**
     * XML node for entries: offline
     */
    protected static final String XML_ENTRY_OFFLINE = "offline";//$NON-NLS-1$
    
    /**
     * The known settings.
     */
    private List<IArchetypeRegistryDefinition> definitions = new ArrayList<IArchetypeRegistryDefinition>();
    
    /**
     * The last id that was used.
     */
    private int lastId = 0;
    
    /**
     * Constructor.
     */
    public ArchetypeRegistrySettings() {
        final File fSettings = this.getRegistryFile();
        if (fSettings.exists()) {
            try {
                final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
                final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
                final Document doc = docBuilder.parse(new InputSource(new FileInputStream(fSettings)));
                final NodeList nl = doc.getChildNodes().item(0).getChildNodes();
                for (int i = 0; i < nl.getLength(); i++) {
                    final Node n = nl.item(i);
                    if (n.getNodeType() != Node.ELEMENT_NODE) continue;
                    if (n.getAttributes().getNamedItem(XML_ATTRIBUTE_CORE) != null) {
                        this.definitions.add(new ArchetypeRegistryCoreDefinition(n));
                    } else {
                        this.definitions.add(new ArchetypeRegistryDefinition(n));
                    }
                }
                this.lastId = Integer.parseInt(doc.getAttributes().getNamedItem(XML_ATTRIBUTE_LASTID).getTextContent());
            }
            catch (Exception ex) {
                // TODO exception handling
            }
            return;
        }
        this.definitions.add(new ArchetypeRegistryCoreDefinition());
        this.save();
    }
    
    /**
     * Saves the last definition.
     */
    private void save() {
        try {
            final StringBuffer buffer = new StringBuffer();
            buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"); //$NON-NLS-1$
            buffer.append("<registry ").append(XML_ATTRIBUTE_LASTID).append("=\"").append(this.lastId).append("\">\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            for (final IArchetypeRegistryDefinition def : this.definitions) {
                if (def.isSystem()) {
                    buffer.append("    <entry ").append(XML_ATTRIBUTE_CORE).append("=\"1\">\n"); //$NON-NLS-1$ //$NON-NLS-2$
                    buffer.append("        <").append(XML_ENTRY_TITLE).append(">").append(def.getTitle()).append("</").append(XML_ENTRY_TITLE).append(">\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                    buffer.append("        <").append(XML_ENTRY_LASTCACHED).append(">").append(def.getLastCached()).append("</").append(XML_ENTRY_LASTCACHED).append(">\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                    buffer.append("        <").append(XML_ENTRY_CACHE_TIMEOUT).append(">").append(def.getTimeoutInMinutes()).append("</").append(XML_ENTRY_CACHE_TIMEOUT).append(">\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                    buffer.append("        <").append(XML_ENTRY_FAILURE).append(">").append(def.wasFailure() ? "true" : "false").append("</").append(XML_ENTRY_FAILURE).append(">\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
                    buffer.append("        <").append(XML_ENTRY_OFFLINE).append(">").append(def.isOffline() ? "true" : "false").append("</").append(XML_ENTRY_OFFLINE).append(">\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
                    buffer.append("    </entry>\n"); //$NON-NLS-1$
                } else {
                    buffer.append("    <entry>\n"); //$NON-NLS-1$
                    buffer.append("        <").append(XML_ENTRY_ID).append(">").append(def.getId()).append("</").append(XML_ENTRY_ID).append(">\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                    buffer.append("        <").append(XML_ENTRY_TITLE).append(">").append(def.getTitle()).append("</").append(XML_ENTRY_TITLE).append(">\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                    buffer.append("        <").append(XML_ENTRY_URL).append(">").append(def.getUrl()).append("</").append(XML_ENTRY_URL).append(">\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                    buffer.append("        <").append(XML_ENTRY_LASTCACHED).append(">").append(def.getLastCached()).append("</").append(XML_ENTRY_LASTCACHED).append(">\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                    buffer.append("        <").append(XML_ENTRY_CACHE_TIMEOUT).append(">").append(def.getTimeoutInMinutes()).append("</").append(XML_ENTRY_CACHE_TIMEOUT).append(">\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                    buffer.append("        <").append(XML_ENTRY_FAILURE).append(">").append(def.wasFailure() ? "true" : "false").append("</").append(XML_ENTRY_FAILURE).append(">\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
                    buffer.append("        <").append(XML_ENTRY_OFFLINE).append(">").append(def.isOffline() ? "true" : "false").append("</").append(XML_ENTRY_OFFLINE).append(">\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
                    buffer.append("    </entry>\n"); //$NON-NLS-1$
                }
            }
            buffer.append("</registry>"); //$NON-NLS-1$
            final File fSettings = this.getRegistryFile();
            final FileOutputStream fos = new FileOutputStream(fSettings);
            fos.write(buffer.toString().getBytes());
            fos.close();
        }
        catch (Exception ex) {
            // TODO exception handling.
        }
    }

    /**
     * Returns the registry file.
     * @return registry file.
     */
    private File getRegistryFile() {
        return new File(PhpmavenCorePlugin.getDefault().getStateLocation().append(XML_REGISTRY_FILENAME).toOSString());
    }
    
    @Override
    public void doCache(IArchetypeRegistryDefinition def) throws CoreException {
        try {
            final URL url = new URL(def.getUrl());
            final String content = new String(Util.getInputStreamAsCharArray(url.openConnection().getInputStream(), -1, "UTF-8")); //$NON-NLS-1$
            final File cache = def.getCacheFile();
            final FileOutputStream fos = new FileOutputStream(cache);
            fos.write(content.getBytes());
            fos.close();
            ((ArchetypeRegistryDefinition)def).setFailure(false);
            ((ArchetypeRegistryDefinition)def).setLastCached(System.currentTimeMillis());
        } catch (IOException ex) {
            ((ArchetypeRegistryDefinition)def).setFailure(true);
        }
        save();
    }
    
    @Override
    public void removeCache(IArchetypeRegistryDefinition def) throws CoreException {
        ((ArchetypeRegistryDefinition)def).setLastCached(-1);
        final File cache = def.getCacheFile();
        if (cache.exists()) {
            cache.delete();
        }
        save();
    }
    
    @Override
    public void remove(IArchetypeRegistryDefinition def) throws CoreException {
        this.definitions.remove(def);
        final File cache = def.getCacheFile();
        if (cache.exists()) {
            cache.delete();
        }
        save();
    }
    
    @Override
    public IArchetypeRegistryDefinition create(String title, String url, int timeoutInMinutes) throws CoreException {
        this.lastId++;
        final IArchetypeRegistryDefinition def = new ArchetypeRegistryDefinition(String.valueOf(this.lastId), title, url, timeoutInMinutes);
        this.definitions.add(def);
        save();
        return def;
    }
    
    @Override
    public Iterable<IArchetypeRegistryDefinition> getDefinitions() {
        return new ArrayList<IArchetypeRegistryDefinition>(this.definitions);
    }
    
}
