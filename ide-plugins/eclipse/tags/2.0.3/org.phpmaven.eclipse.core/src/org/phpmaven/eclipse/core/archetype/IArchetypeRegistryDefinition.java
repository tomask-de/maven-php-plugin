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

package org.phpmaven.eclipse.core.archetype;

import java.io.File;

/**
 * A single definition of archetype registries.
 * 
 * @author mepeisen
 */
public interface IArchetypeRegistryDefinition {
    
    /**
     * Returns the url.
     * @return url.
     */
    String getUrl();
    
    /**
     * Sets the url.
     * @param url url.
     */
    void setUrl(String url);
    
    /**
     * Returns the title.
     * @return title.
     */
    String getTitle();
    
    /**
     * Sets the title.
     * @param title title.
     */
    void setTitle(String title);
    
    /**
     * Returns true if this is a non-mutable system entry.
     * @return true for system entry.
     */
    boolean isSystem();

    /**
     * Returns the timeout to refresh the remote archetypes xml in minutes.
     * @return timeout to refresh the remote archetypes xml in minutes.
     */
    int getTimeoutInMinutes();
    
    /**
     * sets the timeout to refresh the remote archetypes xml in minutes.
     * @param minutes the timeout to refresh the remote archetypes xml in minutes.
     */
    void setTimeoutInMinutes(int minutes);
    
    /**
     * Returns true if this xml was known to fail.
     * @return true on failures.
     */
    boolean wasFailure();
    
    /**
     * Returns true if this entry has a cache.
     * @return true if this has a cache.
     */
    boolean hasCache();
    
    /**
     * Returns true if this is offline.
     * @return offline.
     */
    boolean isOffline();
    
    /**
     * Sets the offline flag.
     * @param offline offline flag.
     */
    void setOffline(boolean offline);

    /**
     * Returns the uniue id used internally.
     * @return unique id
     */
    String getId();

    /**
     * Returns the last cached timestamp
     * @return java timestamp (in milliseconds)
     */
    long getLastCached();
    
    /**
     * Returns the cache file.
     * @return cache file
     */
    File getCacheFile();
    
}
