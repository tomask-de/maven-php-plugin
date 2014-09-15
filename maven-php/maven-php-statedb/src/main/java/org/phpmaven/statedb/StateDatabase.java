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
package org.phpmaven.statedb;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Configuration;
import org.phpmaven.core.ConfigurationParameter;

/**
 * Implementation of the persistent state database.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.1
 */
@Component(role = IStateDatabase.class, instantiationStrategy = "singleton")
public class StateDatabase implements IStateDatabase {
    
    /**
     * The database file.
     */
    @ConfigurationParameter(name = "dbfile", expression = "${project.build.directory}/phpmaven.state.db")
    private File dbfile;
    
    /**
     * The database instance
     */
    private Db database;

    @Override
    public File getDbfile() {
        return this.dbfile;
    }
    
    /**
     * Ensures initialization.
     * @throws IOException
     * @throws ClassNotFoundException 
     */
    private void init() throws IOException, ClassNotFoundException {
        if (this.database == null) {
            if (this.dbfile.exists()) {
                final FileInputStream in = new FileInputStream(this.dbfile);
                final ObjectInputStream ois = new ObjectInputStream(
                        new BufferedInputStream(
                                in));
                this.database = (Db) ois.readObject();
                ois.close();
                in.close();
            } else {
                this.database = new Db();
            }
        }
    }
    
    @Override
    public <T extends Serializable> T get(String groupId, String artifact, String key, Class<T> clazz) {
        try {
            this.init();
            final Map<String, Map<String, byte[]>> groupMap = this.database.getDb().get(groupId);
            if (groupMap != null) {
                final Map<String, byte[]> artifactMap = groupMap.get(artifact);
                if (artifactMap != null) {
                    final byte[] data = artifactMap.get(key);
                    if (data != null) {
                        final ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
                        return clazz.cast(ois.readObject());
                    }
                }
            }
            return null;
        } catch (Exception ex) {
            throw new IllegalStateException("Error while accessing persistent state database", ex);
        }
    }
    
    @Override
    public void set(String groupId, String artifactId, String key, Serializable data) {
        try {
            this.init();
            Map<String, Map<String, byte[]>> groupMap = this.database.getDb().get(groupId);
            if (groupMap == null) {
                groupMap = new HashMap<String, Map<String,byte[]>>();
                this.database.getDb().put(groupId, groupMap);
            }
            Map<String, byte[]> artifactMap = groupMap.get(artifactId);
            if (artifactMap == null) {
                artifactMap = new HashMap<String, byte[]>();
                groupMap.put(artifactId, artifactMap);
            }
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(data);
            oos.flush();
            oos.close();
            artifactMap.put(key, baos.toByteArray());
            
            save();
        } catch (Exception ex) {
            throw new IllegalStateException("Error while accessing persistent state database", ex);
        }
    }

    @Override
    public void delete() {
        this.database = null;
        if (this.dbfile.exists()) {
            this.dbfile.delete();
        }
    }

    @Override
    public void reload() {
        this.database = null;
    }

    @Override
    public void remove(String groupId, String artifactId, String key) {
        try {
            this.init();
            Map<String, Map<String, byte[]>> groupMap = this.database.getDb().get(groupId);
            if (groupMap != null) {
                final Map<String, byte[]> artifactMap = groupMap.get(artifactId);
                if (artifactMap != null) {
                    final byte[] data = artifactMap.get(key);
                    if (data != null) {
                        artifactMap.remove(key);
                        save();
                    }
                }
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Error while accessing persistent state database", ex);
        }
    }

    private void save() throws IOException, FileNotFoundException {
        if (!this.dbfile.getParentFile().exists()) {
            this.dbfile.getParentFile().mkdirs();
        }
        if (!this.dbfile.exists()) {
            this.dbfile.createNewFile();
        }
        final FileOutputStream fos = new FileOutputStream(this.dbfile);
        final ObjectOutputStream foos = new ObjectOutputStream(new BufferedOutputStream(fos));
        foos.writeObject(this.database);
        foos.flush();
        foos.close();
        fos.close();
    }
    
}
