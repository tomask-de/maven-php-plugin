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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.maven.archetype.catalog.Archetype;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.m2e.core.internal.archetype.ArchetypeCatalogFactory;
import org.phpmaven.eclipse.core.MavenPhpUtils;
import org.phpmaven.eclipse.core.PhpmavenCorePlugin;
import org.phpmaven.eclipse.core.archetype.IArchetype;
import org.phpmaven.eclipse.core.archetype.IArchetypeRegistry;
import org.phpmaven.eclipse.core.archetype.IArchetypeRegistryDefinition;
import org.phpmaven.eclipse.core.archetype.IArchetypeRegistrySettings;

/**
 * The archetype registry implementation.
 * 
 * @author mepeisen
 */
@SuppressWarnings("restriction")
public class ArchetypeRegistry implements IArchetypeRegistry {
    
    /**
     * The known archetypes.
     */
    private List<IArchetype> archetypes;
    
    /**
     * Constructor.
     */
    @SuppressWarnings("unchecked")
    public ArchetypeRegistry() {
        // add at least the dummy archetype.
        this.archetypes = new ArrayList<IArchetype>();
        this.archetypes.add(new DummyArchetype());
        final IArchetypeRegistrySettings settings = PhpmavenCorePlugin.getArchetypeRegistrySettings();
        for (final IArchetypeRegistryDefinition def : settings.getDefinitions()) {
            try {
                if (MavenPhpUtils.isOffline()) {
                    if (!def.hasCache() && def.isSystem()) {
                        // initial use the local registry
                        final File cacheFile = def.getCacheFile();
                        final FileOutputStream fos = new FileOutputStream(cacheFile);
                        Util.copy(ArchetypeRegistry.class.getResource("archetype-catalog.xml").openStream(), fos); //$NON-NLS-1$
                        fos.close();
                    }
                }
                else if (!def.hasCache() || ((def.getLastCached() + def.getTimeoutInMinutes() * 60000) < System.currentTimeMillis())) {
                    // update cache
                    try {
                        settings.doCache(def);
                    } catch (CoreException ex) {
                        if (def.isSystem() && !def.hasCache()) {
                            // initial use the local registry
                            final File cacheFile = def.getCacheFile();
                            final FileOutputStream fos = new FileOutputStream(cacheFile);
                            Util.copy(ArchetypeRegistry.class.getResource("archetype-catalog.xml").openStream(), fos); //$NON-NLS-1$
                            fos.close();
                        }
                        PhpmavenCorePlugin.logError("Error reading archetypes", ex); //$NON-NLS-1$
                    }
                }
                
                if (def.hasCache()) {
                    final ArchetypeCatalogFactory factory = new ArchetypeCatalogFactory.LocalCatalogFactory(
                            def.getCacheFile().getAbsolutePath(), def.getTitle(), false);
                    final List<Archetype> atypes = factory.getArchetypeCatalog().getArchetypes();
                    for (final Archetype at : atypes) {
                        this.archetypes.add(new GenericArchetype(at));
                    }
                }
            }
            catch (CoreException ex) {
                PhpmavenCorePlugin.logError("Error reading archetypes", ex); //$NON-NLS-1$
            }
            catch (IOException ex) {
                PhpmavenCorePlugin.logError("Error reading archetypes", ex); //$NON-NLS-1$
            }
        }
    }
    
    /**
     * @see org.phpmaven.eclipse.core.archetype.IArchetypeRegistry#getArchetypes()
     */
    @Override
    public Iterable<IArchetype> getArchetypes() {
        return Collections.unmodifiableList(this.archetypes);
    }
    
}
