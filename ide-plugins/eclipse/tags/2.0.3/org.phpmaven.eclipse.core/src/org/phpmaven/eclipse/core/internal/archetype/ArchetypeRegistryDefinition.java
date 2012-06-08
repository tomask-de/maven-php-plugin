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

import org.phpmaven.eclipse.core.PhpmavenCorePlugin;
import org.phpmaven.eclipse.core.archetype.IArchetypeRegistryDefinition;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author mepeisen
 *
 */
public class ArchetypeRegistryDefinition implements IArchetypeRegistryDefinition {
    
    /**
     * The url.
     */
    private String url;
    
    /**
     * The title.
     */
    private String title;
    
    /**
     * The id.
     */
    private String id;
    
    /**
     * The timeout in minutes.
     */
    private int timeoutInMinutes;
    
    /**
     * true for failures.
     */
    private boolean failure;
    
    /**
     * true for offline.
     */
    private boolean offline;
    
    /**
     * timestamp for the last cache.
     */
    private long lastCached;
    
    /**
     * Constructor to read from xml
     * @param n
     */
    public ArchetypeRegistryDefinition(Node n) {
        final NodeList children = n.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            final Node child = children.item(i);
            if (child.getNodeType() != Node.ELEMENT_NODE) continue;
            if (ArchetypeRegistrySettings.XML_ENTRY_CACHE_TIMEOUT.equals(child.getNodeName())) {
                this.timeoutInMinutes = Integer.parseInt(child.getTextContent());
            }
            else if (ArchetypeRegistrySettings.XML_ENTRY_FAILURE.equals(child.getNodeName())) {
                this.failure = "true".equalsIgnoreCase(child.getTextContent()); //$NON-NLS-1$
            }
            else if (ArchetypeRegistrySettings.XML_ENTRY_ID.equals(child.getNodeName())) {
                this.id = child.getTextContent();
            }
            else if (ArchetypeRegistrySettings.XML_ENTRY_LASTCACHED.equals(child.getNodeName())) {
                this.lastCached = Long.parseLong(child.getTextContent());
            }
            else if (ArchetypeRegistrySettings.XML_ENTRY_OFFLINE.equals(child.getNodeName())) {
                this.offline = "true".equalsIgnoreCase(child.getTextContent()); //$NON-NLS-1$
            }
            else if (ArchetypeRegistrySettings.XML_ENTRY_TITLE.equals(child.getNodeName())) {
                this.title = child.getTextContent();
            }
            else if (ArchetypeRegistrySettings.XML_ENTRY_URL.equals(child.getNodeName())) {
                this.url = child.getTextContent();
            }
        }
    }

    /**
     * @param id
     * @param title
     * @param url
     * @param timeoutInMinutes
     */
    public ArchetypeRegistryDefinition(String id, String title, String url, int timeoutInMinutes) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.timeoutInMinutes = timeoutInMinutes;
        this.failure = false;
        this.offline = false;
        this.lastCached = -1;
    }

    @Override
    public String getUrl() {
        return this.url;
    }
    
    @Override
    public void setUrl(String url) {
        this.url = url;
    }
    
    @Override
    public String getTitle() {
        return this.title;
    }
    
    @Override
    public void setTitle(String title) {
        this.title = title;
    }
    
    @Override
    public boolean isSystem() {
        return false;
    }
    
    @Override
    public int getTimeoutInMinutes() {
        return this.timeoutInMinutes;
    }
    
    @Override
    public void setTimeoutInMinutes(int minutes) {
        this.timeoutInMinutes = minutes;
    }
    
    @Override
    public boolean wasFailure() {
        return this.failure;
    }
    
    @Override
    public boolean hasCache() {
        return this.getCacheFile().exists();
    }
    
    @Override
    public boolean isOffline() {
        return this.offline;
    }
    
    @Override
    public void setOffline(boolean offline) {
        this.offline = offline;
    }

    /**
     * @param b
     */
    void setFailure(boolean b) {
        this.failure = b;
    }

    /**
     * @param currentTimeMillis
     */
    void setLastCached(long currentTimeMillis) {
        this.lastCached = currentTimeMillis;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public long getLastCached() {
        return this.lastCached;
    }

    @Override
    public File getCacheFile() {
        return new File(PhpmavenCorePlugin.getDefault().getStateLocation().append("archetype.registry.cache." + this.getId() + ".xml").toOSString());  //$NON-NLS-1$//$NON-NLS-2$
    }
    
}
